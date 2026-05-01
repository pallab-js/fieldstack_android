package com.fieldstack.android.data.local

import com.fieldstack.android.domain.model.Report
import com.fieldstack.android.domain.model.SyncQueueItem
import com.fieldstack.android.domain.model.Task

fun TaskEntity.toDomain() = Task(
    id, title, description, location, assigneeId,
    priority, status, dueAt, createdAt, updatedAt, syncStatus,
)

fun Task.toEntity() = TaskEntity(
    id, title, description, location, assigneeId,
    priority, status, dueAt, createdAt, updatedAt, syncStatus,
)

fun ReportEntity.toDomain() = Report(
    id, taskId, title, category, details,
    photoUris, latitude, longitude, signatureUri,
    createdAt, updatedAt, syncStatus,
)

fun Report.toEntity() = ReportEntity(
    id, taskId, title, category, details,
    photoUris, latitude, longitude, signatureUri,
    createdAt, updatedAt, syncStatus,
)

fun SyncQueueEntity.toDomain() = SyncQueueItem(
    id, entityType, entityId, operation, createdAt, status, retryCount,
)

fun SyncQueueItem.toEntity() = SyncQueueEntity(
    id, entityType, entityId, operation, createdAt, status, retryCount,
)
