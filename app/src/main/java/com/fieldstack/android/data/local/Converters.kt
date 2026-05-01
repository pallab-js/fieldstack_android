package com.fieldstack.android.data.local

import androidx.room.TypeConverter
import com.fieldstack.android.domain.model.ReportCategory
import com.fieldstack.android.domain.model.SyncStatus
import com.fieldstack.android.domain.model.TaskPriority
import com.fieldstack.android.domain.model.TaskStatus
import java.time.Instant

class Converters {
    @TypeConverter fun fromInstant(v: Instant): Long = v.toEpochMilli()
    @TypeConverter fun toInstant(v: Long): Instant = Instant.ofEpochMilli(v)

    @TypeConverter fun fromTaskStatus(v: TaskStatus): String = v.name
    @TypeConverter fun toTaskStatus(v: String): TaskStatus = TaskStatus.valueOf(v)

    @TypeConverter fun fromTaskPriority(v: TaskPriority): String = v.name
    @TypeConverter fun toTaskPriority(v: String): TaskPriority = TaskPriority.valueOf(v)

    @TypeConverter fun fromReportCategory(v: ReportCategory): String = v.name
    @TypeConverter fun toReportCategory(v: String): ReportCategory = ReportCategory.valueOf(v)

    @TypeConverter fun fromSyncStatus(v: SyncStatus): String = v.name
    @TypeConverter fun toSyncStatus(v: String): SyncStatus = SyncStatus.valueOf(v)

    @TypeConverter fun fromStringList(v: List<String>): String = v.joinToString("|")
    @TypeConverter fun toStringList(v: String): List<String> =
        if (v.isEmpty()) emptyList() else v.split("|")
}
