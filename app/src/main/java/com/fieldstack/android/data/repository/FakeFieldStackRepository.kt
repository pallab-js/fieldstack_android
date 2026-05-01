package com.fieldstack.android.data.repository

import com.fieldstack.android.domain.model.Report
import com.fieldstack.android.domain.model.SyncQueueItem
import com.fieldstack.android.domain.model.SyncStatus
import com.fieldstack.android.domain.model.Task
import com.fieldstack.android.domain.model.TaskStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeFieldStackRepository @Inject constructor() : FieldStackRepository {

    private val tasks  = MutableStateFlow(FakeData.tasks)
    private val reports = MutableStateFlow(FakeData.reports)
    private val queue  = MutableStateFlow<List<SyncQueueItem>>(emptyList())
    private val syncState = MutableStateFlow<SyncState>(SyncState.Synced)
    private val online = MutableStateFlow(true)

    // ── Tasks ──────────────────────────────────────────────────────────────

    override fun observeTasks(userId: String): Flow<List<Task>> =
        tasks.map { list -> list.filter { it.assigneeId == userId } }

    override fun observeTaskById(id: String): Flow<Task?> =
        tasks.map { list -> list.find { it.id == id } }

    override suspend fun saveTask(task: Task) {
        tasks.update { list ->
            val idx = list.indexOfFirst { it.id == task.id }
            if (idx >= 0) list.toMutableList().also { it[idx] = task }
            else list + task
        }
    }

    override suspend fun updateTaskStatus(taskId: String, status: TaskStatus) {
        tasks.update { list ->
            list.map { if (it.id == taskId) it.copy(status = status, updatedAt = Instant.now()) else it }
        }
    }

    // ── Reports ────────────────────────────────────────────────────────────

    override fun observeReportsByTask(taskId: String): Flow<List<Report>> =
        reports.map { list -> list.filter { it.taskId == taskId } }

    override suspend fun saveReport(report: Report): Long {
        reports.update { it + report }
        enqueue(report.id, "report", "create")
        return reports.value.size.toLong()
    }

    // ── Sync ───────────────────────────────────────────────────────────────

    override fun observeSyncQueue(): Flow<List<SyncQueueItem>> = queue

    override fun observeSyncState(): Flow<SyncState> = syncState

    override fun isOnline(): Flow<Boolean> = online

    override suspend fun syncPendingChanges(): SyncState {
        val pending = queue.value.filter { it.status == SyncStatus.Pending }
        if (pending.isEmpty()) return SyncState.Synced.also { syncState.value = it }

        syncState.value = SyncState.Syncing
        delay(800) // simulate network round-trip

        queue.update { list ->
            list.map { if (it.status == SyncStatus.Pending) it.copy(status = SyncStatus.Synced) else it }
        }
        return SyncState.Synced.also { syncState.value = it }
    }

    // ── Internal ───────────────────────────────────────────────────────────

    private fun enqueue(entityId: String, entityType: String, operation: String) {
        val item = SyncQueueItem(
            id = UUID.randomUUID().toString(),
            entityType = entityType,
            entityId = entityId,
            operation = operation,
            createdAt = Instant.now(),
            status = SyncStatus.Pending,
        )
        queue.update { it + item }
        syncState.value = SyncState.Pending(queue.value.count { it.status == SyncStatus.Pending })
    }
}
