package com.fieldstack.android.data.repository

import com.fieldstack.android.domain.model.SyncStatus
import com.fieldstack.android.domain.model.Task
import com.fieldstack.android.domain.model.TaskPriority
import com.fieldstack.android.domain.model.TaskStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class ConflictResolutionTest {

    private fun task(id: String = "t1", updatedAt: Instant) = Task(
        id = id, title = "T", description = "", location = "L",
        assigneeId = "u1", priority = TaskPriority.Medium,
        status = TaskStatus.NotStarted,
        dueAt = Instant.now(), createdAt = Instant.now(),
        updatedAt = updatedAt, syncStatus = SyncStatus.Synced,
    )

    // ── ConflictResolutionStrategy ────────────────────────────────────────

    @Test
    fun `remote wins when remote is newer`() {
        val local  = task(updatedAt = Instant.ofEpochMilli(1000))
        val remote = task(updatedAt = Instant.ofEpochMilli(2000))
        val result = ConflictResolutionStrategy.resolveTask(local, remote)
        assertEquals(remote, result.winner)
        assertTrue(result.localOverwritten)
    }

    @Test
    fun `local wins when local is newer`() {
        val local  = task(updatedAt = Instant.ofEpochMilli(3000))
        val remote = task(updatedAt = Instant.ofEpochMilli(1000))
        val result = ConflictResolutionStrategy.resolveTask(local, remote)
        assertEquals(local, result.winner)
        assertTrue(!result.localOverwritten)
    }

    @Test
    fun `local wins on tie`() {
        val ts = Instant.ofEpochMilli(1000)
        val result = ConflictResolutionStrategy.resolveTask(task(updatedAt = ts), task(updatedAt = ts))
        assertTrue(!result.localOverwritten)
    }

    // ── MergeStrategy ─────────────────────────────────────────────────────

    @Test
    fun `new remote record is always accepted`() {
        val remote = task("new-id", Instant.ofEpochMilli(1000))
        val result = MergeStrategy.mergeTasks(emptyMap(), listOf(remote))
        assertEquals(1, result.toUpsert.size)
        assertEquals("new-id", result.toUpsert[0].id)
        assertTrue(result.conflicts.isEmpty())
    }

    @Test
    fun `remote newer than local creates conflict and upserts`() {
        val local  = task("t1", Instant.ofEpochMilli(1000))
        val remote = task("t1", Instant.ofEpochMilli(2000))
        val result = MergeStrategy.mergeTasks(mapOf("t1" to local), listOf(remote))
        assertEquals(1, result.toUpsert.size)
        assertEquals(1, result.conflicts.size)
    }

    @Test
    fun `local newer than remote produces no upsert`() {
        val local  = task("t1", Instant.ofEpochMilli(3000))
        val remote = task("t1", Instant.ofEpochMilli(1000))
        val result = MergeStrategy.mergeTasks(mapOf("t1" to local), listOf(remote))
        assertTrue(result.toUpsert.isEmpty())
    }
}
