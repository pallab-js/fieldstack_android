package com.fieldstack.android.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldstack.android.data.remote.FieldStackApi
import com.fieldstack.android.data.remote.RoleUpdateRequest
import com.fieldstack.android.domain.model.User
import com.fieldstack.android.domain.model.UserRole
import com.fieldstack.android.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminUiState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val api: FieldStackApi,
    private val session: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState = _uiState.asStateFlow()

    init { loadUsers() }

    private fun loadUsers() = viewModelScope.launch {
        // Client-side guard: prevents accidental UI exposure, but is NOT a security boundary.
        // The server must independently enforce Admin role on GET /admin/users and PUT /admin/users/{id}/role.
        if (session.userRole != UserRole.Admin) {
            _uiState.update { it.copy(error = "Access denied") }
            return@launch
        }
        _uiState.update { it.copy(isLoading = true, error = null) }
        try {
            val users = api.getUsers().map { dto ->
                User(
                    id = dto.id, name = dto.name, email = dto.email,
                    role = runCatching { UserRole.valueOf(dto.role) }.getOrDefault(UserRole.FieldTech),
                )
            }
            _uiState.update { it.copy(users = users, isLoading = false) }
        } catch (e: retrofit2.HttpException) {
            val msg = if (e.code() == 403) "Access denied — Admin role required" else (e.message ?: "Failed to load users")
            _uiState.update { it.copy(isLoading = false, error = msg) }
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, error = e.message ?: "Failed to load users") }
        }
    }

    fun assignRole(userId: String, role: UserRole) = viewModelScope.launch {
        // Client-side guard only — server enforces this independently.
        if (session.userRole != UserRole.Admin) {
            _uiState.update { it.copy(error = "Access denied") }
            return@launch
        }
        try {
            val updated = api.updateUserRole(userId, RoleUpdateRequest(role.name))
            _uiState.update { state ->
                state.copy(users = state.users.map { u ->
                    if (u.id == updated.id) u.copy(
                        role = runCatching { UserRole.valueOf(updated.role) }.getOrDefault(u.role)
                    ) else u
                })
            }
        } catch (e: retrofit2.HttpException) {
            val msg = if (e.code() == 403) "Access denied — Admin role required" else (e.message ?: "Failed to update role")
            _uiState.update { it.copy(error = msg) }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message ?: "Failed to update role") }
        }
    }
}
