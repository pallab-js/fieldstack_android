package com.fieldstack.android.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TaskDto(
    val id: String,
    val title: String,
    val description: String,
    val location: String,
    @Json(name = "assignee_id") val assigneeId: String,
    val priority: String,
    val status: String,
    @Json(name = "due_at") val dueAt: Long,       // epoch millis
    @Json(name = "created_at") val createdAt: Long,
    @Json(name = "updated_at") val updatedAt: Long,
)

@JsonClass(generateAdapter = true)
data class ReportDto(
    val id: String,
    @Json(name = "task_id") val taskId: String,
    val title: String,
    val category: String,
    val details: String,
    @Json(name = "photo_uris") val photoUris: List<String> = emptyList(),
    val latitude: Double? = null,
    val longitude: Double? = null,
    @Json(name = "signature_uri") val signatureUri: String? = null,
    @Json(name = "created_at") val createdAt: Long,
    @Json(name = "updated_at") val updatedAt: Long,
    @Json(name = "custom_fields") val customFields: List<CustomFieldDto> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class CustomFieldDto(
    val id: String,
    val label: String,
    val type: String,
    val value: String = "",
    val required: Boolean = false,
)

@JsonClass(generateAdapter = true)
data class LoginRequest(val email: String, val password: String)

@JsonClass(generateAdapter = true)
data class LoginResponse(
    val token: String,
    @Json(name = "user_id") val userId: String,
    val name: String,
    val email: String = "",
    val role: String = "FieldTech",
)

@JsonClass(generateAdapter = true)
data class SubmitReportResponse(@Json(name = "server_id") val serverId: String)

@JsonClass(generateAdapter = true)
data class ApiError(val message: String, val code: Int = 0)

@JsonClass(generateAdapter = true)
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
)

@JsonClass(generateAdapter = true)
data class RoleUpdateRequest(val role: String)
