package com.fieldstack.android.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldstack.android.domain.model.Task
import com.fieldstack.android.domain.model.TaskStatus
import com.fieldstack.android.domain.usecase.GetTasksUseCase
import com.fieldstack.android.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

enum class TaskFilter { All, Today, Pending, Completed }

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TaskListViewModel @Inject constructor(
    getTasks: GetTasksUseCase,
    session: SessionManager,
) : ViewModel() {

    val filter = MutableStateFlow(TaskFilter.All)
    val query  = MutableStateFlow("")

    private val allTasks = session.userId?.let { getTasks(it) } ?: flowOf(emptyList())

    val tasks = combine(allTasks, filter, query) { list, f, q ->
        list
            .filter { task ->
                when (f) {
                    TaskFilter.All       -> true
                    TaskFilter.Today     -> task.status != TaskStatus.Completed
                    TaskFilter.Pending   -> task.status == TaskStatus.NotStarted
                    TaskFilter.Completed -> task.status == TaskStatus.Completed
                }
            }
            .filter { task ->
                q.isBlank() || task.title.contains(q, ignoreCase = true) ||
                        task.location.contains(q, ignoreCase = true)
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
