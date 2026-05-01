package com.fieldstack.android.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldstack.android.data.remote.FieldStackApi
import com.fieldstack.android.data.remote.LoginRequest
import com.fieldstack.android.domain.model.UserRole
import com.fieldstack.android.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
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
    private val api: FieldStackApi,
) : ViewModel() {

    private val _state = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val state = _state.asStateFlow()

    val isLoggedIn: Boolean get() = session.isLoggedIn
    val userRole: UserRole get() = session.userRole

    // Brute-force lockout state (in-memory; resets on process death which is acceptable)
    private var failedAttempts = 0
    private var lockedUntil = 0L

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = LoginUiState.Error("Email and password are required")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _state.value = LoginUiState.Error("Enter a valid email address")
            return
        }
        val remaining = lockedUntil - System.currentTimeMillis()
        if (remaining > 0) {
            _state.value = LoginUiState.Error("Too many attempts. Try again in ${remaining / 1000}s.")
            return
        }
        viewModelScope.launch {
            _state.value = LoginUiState.Loading
            try {
                val response = api.login(LoginRequest(email, password))
                session.token    = response.token
                session.userId   = response.userId
                session.userName = response.name
                session.userRole = runCatching { UserRole.valueOf(response.role) }
                    .getOrDefault(UserRole.FieldTech)
                failedAttempts = 0
                _state.value = LoginUiState.Success
            } catch (e: HttpException) {
                recordFailure()
                _state.value = LoginUiState.Error(
                    if (e.code() == 401) "Invalid email or password" else "Login failed. Please try again."
                )
            } catch (e: Exception) {
                _state.value = LoginUiState.Error("Network error. Check your connection.")
            }
        }
    }

    fun logout() {
        session.clear()
        _state.value = LoginUiState.Idle
    }

    private fun recordFailure() {
        failedAttempts++
        if (failedAttempts >= 5) {
            lockedUntil = System.currentTimeMillis() + 30_000L
            failedAttempts = 0
        }
    }
}
