package com.fieldstack.android.domain.model

import java.time.Instant

enum class TaskStatus { NotStarted, InProgress, Completed, Cancelled }
enum class TaskPriority { Low, Medium, High }
enum class ReportCategory { Inspection, Maintenance, Incident, General }
enum class SyncStatus { Pending, Syncing, Synced, Failed }

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val location: String,
    val assigneeId: String,
    val priority: TaskPriority,
    val status: TaskStatus,
    val dueAt: Instant,
    val createdAt: Instant,
    val updatedAt: Instant,
    val syncStatus: SyncStatus = SyncStatus.Synced,
)

data class Report(
    val id: String,
    val taskId: String,
    val title: String,
    val category: ReportCategory,
    val details: String,
    val photoUris: List<String> = emptyList(),
    val latitude: Double? = null,
    val longitude: Double? = null,
    val signatureUri: String? = null,
    val createdAt: Instant,
    val updatedAt: Instant,
    val syncStatus: SyncStatus = SyncStatus.Pending,
    val customFields: List<CustomField> = emptyList(),
)

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
)

data class Comment(
    val id: String,
    val taskId: String,
    val authorId: String,
    val authorName: String,
    val body: String,           // raw text; @mentions parsed on display
    val createdAt: Instant,
    val syncStatus: SyncStatus = SyncStatus.Pending,
)

enum class UserRole { FieldTech, Supervisor, Admin }

data class SyncQueueItem(
    val id: String,
    val entityType: String,   // "task" | "report"
    val entityId: String,
    val operation: String,    // "create" | "update" | "delete"
    val createdAt: Instant,
    val status: SyncStatus = SyncStatus.Pending,
    val retryCount: Int = 0,
)

// ── Custom Fields ──────────────────────────────────────────────────────────
enum class CustomFieldType { Text, Number, Checkbox, Date }

data class CustomField(
    val id: String,
    val label: String,
    val type: CustomFieldType,
    val value: String = "",   // all values stored as String; parsed on read
    val required: Boolean = false,
)
