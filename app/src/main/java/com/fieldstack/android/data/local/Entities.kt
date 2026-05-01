package com.fieldstack.android.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fieldstack.android.domain.model.ReportCategory
import com.fieldstack.android.domain.model.SyncStatus
import com.fieldstack.android.domain.model.TaskPriority
import com.fieldstack.android.domain.model.TaskStatus
import java.time.Instant

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val location: String,
    val assigneeId: String,
    val priority: TaskPriority,
    val status: TaskStatus,
    val dueAt: Instant,
    val createdAt: Instant,
    val updatedAt: Instant,
    val syncStatus: SyncStatus,
)

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey val id: String,
    val taskId: String,
    val title: String,
    val category: ReportCategory,
    val details: String,
    val photoUris: List<String>,
    val latitude: Double?,
    val longitude: Double?,
    val signatureUri: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val syncStatus: SyncStatus,
    val customFields: List<com.fieldstack.android.domain.model.CustomField> = emptyList(),
)

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey val id: String,
    val entityType: String,
    val entityId: String,
    val operation: String,
    val createdAt: Instant,
    val status: SyncStatus,
    val retryCount: Int,
)
