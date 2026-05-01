package com.fieldstack.android.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {
    @Query("SELECT * FROM reports WHERE taskId = :taskId ORDER BY createdAt DESC")
    fun observeByTask(taskId: String): Flow<List<ReportEntity>>

    @Query("SELECT * FROM reports WHERE syncStatus = 'Pending' ORDER BY createdAt ASC")
    fun observePending(): Flow<List<ReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(report: ReportEntity): Long

    @Update
    suspend fun update(report: ReportEntity)

    @Query("UPDATE reports SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: String)

    @Query("DELETE FROM reports WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface SyncQueueDao {
    @Query("SELECT * FROM sync_queue WHERE status = 'Pending' ORDER BY createdAt ASC")
    fun observePending(): Flow<List<SyncQueueEntity>>

    @Query("SELECT * FROM sync_queue WHERE status = 'Pending' ORDER BY createdAt ASC")
    suspend fun getPending(): List<SyncQueueEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueue(item: SyncQueueEntity)

    @Query("UPDATE sync_queue SET status = 'Synced' WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("UPDATE sync_queue SET status = 'Failed', retryCount = retryCount + 1 WHERE id = :id")
    suspend fun markFailed(id: String)

    @Query("DELETE FROM sync_queue WHERE status = 'Synced'")
    suspend fun clearSynced()
}
