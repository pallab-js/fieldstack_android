package com.fieldstack.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [TaskEntity::class, ReportEntity::class, SyncQueueEntity::class, CommentEntity::class],
    version = 2,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class FieldStackDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun reportDao(): ReportDao
    abstract fun syncQueueDao(): SyncQueueDao
    abstract fun commentDao(): CommentDao
}
