package com.fieldstack.android.data.repository

import app.cash.turbine.test
import com.fieldstack.android.domain.model.ReportCategory
import com.fieldstack.android.domain.model.SyncStatus
import com.fieldstack.android.domain.model.TaskStatus
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

class FakeRepositoryTest {

    private lateinit var repo: FakeFieldStackRepository

    @Before
    fun setup() { repo = FakeFieldStackRepository() }

    @Test
    fun `observeTasks filters by userId`() = runTest {
        repo.observeTasks(FakeData.USER_ID).test {
            val tasks = awaitItem()
            assertEquals(FakeData.tasks.size, tasks.size)
            assertTrue(tasks.all { it.assigneeId == FakeData.USER_ID })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observeTasks returns empty for unknown user`() = runTest {
        repo.observeTasks("unknown").test {
            assertEquals(0, awaitItem().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateTaskStatus reflects in flow`() = runTest {
        repo.updateTaskStatus("t1", TaskStatus.Completed)
        repo.observeTaskById("t1").test {
            assertEquals(TaskStatus.Completed, awaitItem()?.status)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveReport enqueues sync item`() = runTest {
        val report = sampleReport()
        repo.saveReport(report)
        repo.observeSyncQueue().test {
            val queue = awaitItem()
            assertTrue(queue.any { it.entityId == report.id && it.status == SyncStatus.Pending })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `syncPendingChanges clears pending queue`() = runTest {
        repo.saveReport(sampleReport())
        val result = repo.syncPendingChanges()
        assertEquals(SyncState.Synced, result)
        repo.observeSyncQueue().test {
            assertTrue(awaitItem().none { it.status == SyncStatus.Pending })
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun sampleReport() = com.fieldstack.android.domain.model.Report(
        id = "r-test", taskId = "t1", title = "Test Report",
        category = ReportCategory.Inspection, details = "Test",
        createdAt = Instant.now(), updatedAt = Instant.now(),
    )
}
