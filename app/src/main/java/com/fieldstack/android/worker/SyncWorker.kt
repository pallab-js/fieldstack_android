package com.fieldstack.android.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fieldstack.android.data.repository.FieldStackRepository
import com.fieldstack.android.data.repository.SyncState
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: FieldStackRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            when (repository.syncPendingChanges()) {
                is SyncState.Synced -> Result.success()
                is SyncState.Error  -> if (runAttemptCount < MAX_RETRIES) Result.retry()
                                       else Result.failure()
                else                -> Result.success()
            }
        } catch (e: Exception) {
            if (runAttemptCount < MAX_RETRIES) Result.retry() else Result.failure()
        }
    }

    companion object {
        const val MAX_RETRIES = 3
        const val WORK_NAME   = "fieldstack_sync"
    }
}
