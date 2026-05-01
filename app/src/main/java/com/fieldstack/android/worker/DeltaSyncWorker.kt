package com.fieldstack.android.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fieldstack.android.data.local.TaskDao
import com.fieldstack.android.data.local.toEntity
import com.fieldstack.android.data.remote.FieldStackApi
import com.fieldstack.android.data.remote.toDomain
import com.fieldstack.android.data.repository.FakeData
import com.fieldstack.android.data.repository.MergeStrategy
import com.fieldstack.android.data.repository.FieldStackRepository
import com.fieldstack.android.util.AppPrefsStore
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
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val since = prefs.lastSyncTimestamp.first() ?: 0L
            val remoteDelta = api.getTasksDelta(FakeData.USER_ID, since).map { it.toDomain() }

            if (remoteDelta.isNotEmpty()) {
                val localMap = taskDao.observeAll().first()
                    .associate { it.id to com.fieldstack.android.data.local.toDomain(it) }
                val mergeResult = MergeStrategy.mergeTasks(localMap, remoteDelta)
                mergeResult.toUpsert.forEach { taskDao.upsert(it.toEntity()) }
            }

            // Also push pending local changes
            repository.syncPendingChanges()
            prefs.setLastSyncTimestamp(System.currentTimeMillis())
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < SyncWorker.MAX_RETRIES) Result.retry() else Result.failure()
        }
    }

    companion object { const val WORK_NAME = "fieldstack_delta_sync" }
}

// Extension to call toDomain on TaskEntity without import ambiguity
private fun com.fieldstack.android.data.local.toDomain(e: com.fieldstack.android.data.local.TaskEntity) = e.toDomain()
