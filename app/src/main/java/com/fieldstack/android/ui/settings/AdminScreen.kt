package com.fieldstack.android.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.fieldstack.android.ui.components.ZenCard
import com.fieldstack.android.ui.theme.Spacing
import com.fieldstack.android.ui.theme.Stone

@Composable
fun AdminScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text("Admin Console", style = MaterialTheme.typography.headlineLarge)
        Text("Manage users, roles, and organization settings",
            style = MaterialTheme.typography.bodySmall, color = Stone)

        ZenCard(modifier = Modifier.fillMaxWidth()) {
            Text("User Management", style = MaterialTheme.typography.headlineMedium)
            Text("Full user management coming in a future release.",
                style = MaterialTheme.typography.bodySmall, color = Stone)
        }
        ZenCard(modifier = Modifier.fillMaxWidth()) {
            Text("Audit Logs", style = MaterialTheme.typography.headlineMedium)
            Text("Audit log viewer coming in a future release.",
                style = MaterialTheme.typography.bodySmall, color = Stone)
        }
    }
}
