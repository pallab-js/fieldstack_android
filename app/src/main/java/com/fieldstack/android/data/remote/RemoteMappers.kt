package com.fieldstack.android.data.remote

import com.fieldstack.android.domain.model.Report
import com.fieldstack.android.domain.model.ReportCategory
import com.fieldstack.android.domain.model.SyncStatus
import com.fieldstack.android.domain.model.Task
import com.fieldstack.android.domain.model.TaskPriority
import com.fieldstack.android.domain.model.TaskStatus
import java.time.Instant

fun TaskDto.toDomain() = Task(
    id = id, title = title, description = description,
    location = location, assigneeId = assigneeId,
    priority = runCatching { TaskPriority.valueOf(priority.replaceFirstChar { it.uppercase() }) }
        .getOrDefault(TaskPriority.Medium),
    status = runCatching { TaskStatus.valueOf(status.replaceFirstChar { it.uppercase() }) }
        .getOrDefault(TaskStatus.NotStarted),
    dueAt = Instant.ofEpochMilli(dueAt),
    createdAt = Instant.ofEpochMilli(createdAt),
    updatedAt = Instant.ofEpochMilli(updatedAt),
    syncStatus = SyncStatus.Synced,
)

fun ReportDto.toDomain() = Report(
    id = id, taskId = taskId, title = title,
    category = runCatching { ReportCategory.valueOf(category.replaceFirstChar { it.uppercase() }) }
        .getOrDefault(ReportCategory.General),
    details = details, photoUris = photoUris,
    latitude = latitude, longitude = longitude, signatureUri = signatureUri,
    createdAt = Instant.ofEpochMilli(createdAt),
    updatedAt = Instant.ofEpochMilli(updatedAt),
    syncStatus = SyncStatus.Synced,
    customFields = customFields.map { it.toDomain() },
)

fun Report.toDto() = ReportDto(
    id = id, taskId = taskId, title = title,
    category = category.name.lowercase(),
    details = details, photoUris = photoUris,
    latitude = latitude, longitude = longitude, signatureUri = signatureUri,
    createdAt = createdAt.toEpochMilli(),
    updatedAt = updatedAt.toEpochMilli(),
    customFields = customFields.map { it.toDto() },
)

private fun CustomFieldDto.toDomain() = com.fieldstack.android.domain.model.CustomField(
    id = id, label = label,
    type = runCatching { com.fieldstack.android.domain.model.CustomFieldType.valueOf(type) }
        .getOrDefault(com.fieldstack.android.domain.model.CustomFieldType.Text),
    value = value, required = required,
)

private fun com.fieldstack.android.domain.model.CustomField.toDto() = CustomFieldDto(
    id = id, label = label, type = type.name, value = value, required = required,
)
