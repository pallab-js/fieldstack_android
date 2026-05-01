package com.fieldstack.android.data

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.fieldstack.android.data.local.FieldStackDatabase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseMigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        FieldStackDatabase::class.java,
    )

    @Test
    fun migrate2To3_addsCustomFieldsColumn() {
        helper.createDatabase(TEST_DB, 2).apply {
            // Insert a row in the v2 schema (no customFields column yet)
            execSQL(
                """INSERT INTO reports
                   (id, taskId, title, category, details, photoUris,
                    latitude, longitude, signatureUri, createdAt, updatedAt, syncStatus)
                   VALUES ('r1','t1','T','General','D','[]',NULL,NULL,NULL,0,0,'Pending')"""
            )
            close()
        }

        helper.runMigrationsAndValidate(
            TEST_DB, 3, true,
            FieldStackDatabase.MIGRATION_2_3,
        ).use { db ->
            val cursor = db.query("SELECT customFields FROM reports WHERE id='r1'")
            assert(cursor.moveToFirst())
            assert(cursor.getString(0) == "[]") {
                "customFields default should be '[]', got: ${cursor.getString(0)}"
            }
        }
    }

    @Test
    fun migrate3To4_createsIndexesAndUniqueConstraint() {
        helper.createDatabase(TEST_DB, 3).apply {
            // Seed a duplicate sync_queue entry to verify dedup logic
            execSQL(
                """INSERT INTO sync_queue (id, entityType, entityId, operation, createdAt, status, retryCount)
                   VALUES ('q1','report','e1','create',0,'Pending',0)"""
            )
            execSQL(
                """INSERT INTO sync_queue (id, entityType, entityId, operation, createdAt, status, retryCount)
                   VALUES ('q2','report','e1','create',1,'Pending',0)"""
            )
            close()
        }

        helper.runMigrationsAndValidate(
            TEST_DB, 4, true,
            FieldStackDatabase.MIGRATION_3_4,
        ).use { db ->
            // Only one row should remain after dedup
            val cursor = db.query(
                "SELECT COUNT(*) FROM sync_queue WHERE entityId='e1' AND operation='create'"
            )
            assert(cursor.moveToFirst())
            assert(cursor.getInt(0) == 1) {
                "Expected 1 row after dedup, got ${cursor.getInt(0)}"
            }
        }
    }

    @Test
    fun migrateAll_2To4() {
        helper.createDatabase(TEST_DB, 2).close()
        helper.runMigrationsAndValidate(
            TEST_DB, 4, true,
            FieldStackDatabase.MIGRATION_2_3,
            FieldStackDatabase.MIGRATION_3_4,
        ).close()
    }

    companion object {
        private const val TEST_DB = "migration-test"
    }
}
