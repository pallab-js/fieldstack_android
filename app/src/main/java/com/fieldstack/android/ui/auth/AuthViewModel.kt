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

    /** Call on app resume to detect a token that expired while the app was backgrounded. */
    fun checkSession() {
        if (session.token != null && !session.isLoggedIn) {
            session.clear()
            _state.value = LoginUiState.Error("Your session has expired. Please log in again.")
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = LoginUiState.Error("Email and password are required")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _state.value = LoginUiState.Error("Enter a valid email address")
            return
        }
        // Fix #6: read lockout from persisted storage — survives process death
        val remaining = session.lockedUntilMs - System.currentTimeMillis()
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
                session.userEmail = response.email
                // Fix #6: clear lockout on successful login
                session.failedLoginAttempts = 0
                session.lockedUntilMs = 0L
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
        val attempts = session.failedLoginAttempts + 1
        if (attempts >= 5) {
            session.lockedUntilMs = System.currentTimeMillis() + 30_000L
            session.failedLoginAttempts = 0
        } else {
            session.failedLoginAttempts = attempts
        }
    }
}
