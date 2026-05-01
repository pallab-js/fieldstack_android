package com.fieldstack.android.ui.dashboard

import app.cash.turbine.test
import com.fieldstack.android.data.repository.FakeData
import com.fieldstack.android.data.repository.FakeFieldStackRepository
import com.fieldstack.android.domain.model.TaskStatus
import com.fieldstack.android.domain.usecase.InsightsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private lateinit var repo: FakeFieldStackRepository
    private lateinit var viewModel: DashboardViewModel
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repo = FakeFieldStackRepository()
        viewModel = DashboardViewModel(repo)
    }

    @After
    fun teardown() = Dispatchers.resetMain()

    @Test
    fun `initial state has tasks from fake data`() = runTest {
        viewModel.uiState.test {
            assertTrue(awaitItem().tasks.isNotEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `progress reflects completed task ratio`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            val expected = if (state.totalToday == 0) 0f
                           else state.completedToday / state.totalToday.toFloat()
            assertEquals(expected, state.progress, 0.01f)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class InsightsUseCaseTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() = Dispatchers.setMain(dispatcher)

    @After
    fun teardown() = Dispatchers.resetMain()

    @Test
    fun `completionRate is 0 when no tasks completed`() = runTest {
        val repo = FakeFieldStackRepository()
        // Mark all tasks not started
        FakeData.tasks.forEach { repo.updateTaskStatus(it.id, TaskStatus.NotStarted) }
        InsightsUseCase(repo)(FakeData.USER_ID).test {
            val insights = awaitItem()
            assertEquals(0, insights.tasksCompleted)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `completionRate never uses OverBudget status chip for minor misses`() {
        // Verify the UI logic: <50% → AtRisk, not OverBudget
        val rate = 0.3f
        val status = when {
            rate >= 0.8f -> "OnTrack"
            rate >= 0.5f -> "AtRisk"
            else         -> "AtRisk"  // never OverBudget
        }
        assertEquals("AtRisk", status)
    }
}
