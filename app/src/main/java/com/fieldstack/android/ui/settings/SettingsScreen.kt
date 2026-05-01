package com.fieldstack.android.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fieldstack.android.ui.components.ZenButton
import com.fieldstack.android.ui.components.ZenButtonVariant
import com.fieldstack.android.ui.components.ZenCard
import com.fieldstack.android.ui.components.AdminOnly
import com.fieldstack.android.ui.theme.Mint
import com.fieldstack.android.ui.theme.Spacing
import com.fieldstack.android.ui.theme.Stone

@Composable
fun SettingsScreen(
    onLogout: () -> Unit = {},
    onAdminConsole: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val prefs by viewModel.prefs.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        // Profile
        ZenCard(modifier = Modifier.fillMaxWidth()) {
            Text("Profile", style = MaterialTheme.typography.labelMedium, color = Stone)
            Spacer(Modifier.height(6.dp))
            HorizontalDivider()
            Spacer(Modifier.height(6.dp))
            Text(prefs.userName.ifBlank { "Field Worker" },
                style = MaterialTheme.typography.bodyMedium)
            Text(prefs.userEmail.ifBlank { "—" },
                style = MaterialTheme.typography.bodySmall, color = Stone)
        }

        // Sync preferences
        ZenCard(modifier = Modifier.fillMaxWidth()) {
            Text("Sync", style = MaterialTheme.typography.labelMedium, color = Stone)
            Spacer(Modifier.height(6.dp))
            HorizontalDivider()
            Spacer(Modifier.height(6.dp))
            SettingsToggle(
                label = "Wi-Fi only sync",
                description = "Only sync when connected to Wi-Fi",
                checked = prefs.wifiOnlySync,
                onCheckedChange = viewModel::setWifiOnly,
            )
        }

        // Security
        ZenCard(modifier = Modifier.fillMaxWidth()) {
            Text("Security", style = MaterialTheme.typography.labelMedium, color = Stone)
            Spacer(Modifier.height(6.dp))
            HorizontalDivider()
            Spacer(Modifier.height(6.dp))
            SettingsToggle(
                label = "Biometric unlock",
                description = "Use fingerprint or face to unlock",
                checked = prefs.biometricEnabled,
                onCheckedChange = viewModel::setBiometric,
            )
        }

        // Data
        ZenCard(modifier = Modifier.fillMaxWidth()) {
            Text("Data & Privacy", style = MaterialTheme.typography.labelMedium, color = Stone)
            Spacer(Modifier.height(6.dp))
            HorizontalDivider()
            Spacer(Modifier.height(6.dp))
            ZenButton(
                text = "Export Data",
                onClick = { /* Phase 2 */ },
                variant = ZenButtonVariant.Secondary,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // About
        ZenCard(modifier = Modifier.fillMaxWidth()) {
            Text("About", style = MaterialTheme.typography.labelMedium, color = Stone)
            Spacer(Modifier.height(6.dp))
            HorizontalDivider()
            Spacer(Modifier.height(6.dp))
            Text("FieldStack Android", style = MaterialTheme.typography.bodyMedium)
            Text("Version 0.1.0-mvp · BudgetZen Design",
                style = MaterialTheme.typography.bodySmall, color = Stone)
        }

        Spacer(Modifier.height(Spacing.xs))
        AdminOnly(viewModel.currentRole) {
            ZenButton(
                text = "Admin Console",
                onClick = onAdminConsole,
                variant = ZenButtonVariant.Secondary,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        ZenButton(
            text = "Sign Out",
            onClick = { viewModel.logout(); onLogout() },
            variant = ZenButtonVariant.Destructive,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(Spacing.xl))
    }
}

@Composable
private fun SettingsToggle(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "$label: ${if (checked) "on" else "off"}" },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(description, style = MaterialTheme.typography.bodySmall, color = Stone)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Mint, checkedTrackColor = Mint.copy(alpha = 0.4f)),
        )
    }
}
