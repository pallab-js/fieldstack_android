package com.fieldstack.android.data.repository

import com.fieldstack.android.domain.model.Report
import com.fieldstack.android.domain.model.SyncQueueItem
import com.fieldstack.android.domain.model.Task
import kotlinx.coroutines.flow.Flow

sealed interface SyncState {
    data object Idle : SyncState
    data object Syncing : SyncState
    data object Synced : SyncState
    data class Error(val message: String) : SyncState
    data class Pending(val count: Int) : SyncState
}

sealed interface SyncResult {
    data object Success : SyncResult
    data class Partial(val synced: Int, val failed: Int) : SyncResult
    data class Failure(val message: String) : SyncResult
}

interface FieldStackRepository {
    // Tasks
    fun observeTasks(userId: String): Flow<List<Task>>
    fun observeTaskById(id: String): Flow<Task?>
    suspend fun saveTask(task: Task)
    suspend fun updateTaskStatus(taskId: String, status: com.fieldstack.android.domain.model.TaskStatus)

    // Reports
    fun observeReportsByTask(taskId: String): Flow<List<Report>>
    suspend fun saveReport(report: Report): Long

    // Sync
    fun observeSyncQueue(): Flow<List<SyncQueueItem>>
    fun observeSyncState(): Flow<SyncState>
    fun isOnline(): Flow<Boolean>
    suspend fun syncPendingChanges(): SyncState
}
