package com.fieldstack.android.ui.tasks

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fieldstack.android.domain.model.Task
import com.fieldstack.android.domain.model.TaskPriority
import com.fieldstack.android.domain.model.TaskStatus
import com.fieldstack.android.ui.components.ZenCard
import com.fieldstack.android.ui.components.ZenFilterChip
import com.fieldstack.android.ui.components.ZenStatus
import com.fieldstack.android.ui.components.ZenStatusChip
import com.fieldstack.android.ui.components.ZenTextField
import com.fieldstack.android.ui.theme.Spacing
import com.fieldstack.android.ui.theme.Stone
import com.fieldstack.android.ui.theme.Warning
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val TIME_FMT = DateTimeFormatter.ofPattern("h:mm a").withZone(ZoneId.systemDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onTaskClick: (String) -> Unit = {},
    viewModel: TaskListViewModel = hiltViewModel(),
) {
    val tasks  by viewModel.tasks.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()
    val query  by viewModel.query.collectAsStateWithLifecycle()

    PullToRefreshBox(
        isRefreshing = false,
        onRefresh = {},
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(Modifier.fillMaxSize()) {
            ZenTextField(
                value = query,
                onValueChange = { viewModel.query.value = it },
                placeholder = "Search tasks…",
                contentDesc = "Search tasks",
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = Spacing.sm),
            ) {
                items(TaskFilter.entries) { f ->
                    ZenFilterChip(
                        label = f.name,
                        selected = filter == f,
                        onClick = { viewModel.filter.value = f },
                    )
                }
            }
            Spacer(Modifier.height(Spacing.xs))
            if (tasks.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Spacing.sm),
                ) {
                    items(tasks, key = { it.id }) { task ->
                        TaskRow(task = task, onClick = { onTaskClick(task.id) })
                    }
                    item { Spacer(Modifier.height(Spacing.sm)) }
                }
            }
        }
    }
}

@Composable
private fun TaskRow(task: Task, onClick: () -> Unit) {
    ZenCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClickLabel = "Open ${task.title}", onClick = onClick)
            .semantics { contentDescription = "Task: ${task.title}, ${task.status.name}" },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(task.title, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material3.Icon(Icons.Default.LocationOn, null,
                        tint = Stone, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(3.dp))
                    Text(task.location, style = MaterialTheme.typography.bodySmall, color = Stone)
                    Spacer(Modifier.width(10.dp))
                    androidx.compose.material3.Icon(Icons.Default.Schedule, null,
                        tint = Stone, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(3.dp))
                    Text(TIME_FMT.format(task.dueAt),
                        style = MaterialTheme.typography.bodySmall, color = Stone)
                }
            }
            Spacer(Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ZenStatusChip(task.status.toZenStatus())
                if (task.priority == TaskPriority.High) {
                    Text("● High", style = MaterialTheme.typography.labelSmall, color = Warning)
                }
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("☀️", style = MaterialTheme.typography.displayLarge)
            Spacer(Modifier.height(8.dp))
            Text("No tasks yet — enjoy the calm",
                style = MaterialTheme.typography.bodyMedium, color = Stone)
        }
    }
}

private fun TaskStatus.toZenStatus() = when (this) {
    TaskStatus.NotStarted -> ZenStatus.OnTrack
    TaskStatus.InProgress -> ZenStatus.AtRisk
    TaskStatus.Completed  -> ZenStatus.OnTrack
    TaskStatus.Cancelled  -> ZenStatus.OverBudget
}
