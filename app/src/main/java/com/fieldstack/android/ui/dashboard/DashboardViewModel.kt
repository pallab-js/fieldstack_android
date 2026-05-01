package com.fieldstack.android.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldstack.android.data.repository.FakeData
import com.fieldstack.android.data.repository.FieldStackRepository
import com.fieldstack.android.data.repository.SyncState
import com.fieldstack.android.domain.model.Task
import com.fieldstack.android.domain.model.TaskStatus
import com.fieldstack.android.ui.components.SyncBadgeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class DashboardUiState(
    val userName: String = "Alex",
    val tasks: List<Task> = emptyList(),
    val syncBadge: SyncBadgeState = SyncBadgeState.Synced,
    val isOnline: Boolean = true,
) {
    val totalToday: Int get() = tasks.size
    val completedToday: Int get() = tasks.count { it.status == TaskStatus.Completed }
    val progress: Float get() = if (totalToday == 0) 0f else completedToday / totalToday.toFloat()
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: FieldStackRepository,
    private val session: com.fieldstack.android.util.SessionManager,
) : ViewModel() {

    val currentRole get() = session.userRole

    val uiState = combine(
        repository.observeTasks(FakeData.USER_ID),
        repository.observeSyncState(),
        repository.isOnline(),
    ) { tasks, sync, online ->
        DashboardUiState(
            tasks = tasks,
            syncBadge = when {
                !online            -> SyncBadgeState.Offline
                sync is SyncState.Pending -> SyncBadgeState.Pending(sync.count)
                else               -> SyncBadgeState.Synced
            },
            isOnline = online,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())
}
