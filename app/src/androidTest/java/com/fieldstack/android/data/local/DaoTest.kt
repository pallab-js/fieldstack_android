package com.fieldstack.android.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.fieldstack.android.domain.model.ReportCategory
import com.fieldstack.android.domain.model.SyncStatus
import com.fieldstack.android.domain.model.TaskPriority
import com.fieldstack.android.domain.model.TaskStatus
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant

@RunWith(AndroidJUnit4::class)
class DaoTest {

    private lateinit var db: FieldStackDatabase
    private lateinit var taskDao: TaskDao
    private lateinit var reportDao: ReportDao
    private lateinit var syncQueueDao: SyncQueueDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FieldStackDatabase::class.java,
        ).allowMainThreadQueries().build()
        taskDao = db.taskDao()
        reportDao = db.reportDao()
        syncQueueDao = db.syncQueueDao()
    }

    @After
    fun teardown() = db.close()

    // ── Task ──────────────────────────────────────────────────────────────

    @Test
    fun `upsert and observe task`() = runTest {
        taskDao.upsert(sampleTask("t1"))
        taskDao.observeById("t1").test {
            assertEquals("t1", awaitItem()?.id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `delete task removes it`() = runTest {
        taskDao.upsert(sampleTask("t2"))
        taskDao.deleteById("t2")
        taskDao.observeById("t2").test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getPending returns only pending tasks`() = runTest {
        taskDao.upsert(sampleTask("t3", SyncStatus.Pending))
        taskDao.upsert(sampleTask("t4", SyncStatus.Synced))
        val pending = taskDao.getPending()
        assertEquals(1, pending.size)
        assertEquals("t3", pending[0].id)
    }

    // ── Report ────────────────────────────────────────────────────────────

    @Test
    fun `upsert report and observe pending`() = runTest {
        reportDao.upsert(sampleReport("r1", "t1"))
        reportDao.observePending().test {
            assertEquals(1, awaitItem().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateSyncStatus changes report status`() = runTest {
        reportDao.upsert(sampleReport("r2", "t1"))
        reportDao.updateSyncStatus("r2", SyncStatus.Synced.name)
        reportDao.observePending().test {
            assertEquals(0, awaitItem().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ── SyncQueue ─────────────────────────────────────────────────────────

    @Test
    fun `enqueue and markSynced`() = runTest {
        syncQueueDao.enqueue(sampleQueueItem("q1"))
        syncQueueDao.markSynced("q1")
        assertEquals(0, syncQueueDao.getPending().size)
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private fun sampleTask(id: String, sync: SyncStatus = SyncStatus.Synced) = TaskEntity(
        id = id, title = "Task $id", description = "", location = "Site A",
        assigneeId = "user1", priority = TaskPriority.Medium, status = TaskStatus.NotStarted,
        dueAt = Instant.now(), createdAt = Instant.now(), updatedAt = Instant.now(),
        syncStatus = sync,
    )

    private fun sampleReport(id: String, taskId: String) = ReportEntity(
        id = id, taskId = taskId, title = "Report $id",
        category = ReportCategory.Inspection, details = "Details",
        photoUris = emptyList(), latitude = null, longitude = null, signatureUri = null,
        createdAt = Instant.now(), updatedAt = Instant.now(), syncStatus = SyncStatus.Pending,
    )

    private fun sampleQueueItem(id: String) = SyncQueueEntity(
        id = id, entityType = "report", entityId = "r1",
        operation = "create", createdAt = Instant.now(),
        status = SyncStatus.Pending, retryCount = 0,
    )
}
