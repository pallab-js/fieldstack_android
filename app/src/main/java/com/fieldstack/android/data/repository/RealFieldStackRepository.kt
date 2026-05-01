package com.fieldstack.android.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.room.Transaction
import com.fieldstack.android.data.local.ReportDao
import com.fieldstack.android.data.local.SyncQueueDao
import com.fieldstack.android.data.local.SyncQueueEntity
import com.fieldstack.android.data.local.TaskDao
import com.fieldstack.android.data.local.toDomain
import com.fieldstack.android.data.local.toEntity
import com.fieldstack.android.data.remote.FieldStackApi
import com.fieldstack.android.data.remote.toDomain
import com.fieldstack.android.data.remote.toDto
import com.fieldstack.android.domain.model.Report
import com.fieldstack.android.domain.model.SyncQueueItem
import com.fieldstack.android.domain.model.SyncStatus
import com.fieldstack.android.domain.model.Task
import com.fieldstack.android.domain.model.TaskStatus
import com.fieldstack.android.domain.model.UserRole
import com.fieldstack.android.util.SessionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.io.File
import java.net.URI
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private const val MAX_ITEM_RETRIES = 5

@Singleton
class RealFieldStackRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val taskDao: TaskDao,
    private val reportDao: ReportDao,
    private val syncQueueDao: SyncQueueDao,
    private val api: FieldStackApi,
    private val session: SessionManager,
) : FieldStackRepository {

    private val syncState = MutableStateFlow<SyncState>(SyncState.Idle)

    // ── Connectivity ───────────────────────────────────────────────────────

    override fun isOnline(): Flow<Boolean> = callbackFlow {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                trySend(caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
            }
            override fun onLost(network: Network) { trySend(false) }
        }
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        cm.registerNetworkCallback(request, callback)
        val active = cm.activeNetwork?.let { cm.getNetworkCapabilities(it) }
        trySend(active?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true)
        awaitClose { cm.unregisterNetworkCallback(callback) }
    }

    // ── Tasks ──────────────────────────────────────────────────────────────

    override fun observeTasks(userId: String): Flow<List<Task>> =
        taskDao.observeByUser(userId).map { list -> list.map { it.toDomain() } }

    override fun observeTaskById(id: String): Flow<Task?> =
        taskDao.observeById(id).map { entity ->
            entity?.toDomain()?.takeIf { task ->
                session.userRole != UserRole.FieldTech || task.assigneeId == session.userId
            }
        }

    override suspend fun saveTask(task: Task) = taskDao.upsert(task.toEntity())

    /** Single UPDATE — avoids the read-then-write TOCTOU race. */
    override suspend fun updateTaskStatus(taskId: String, status: TaskStatus) {
        taskDao.updateStatus(taskId, status, Instant.now())
    }

    // ── Reports ────────────────────────────────────────────────────────────

    override fun observeReportsByTask(taskId: String): Flow<List<Report>> =
        reportDao.observeByTask(taskId).map { list -> list.map { it.toDomain() } }

    /** Atomically persists the report and enqueues it for sync. */
    @Transaction
    override suspend fun saveReport(report: Report): Long {
        val id = reportDao.upsert(report.toEntity())
        syncQueueDao.enqueue(
            SyncQueueEntity(
                id = UUID.randomUUID().toString(),
                entityType = "report", entityId = report.id,
                operation = "create", createdAt = Instant.now(),
                status = SyncStatus.Pending, retryCount = 0,
            )
        )
        syncState.update { SyncState.Pending(syncQueueDao.getPending().size) }
        return id
    }

    // ── Sync ───────────────────────────────────────────────────────────────

    override fun observeSyncQueue(): Flow<List<SyncQueueItem>> =
        syncQueueDao.observePending().map { list -> list.map { it.toDomain() } }

    override fun observeSyncState(): Flow<SyncState> = syncState

    override suspend fun syncPendingChanges(): SyncState {
        // Only retry items that haven't exceeded the per-item cap
        val pending = syncQueueDao.getPending().filter { it.retryCount < MAX_ITEM_RETRIES }
        if (pending.isEmpty()) return SyncState.Synced.also { syncState.value = it }

        syncState.value = SyncState.Syncing
        var failed = 0
        pending.forEach { item ->
            try {
                when (item.entityType) {
                    "report" -> {
                        val entity = reportDao.getById(item.entityId)
                            ?: error("Report ${item.entityId} not found")
                        api.submitReport(entity.toDomain().toDto())
                        reportDao.updateSyncStatus(item.entityId, SyncStatus.Synced.name)
                        // Clean up local photo files after successful sync
                        entity.photoUris.forEach { uriStr ->
                            try {
                                val file = when {
                                    uriStr.startsWith("file://") -> File(URI.create(uriStr))
                                    uriStr.startsWith("/")       -> File(uriStr)
                                    else                         -> null
                                }
                                file?.takeIf { it.exists() }?.delete()
                            } catch (_: Exception) { /* best-effort */ }
                        }
                    }
                }
                syncQueueDao.markSynced(item.id)
            } catch (e: Exception) {
                syncQueueDao.markFailed(item.id)
                failed++
            }
        }
        return if (failed == 0) {
            SyncState.Synced.also { syncState.value = it }
        } else {
            SyncState.Error("$failed item(s) failed to sync").also { syncState.value = it }
        }
    }
}
