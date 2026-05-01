package com.fieldstack.android.ui.tasks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldstack.android.domain.model.Task
import com.fieldstack.android.domain.model.TaskStatus
import com.fieldstack.android.domain.usecase.GetTaskByIdUseCase
import com.fieldstack.android.domain.usecase.UpdateTaskStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    savedState: SavedStateHandle,
    getTaskById: GetTaskByIdUseCase,
    private val updateStatus: UpdateTaskStatusUseCase,
) : ViewModel() {

    private val taskId: String = checkNotNull(savedState["taskId"])

    val task = getTaskById(taskId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun markComplete() = viewModelScope.launch {
        updateStatus(taskId, TaskStatus.Completed)
    }

    fun markInProgress() = viewModelScope.launch {
        updateStatus(taskId, TaskStatus.InProgress)
    }
}
