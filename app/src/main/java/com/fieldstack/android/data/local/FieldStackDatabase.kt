package com.fieldstack.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [TaskEntity::class, ReportEntity::class, SyncQueueEntity::class, CommentEntity::class],
    version = 4,
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

        /** Adds indexes for query performance and a unique constraint on sync_queue. */
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE INDEX IF NOT EXISTS index_tasks_assigneeId ON tasks(assigneeId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_tasks_syncStatus ON tasks(syncStatus)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_tasks_dueAt ON tasks(dueAt)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_reports_taskId ON reports(taskId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_reports_syncStatus ON reports(syncStatus)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_sync_queue_status ON sync_queue(status)")
                // Unique index: drop duplicates first (keep lowest rowid), then create index
                db.execSQL("""
                    DELETE FROM sync_queue WHERE rowid NOT IN (
                        SELECT MIN(rowid) FROM sync_queue GROUP BY entityId, operation
                    )
                """.trimIndent())
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_sync_queue_entityId_operation ON sync_queue(entityId, operation)")
            }
        }
    }
}
