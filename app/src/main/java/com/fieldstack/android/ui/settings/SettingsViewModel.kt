package com.fieldstack.android.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldstack.android.util.AppPrefsStore
import com.fieldstack.android.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppPrefs(
    val userName: String = "",
    val userEmail: String = "",
    val wifiOnlySync: Boolean = false,
    val biometricEnabled: Boolean = false,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val session: SessionManager,
    private val store: AppPrefsStore,
) : ViewModel() {

    val prefs = combine(store.wifiOnlySync, store.biometricEnabled) { wifi, bio ->
        AppPrefs(
            userName        = session.userName ?: "",
            userEmail       = session.userId  ?: "",
            wifiOnlySync    = wifi,
            biometricEnabled = bio,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppPrefs())

    val currentRole get() = session.userRole

    fun setWifiOnly(v: Boolean)  = viewModelScope.launch { store.setWifiOnly(v) }
    fun setBiometric(v: Boolean) = viewModelScope.launch { store.setBiometric(v) }
    fun logout() = session.clear()
}
