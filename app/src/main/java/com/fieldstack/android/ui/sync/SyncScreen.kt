package com.fieldstack.android.ui.sync

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fieldstack.android.data.repository.SyncState
import com.fieldstack.android.domain.model.SyncQueueItem
import com.fieldstack.android.domain.model.SyncStatus
import com.fieldstack.android.ui.components.TipChip
import com.fieldstack.android.ui.components.ZenButton
import com.fieldstack.android.ui.components.ZenButtonVariant
import com.fieldstack.android.ui.components.ZenCard
import com.fieldstack.android.ui.theme.Error
import com.fieldstack.android.ui.theme.Mint
import com.fieldstack.android.ui.theme.Spacing
import com.fieldstack.android.ui.theme.Stone
import com.fieldstack.android.ui.theme.WarmGray
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val DT_FMT = DateTimeFormatter.ofPattern("MMM d, h:mm a").withZone(ZoneId.systemDefault())

@Composable
fun SyncScreen(viewModel: SyncViewModel = hiltViewModel()) {
    val state  by viewModel.syncState.collectAsStateWithLifecycle()
    val queue  by viewModel.queue.collectAsStateWithLifecycle()
    val online by viewModel.isOnline.collectAsStateWithLifecycle()
    val lastSynced by viewModel.lastSyncedAt.collectAsStateWithLifecycle()

    val pending = queue.filter { it.status == SyncStatus.Pending }
    val failed  = queue.filter { it.status == SyncStatus.Failed }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        // Status card
        ZenCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                when (state) {
                    SyncState.Syncing -> CircularProgressIndicator(
                        modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Mint)
                    SyncState.Synced  -> Icon(Icons.Default.CheckCircle, null,
                        tint = Mint, modifier = Modifier.size(16.dp))
                    is SyncState.Error -> Icon(Icons.Default.ErrorOutline, null,
                        tint = Error, modifier = Modifier.size(16.dp))
                    else -> Icon(Icons.Default.HourglassEmpty, null,
                        tint = WarmGray, modifier = Modifier.size(16.dp))
                }
                Text(
                    text = when (state) {
                        SyncState.Synced  -> "All synced"
                        SyncState.Syncing -> "Syncing…"
                        SyncState.Idle    -> "Idle"
                        is SyncState.Pending -> "${pending.size} pending"
                        is SyncState.Error   -> "Error: ${(state as SyncState.Error).message}"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
            if (lastSynced != null) {
                Spacer(Modifier.height(4.dp))
                Text("Last synced: ${DT_FMT.format(lastSynced)}",
                    style = MaterialTheme.typography.labelSmall, color = Stone)
            }
        }

        if (!online) TipChip("Connect to Wi-Fi for faster sync")

        AnimatedVisibility(visible = failed.isNotEmpty()) {
            TipChip("${failed.size} item(s) failed — tap Retry to try again")
        }
        // Queue
        if (queue.isEmpty()) {
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text("Queue is empty 🎉",
                    style = MaterialTheme.typography.bodyMedium, color = Stone)
            }
        } else {
            Text("Queue (${queue.size})",
                style = MaterialTheme.typography.labelMedium, color = Stone)
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                items(queue, key = { it.id }) { item -> QueueRow(item) }
            }
        }

        HorizontalDivider()
        Spacer(Modifier.height(4.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            if (failed.isNotEmpty()) {
                ZenButton(
                    text = "Retry Failed",
                    onClick = viewModel::retryFailed,
                    variant = ZenButtonVariant.Secondary,
                    modifier = Modifier.weight(1f),
                )
            }
            ZenButton(
                text = if (state == SyncState.Syncing) "Syncing…" else "🔄 Sync Now",
                onClick = viewModel::syncNow,
                enabled = state != SyncState.Syncing && online,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun QueueRow(item: SyncQueueItem) {
    ZenCard(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "${item.entityType} ${item.operation} ${item.status.name}" },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                when (item.status) {
                    SyncStatus.Synced  -> Icons.Default.CheckCircle
                    SyncStatus.Failed  -> Icons.Default.ErrorOutline
                    else               -> Icons.Default.HourglassEmpty
                },
                contentDescription = null,
                tint = when (item.status) {
                    SyncStatus.Synced -> Mint
                    SyncStatus.Failed -> Error
                    else              -> WarmGray
                },
                modifier = Modifier.size(18.dp),
            )
            Column(modifier = Modifier.padding(start = 10.dp).weight(1f)) {
                Text("${item.entityType.replaceFirstChar { it.uppercase() }}: ${item.operation}",
                    style = MaterialTheme.typography.bodySmall)
                Text(DT_FMT.format(item.createdAt),
                    style = MaterialTheme.typography.labelSmall, color = Stone)
                if (item.retryCount > 0) {
                    Text("Retried ${item.retryCount}×",
                        style = MaterialTheme.typography.labelSmall, color = WarmGray)
                }
            }
            Text(item.status.name,
                style = MaterialTheme.typography.labelSmall,
                color = when (item.status) {
                    SyncStatus.Synced -> Mint
                    SyncStatus.Failed -> Error
                    else              -> WarmGray
                })
        }
    }
}
