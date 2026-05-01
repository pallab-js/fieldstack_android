package com.fieldstack.android.ui.components

import androidx.compose.runtime.Composable
import com.fieldstack.android.domain.model.UserRole

/**
 * Renders [content] only when the current user's role is in [allowedRoles].
 * Use this to hide/show UI elements based on role without scattering role checks.
 */
@Composable
fun PermissionGuard(
    currentRole: UserRole,
    allowedRoles: Set<UserRole>,
    content: @Composable () -> Unit,
) {
    if (currentRole in allowedRoles) content()
}

// Convenience aliases
@Composable
fun SupervisorOnly(currentRole: UserRole, content: @Composable () -> Unit) =
    PermissionGuard(currentRole, setOf(UserRole.Supervisor, UserRole.Admin), content)

@Composable
fun AdminOnly(currentRole: UserRole, content: @Composable () -> Unit) =
    PermissionGuard(currentRole, setOf(UserRole.Admin), content)
