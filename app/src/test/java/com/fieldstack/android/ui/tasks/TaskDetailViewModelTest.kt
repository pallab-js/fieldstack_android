package com.fieldstack.android.ui.tasks

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.fieldstack.android.data.repository.FakeFieldStackRepository
import com.fieldstack.android.domain.model.TaskStatus
import com.fieldstack.android.domain.usecase.GetTaskByIdUseCase
import com.fieldstack.android.domain.usecase.UpdateTaskStatusUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskDetailViewModelTest {

    private lateinit var repo: FakeFieldStackRepository
    private lateinit var viewModel: TaskDetailViewModel
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repo = FakeFieldStackRepository()
        viewModel = TaskDetailViewModel(
            savedState = SavedStateHandle(mapOf("taskId" to "t1")),
            getTaskById = GetTaskByIdUseCase(repo),
            updateStatus = UpdateTaskStatusUseCase(repo),
        )
    }

    @After
    fun teardown() = Dispatchers.resetMain()

    @Test
    fun `task is loaded by id`() = runTest {
        viewModel.task.test {
            val task = awaitItem()
            assertNotNull(task)
            assertEquals("t1", task?.id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `markComplete updates task status`() = runTest {
        viewModel.markComplete()
        advanceUntilIdle()
        viewModel.task.test {
            assertEquals(TaskStatus.Completed, awaitItem()?.status)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `markInProgress updates task status`() = runTest {
        viewModel.markInProgress()
        advanceUntilIdle()
        viewModel.task.test {
            assertEquals(TaskStatus.InProgress, awaitItem()?.status)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
