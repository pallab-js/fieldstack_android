package com.fieldstack.android.domain.usecase

import com.fieldstack.android.domain.model.SyncStatus
import com.fieldstack.android.domain.model.Task
import com.fieldstack.android.domain.model.TaskPriority
import com.fieldstack.android.domain.model.TaskStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class MergeTasksUseCaseTest {

    private val useCase = MergeTasksUseCase()

    private fun task(id: String = "t1", updatedAt: Instant) = Task(
        id = id, title = "T", description = "", location = "L",
        assigneeId = "u1", priority = TaskPriority.Medium,
        status = TaskStatus.NotStarted,
        dueAt = Instant.now(), createdAt = Instant.now(),
        updatedAt = updatedAt, syncStatus = SyncStatus.Synced,
    )

    @Test
    fun `new remote record is accepted`() {
        val result = useCase(emptyMap(), listOf(task("new", Instant.ofEpochMilli(1000))))
        assertEquals(1, result.toUpsert.size)
        assertEquals("new", result.toUpsert[0].id)
    }

    @Test
    fun `remote newer than local is upserted`() {
        val local = task("t1", Instant.ofEpochMilli(1000))
        val remote = task("t1", Instant.ofEpochMilli(2000))
        val result = useCase(mapOf("t1" to local), listOf(remote))
        assertEquals(1, result.toUpsert.size)
        assertEquals(remote, result.toUpsert[0])
    }

    @Test
    fun `local newer than remote produces no upsert`() {
        val local = task("t1", Instant.ofEpochMilli(3000))
        val remote = task("t1", Instant.ofEpochMilli(1000))
        val result = useCase(mapOf("t1" to local), listOf(remote))
        assertTrue(result.toUpsert.isEmpty())
    }

    @Test
    fun `empty delta produces empty result`() {
        val result = useCase(emptyMap(), emptyList())
        assertTrue(result.toUpsert.isEmpty())
        assertTrue(result.conflicts.isEmpty())
    }
}
