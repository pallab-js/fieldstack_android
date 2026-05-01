package com.fieldstack.android.ui.tasks

import app.cash.turbine.test
import com.fieldstack.android.data.repository.FakeData
import com.fieldstack.android.data.repository.FakeFieldStackRepository
import com.fieldstack.android.domain.model.TaskStatus
import com.fieldstack.android.domain.usecase.GetTasksUseCase
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
class TaskListViewModelTest {

    private lateinit var viewModel: TaskListViewModel
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        val repo = FakeFieldStackRepository()
        viewModel = TaskListViewModel(GetTasksUseCase(repo))
    }

    @After
    fun teardown() = Dispatchers.resetMain()

    @Test
    fun `default filter returns all tasks`() = runTest {
        viewModel.tasks.test {
            val items = awaitItem()
            assertEquals(FakeData.tasks.size, items.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Completed filter returns only completed tasks`() = runTest {
        viewModel.filter.value = TaskFilter.Completed
        viewModel.tasks.test {
            val items = awaitItem()
            assertTrue(items.all { it.status == TaskStatus.Completed })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `search query filters by title`() = runTest {
        viewModel.query.value = "Inspect"
        viewModel.tasks.test {
            val items = awaitItem()
            assertTrue(items.all { it.title.contains("Inspect", ignoreCase = true) })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `search query filters by location`() = runTest {
        viewModel.query.value = "Warehouse"
        viewModel.tasks.test {
            val items = awaitItem()
            assertTrue(items.all { it.location.contains("Warehouse", ignoreCase = true) })
            cancelAndIgnoreRemainingEvents()
        }
    }
}
