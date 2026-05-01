package com.fieldstack.android.data.repository

import com.fieldstack.android.domain.model.Report
import com.fieldstack.android.domain.model.ReportCategory
import com.fieldstack.android.domain.model.SyncStatus
import com.fieldstack.android.domain.model.Task
import com.fieldstack.android.domain.model.TaskPriority
import com.fieldstack.android.domain.model.TaskStatus
import java.time.Instant
import java.time.temporal.ChronoUnit

object FakeData {
    const val USER_ID = "user-alex-01"

    val tasks = listOf(
        task("t1", "Inspect Site A",        "Downtown",  TaskStatus.InProgress,  TaskPriority.High,   hoursFromNow(2)),
        task("t2", "Document Equipment",    "Warehouse", TaskStatus.NotStarted,  TaskPriority.Medium, hoursFromNow(4)),
        task("t3", "Safety Walkthrough",    "Site B",    TaskStatus.NotStarted,  TaskPriority.High,   hoursFromNow(6)),
        task("t4", "Meter Reading — Block C","Uptown",   TaskStatus.Completed,   TaskPriority.Low,    hoursFromNow(-2), SyncStatus.Synced),
        task("t5", "Update Asset Logs",     "HQ",        TaskStatus.Completed,   TaskPriority.Low,    hoursFromNow(-4), SyncStatus.Synced),
    )

    val reports = listOf(
        Report(
            id = "r1", taskId = "t4", title = "Meter Reading Report",
            category = ReportCategory.Inspection, details = "All meters within normal range.",
            createdAt = Instant.now().minus(3, ChronoUnit.HOURS),
            updatedAt = Instant.now().minus(3, ChronoUnit.HOURS),
            syncStatus = SyncStatus.Synced,
        ),
        Report(
            id = "r2", taskId = "t5", title = "Asset Log Update",
            category = ReportCategory.Maintenance, details = "Updated 12 asset records.",
            createdAt = Instant.now().minus(5, ChronoUnit.HOURS),
            updatedAt = Instant.now().minus(5, ChronoUnit.HOURS),
            syncStatus = SyncStatus.Pending,
        ),
    )

    private fun task(
        id: String, title: String, location: String,
        status: TaskStatus, priority: TaskPriority,
        dueAt: Instant, sync: SyncStatus = SyncStatus.Synced,
    ) = Task(
        id = id, title = title, description = "Field task: $title",
        location = location, assigneeId = USER_ID,
        priority = priority, status = status,
        dueAt = dueAt,
        createdAt = Instant.now().minus(1, ChronoUnit.DAYS),
        updatedAt = Instant.now(),
        syncStatus = sync,
    )

    private fun hoursFromNow(h: Long): Instant =
        Instant.now().plus(h, ChronoUnit.HOURS)
}
