package com.fieldstack.android.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FieldStackApi {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("tasks")
    suspend fun getTasks(): List<TaskDto>

    @GET("tasks/{id}")
    suspend fun getTask(@Path("id") id: String): TaskDto

    @PUT("tasks/{id}/status")
    suspend fun updateTaskStatus(
        @Path("id") id: String,
        @Body body: TaskStatusRequest,  // was @Query — status in query params appears in server logs
    ): TaskDto

    @POST("reports")
    suspend fun submitReport(@Body report: ReportDto): SubmitReportResponse

    @GET("reports")
    suspend fun getReports(@Query("task_id") taskId: String): List<ReportDto>

    /** Delta sync — only records updated after [since] epoch millis */
    @GET("tasks/delta")
    suspend fun getTasksDelta(@Query("since") since: Long): List<TaskDto>

    // ── Admin ──────────────────────────────────────────────────────────────

    @GET("admin/users")
    suspend fun getUsers(): List<UserDto>

    @PUT("admin/users/{id}/role")
    suspend fun updateUserRole(
        @Path("id") id: String,
        @Body body: RoleUpdateRequest,
    ): UserDto
}
