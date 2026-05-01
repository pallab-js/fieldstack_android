package com.fieldstack.android.ui.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldstack.android.data.repository.FieldStackRepository
import com.fieldstack.android.data.repository.SyncState
import com.fieldstack.android.util.AppPrefsStore
import com.fieldstack.android.worker.SyncScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val repository: FieldStackRepository,
    private val scheduler: SyncScheduler,
    private val prefs: AppPrefsStore,
) : ViewModel() {

    val syncState = repository.observeSyncState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SyncState.Idle)

    val queue = repository.observeSyncQueue()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val isOnline = repository.isOnline()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    private val _lastSyncedAt = MutableStateFlow<Instant?>(null)
    val lastSyncedAt = _lastSyncedAt

    fun syncNow() = viewModelScope.launch {
        val wifiOnly = prefs.wifiOnlySync.first()
        scheduler.scheduleSync(requireWifi = wifiOnly)
        val result = repository.syncPendingChanges()
        if (result == SyncState.Synced) _lastSyncedAt.value = Instant.now()
    }

    fun deltaSync() = viewModelScope.launch {
        val wifiOnly = prefs.wifiOnlySync.first()
        scheduler.scheduleDeltaSync(requireWifi = wifiOnly)
    }

    fun retryFailed() = syncNow()
}
