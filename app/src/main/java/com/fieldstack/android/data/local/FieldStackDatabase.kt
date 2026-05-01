package com.fieldstack.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [TaskEntity::class, ReportEntity::class, SyncQueueEntity::class, CommentEntity::class],
    version = 3,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class FieldStackDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun reportDao(): ReportDao
    abstract fun syncQueueDao(): SyncQueueDao
    abstract fun commentDao(): CommentDao

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE reports ADD COLUMN customFields TEXT NOT NULL DEFAULT '[]'")
            }
        }
    }
}
