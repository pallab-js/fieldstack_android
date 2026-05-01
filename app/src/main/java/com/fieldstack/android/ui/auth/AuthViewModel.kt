package com.fieldstack.android.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldstack.android.data.repository.FakeData
import com.fieldstack.android.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data object Success : LoginUiState
    data class Error(val message: String) : LoginUiState
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val session: SessionManager,
) : ViewModel() {

    private val _state = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val state = _state.asStateFlow()

    val isLoggedIn: Boolean get() = session.isLoggedIn

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = LoginUiState.Error("Email and password are required")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _state.value = LoginUiState.Error("Enter a valid email address")
            return
        }
        viewModelScope.launch {
            _state.value = LoginUiState.Loading
            delay(600) // simulate auth round-trip
            // Fake auth: any non-empty credentials succeed
            session.token   = "fake-token-${System.currentTimeMillis()}"
            session.userId  = FakeData.USER_ID
            session.userName = email.substringBefore("@").replaceFirstChar { it.uppercase() }
            _state.value = LoginUiState.Success
        }
    }

    fun logout() {
        session.clear()
        _state.value = LoginUiState.Idle
    }
}
