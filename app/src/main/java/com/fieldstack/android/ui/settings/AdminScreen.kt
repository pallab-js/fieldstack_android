package com.fieldstack.android.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fieldstack.android.domain.model.User
import com.fieldstack.android.domain.model.UserRole
import com.fieldstack.android.ui.components.ZenCard
import com.fieldstack.android.ui.theme.Spacing
import com.fieldstack.android.ui.theme.Stone

@Composable
fun AdminScreen(viewModel: AdminViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Client-side role guard
    if (viewModel.session.userRole != UserRole.Admin) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Access Denied — Admin role required.",
                style = MaterialTheme.typography.bodyMedium, color = Stone)
        }
        return
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text("Admin Console", style = MaterialTheme.typography.headlineLarge)
        Text("Manage users and roles",
            style = MaterialTheme.typography.bodySmall, color = Stone)

        state.error?.let { err ->
            Text(err, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error)
        }

        when {
            state.isLoading -> Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            state.users.isEmpty() -> Text("No users found.",
                style = MaterialTheme.typography.bodySmall, color = Stone)
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                items(state.users, key = { it.id }) { user ->
                    UserRow(user = user, onRoleSelected = { viewModel.assignRole(user.id, it) })
                }
            }
        }
    }
}

@Composable
private fun UserRow(user: User, onRoleSelected: (UserRole) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ZenCard(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "User: ${user.name}" },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.weight(1f)) {
                Text(user.name, style = MaterialTheme.typography.bodyMedium)
                Text(user.email, style = MaterialTheme.typography.bodySmall, color = Stone)
            }
            Box {
                TextButton(onClick = { expanded = true }) {
                    Text(user.role.name, style = MaterialTheme.typography.labelMedium)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    UserRole.entries.forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role.name) },
                            onClick = { expanded = false; onRoleSelected(role) },
                        )
                    }
                }
            }
        }
    }
}
