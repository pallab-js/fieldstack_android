package com.fieldstack.android.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fieldstack.android.domain.model.Task
import com.fieldstack.android.domain.model.TaskStatus
import com.fieldstack.android.ui.components.AppTopBar
import com.fieldstack.android.ui.components.SyncBadge
import com.fieldstack.android.ui.components.ZenButton
import com.fieldstack.android.ui.components.ZenButtonVariant
import com.fieldstack.android.ui.components.ZenCard
import com.fieldstack.android.ui.components.ZenCardVariant
import com.fieldstack.android.ui.components.ZenProgressBar
import com.fieldstack.android.ui.components.ZenStatus
import com.fieldstack.android.ui.components.ZenStatusChip
import com.fieldstack.android.ui.components.SupervisorOnlyimport com.fieldstack.android.ui.theme.Mint
import com.fieldstack.android.ui.theme.Spacing
import com.fieldstack.android.ui.theme.Stone
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val TIME_FMT = DateTimeFormatter.ofPattern("h:mm a").withZone(ZoneId.systemDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onTaskClick: (String) -> Unit = {},
    onNewReport: () -> Unit = {},
    onViewInsights: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showFabSheet by remember { mutableStateOf(false) }
    var showCelebration by remember { mutableStateOf(false) }

    // Milestone celebration at 100%
    LaunchedEffect(state.progress) {
        if (state.progress >= 1f && state.totalToday > 0) showCelebration = true
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Dashboard",
                syncBadge = { SyncBadge(state.syncBadge) },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showFabSheet = true },
                shape = CircleShape,
                containerColor = Mint,
                modifier = Modifier.semantics { contentDescription = "Quick add" },
            ) {
                Icon(Icons.Default.Add, "Quick add", tint = androidx.compose.ui.graphics.Color.White)
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            item {
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    "👋 Hi, ${state.userName}",
                    style = MaterialTheme.typography.headlineLarge,
                )
                Spacer(Modifier.height(Spacing.sm))
            }

            // Progress card
            item {
                ZenCard(variant = ZenCardVariant.Elevated, modifier = Modifier.fillMaxWidth()) {
                    Text("🎯 Today's Progress",
                        style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(Spacing.xs))
                    ZenProgressBar(
                        progress = state.progress,
                        modifier = Modifier.fillMaxWidth(),
                        onMilestone = { /* celebration handled above */ },
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "${state.completedToday}/${state.totalToday} tasks completed",
                        style = MaterialTheme.typography.bodySmall,
                        color = Stone,
                    )
                    AnimatedVisibility(visible = showCelebration, enter = fadeIn(), exit = fadeOut()) {
                        Text(
                            "✨ All done — great work today!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Mint,
                        )
                    }
                }
            }

            // Weekly insights card
            item {
                val insightsVm: InsightsViewModel = hiltViewModel()
                val insights by insightsVm.insights.collectAsStateWithLifecycle()
                WeeklySummaryCard(insights)
                ZenButton(
                    text = "View Details →",
                    onClick = onViewInsights,
                    variant = ZenButtonVariant.Ghost,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // Supervisor-only team progress card
            item {
                SupervisorOnly(viewModel.currentRole) {
                    ZenCard(modifier = Modifier.fillMaxWidth()) {
                        Text("👥 Team Progress",
                            style = MaterialTheme.typography.headlineMedium)
                        Spacer(Modifier.height(6.dp))
                        Text("${state.completedToday} tasks completed across team today",
                            style = MaterialTheme.typography.bodySmall,
                            color = Stone)
                    }
                }
            }

            // Task summary header
            item {
                Text("📋 Your Tasks",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(top = Spacing.xs))
            }
            // Top 3 tasks
            items(state.tasks.take(3), key = { it.id }) { task ->
                DashboardTaskRow(task = task, onClick = { onTaskClick(task.id) })
            }

            if (state.tasks.size > 3) {
                item {
                    ZenButton(
                        text = "View all ${state.tasks.size} tasks →",
                        onClick = { /* navigate to task list — wired in NavHost */ },
                        variant = ZenButtonVariant.Ghost,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            item { Spacer(Modifier.height(80.dp)) } // FAB clearance
        }
    }

    // Quick-add bottom sheet
    if (showFabSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFabSheet = false },
            sheetState = rememberModalBottomSheetState(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.sm)
                    .padding(bottom = Spacing.xl),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                Text("Quick Add", style = MaterialTheme.typography.headlineMedium)
                ZenButton(
                    text = "+ New Report",
                    onClick = { showFabSheet = false; onNewReport() },
                    modifier = Modifier.fillMaxWidth(),
                )
                ZenButton(
                    text = "+ New Task",
                    onClick = { showFabSheet = false },
                    variant = ZenButtonVariant.Secondary,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun DashboardTaskRow(task: Task, onClick: () -> Unit) {
    ZenCard(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Task: ${task.title}" },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(task.title, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null,
                        tint = Stone, modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(3.dp))
                    Text(task.location,
                        style = MaterialTheme.typography.bodySmall, color = Stone)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.Schedule, null,
                        tint = Stone, modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(3.dp))
                    Text(TIME_FMT.format(task.dueAt),
                        style = MaterialTheme.typography.bodySmall, color = Stone)
                }
            }
            ZenStatusChip(
                when (task.status) {
                    TaskStatus.Completed  -> ZenStatus.OnTrack
                    TaskStatus.InProgress -> ZenStatus.AtRisk
                    else                  -> ZenStatus.OnTrack
                }
            )
        }
    }
}
