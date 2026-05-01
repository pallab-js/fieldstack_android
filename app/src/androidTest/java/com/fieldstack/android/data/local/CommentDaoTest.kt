package com.fieldstack.android.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.fieldstack.android.domain.model.SyncStatus
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant

@RunWith(AndroidJUnit4::class)
class CommentDaoTest {

    private lateinit var db: FieldStackDatabase
    private lateinit var dao: CommentDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FieldStackDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = db.commentDao()
    }

    @After
    fun teardown() = db.close()

    @Test
    fun `insert and observe comment`() = runTest {
        dao.insert(comment("c1", "t1"))
        dao.observeByTask("t1").test {
            assertEquals(1, awaitItem().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `delete removes comment`() = runTest {
        dao.insert(comment("c2", "t1"))
        dao.deleteById("c2")
        dao.observeByTask("t1").test {
            assertEquals(0, awaitItem().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `mention parsing extracts @words`() {
        val body = "Hey @Alex, check this out with @Jordan"
        val mentions = Regex("""@\w+""").findAll(body).map { it.value }.toList()
        assertEquals(listOf("@Alex", "@Jordan"), mentions)
    }

    private fun comment(id: String, taskId: String) = CommentEntity(
        id = id, taskId = taskId, authorId = "u1", authorName = "Alex",
        body = "Test comment @Bob", createdAt = Instant.now(),
        syncStatus = SyncStatus.Pending,
    )
}
