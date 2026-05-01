package com.fieldstack.android.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fieldstack.android.data.local.TaskDao
import com.fieldstack.android.data.local.toEntity
import com.fieldstack.android.data.remote.FieldStackApi
import com.fieldstack.android.data.remote.toDomain
import com.fieldstack.android.data.repository.FieldStackRepository
import com.fieldstack.android.domain.usecase.MergeTasksUseCase
import com.fieldstack.android.util.AppPrefsStore
import com.fieldstack.android.util.SessionManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class DeltaSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val api: FieldStackApi,
    private val taskDao: TaskDao,
    private val prefs: AppPrefsStore,
    private val repository: FieldStackRepository,
    private val session: SessionManager,
    private val mergeTasks: MergeTasksUseCase,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val userId = session.userId ?: return Result.failure()
        return try {
            val since = prefs.lastSyncTimestamp.first() ?: 0L
            val remoteDelta = api.getTasksDelta(userId, since).map { it.toDomain() }

            if (remoteDelta.isNotEmpty()) {
                val localMap = taskDao.observeAll().first()
                    .associate { it.id to it.toDomain() }
                val mergeResult = mergeTasks(localMap, remoteDelta)
                mergeResult.toUpsert.forEach { taskDao.upsert(it.toEntity()) }

                // Clamp to [30 days ago, 1 minute from now] to guard against malicious/corrupt server timestamps
                val now = System.currentTimeMillis()
                val serverTs = remoteDelta.maxOf { it.updatedAt.toEpochMilli() }
                    .coerceIn(now - 30L * 24 * 3600 * 1000, now + 60_000L)
                prefs.setLastSyncTimestamp(serverTs)
            }

            repository.syncPendingChanges()
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < SyncWorker.MAX_RETRIES) Result.retry() else Result.failure()
        }
    }

    companion object { const val WORK_NAME = "fieldstack_delta_sync" }
}

private fun com.fieldstack.android.data.local.TaskEntity.toDomain() =
    com.fieldstack.android.data.local.toDomain(this)
