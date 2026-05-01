package com.fieldstack.android.ui.tasks

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fieldstack.android.domain.model.Taskimport com.fieldstack.android.domain.model.TaskPriority
import com.fieldstack.android.domain.model.TaskStatus
import com.fieldstack.android.ui.components.ZenButton
import com.fieldstack.android.ui.components.ZenButtonVariant
import com.fieldstack.android.ui.components.ZenCard
import com.fieldstack.android.ui.components.ZenCardVariant
import com.fieldstack.android.ui.components.ZenStatus
import com.fieldstack.android.ui.components.ZenStatusChip
import com.fieldstack.android.ui.theme.Mint
import com.fieldstack.android.ui.theme.Spacing
import com.fieldstack.android.ui.theme.Stone
import com.fieldstack.android.ui.theme.Warning
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val DT_FMT = DateTimeFormatter.ofPattern("EEE, MMM d · h:mm a")
    .withZone(ZoneId.systemDefault())

@Composable
fun TaskDetailScreen(
    taskId: String,
    onStartTask: (String) -> Unit = {},
    viewModel: TaskDetailViewModel = hiltViewModel(),
    commentViewModel: CommentViewModel = hiltViewModel(),
) {
    val task by viewModel.task.collectAsStateWithLifecycle()
    val comments by commentViewModel.commentsFor(taskId).collectAsStateWithLifecycle(emptyList())

    when (val t = task) {
        null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading…", color = Stone)
        }
        else -> TaskDetailContent(
            task = t,
            comments = comments,
            onStartTask = { onStartTask(t.id) },
            onMarkComplete = { viewModel.markComplete() },
            onMarkInProgress = { viewModel.markInProgress() },
            onAddComment = { commentViewModel.addComment(taskId, it) },
        )
    }
}

@Composable
private fun TaskDetailContent(
    task: Task,
    comments: List<com.fieldstack.android.domain.model.Comment> = emptyList(),
    onStartTask: () -> Unit,
    onMarkComplete: () -> Unit,
    onMarkInProgress: () -> Unit,
    onAddComment: (String) -> Unit = {},
) {
    // Completion celebration: scale bounce on status → Completed
    val scale = remember { Animatable(1f) }
    var prevStatus by remember { mutableStateOf(task.status) }

    LaunchedEffect(task.status) {
        if (task.status == TaskStatus.Completed && prevStatus != TaskStatus.Completed) {
            scale.animateTo(1.15f, spring(stiffness = Spring.StiffnessMediumLow))
            scale.animateTo(1f,    spring(stiffness = Spring.StiffnessMediumLow))
        }
        prevStatus = task.status
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        // Header card
        ZenCard(
            variant = ZenCardVariant.Elevated,
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale.value)
                .semantics { contentDescription = "Task: ${task.title}" },
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(task.title, style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null,
                            tint = Stone, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(3.dp))
                        Text(task.location, style = MaterialTheme.typography.bodySmall, color = Stone)
                    }
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, null,
                            tint = Stone, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(3.dp))
                        Text(DT_FMT.format(task.dueAt),
                            style = MaterialTheme.typography.bodySmall, color = Stone)
                    }
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    ZenStatusChip(task.status.toZenStatus())
                    if (task.priority == TaskPriority.High) {
                        Text("● High", style = MaterialTheme.typography.labelSmall, color = Warning)
                    }
                }
            }

            if (task.status == TaskStatus.Completed) {
                Spacer(Modifier.height(8.dp))
                Text("✅ Great work — task complete!",
                    style = MaterialTheme.typography.bodySmall, color = Mint)
            }
        }

        // Description
        ZenCard(modifier = Modifier.fillMaxWidth()) {
            Text("Description", style = MaterialTheme.typography.labelMedium, color = Stone)
            Spacer(Modifier.height(6.dp))
            HorizontalDivider()
            Spacer(Modifier.height(6.dp))
            Text(task.description, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(Modifier.height(Spacing.xs))

        // CTAs
        when (task.status) {
            TaskStatus.NotStarted -> {
                ZenButton(
                    text = "Start Task",
                    onClick = {
                        onMarkInProgress()
                        onStartTask()
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
                ZenButton(
                    text = "Mark Complete",
                    onClick = onMarkComplete,
                    variant = ZenButtonVariant.Secondary,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            TaskStatus.InProgress -> {
                ZenButton(
                    text = "Continue → Add Report",
                    onClick = onStartTask,
                    modifier = Modifier.fillMaxWidth(),
                )
                ZenButton(
                    text = "Mark Complete",
                    onClick = onMarkComplete,
                    variant = ZenButtonVariant.Secondary,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            TaskStatus.Completed -> {
                ZenButton(
                    text = "View Reports",
                    onClick = onStartTask,
                    variant = ZenButtonVariant.Ghost,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            TaskStatus.Cancelled -> {
                Text("This task has been cancelled.",
                    style = MaterialTheme.typography.bodySmall, color = Stone)
            }
        }

        Spacer(Modifier.height(Spacing.xs))
        CommentsSection(
            taskId = task.id,
            comments = comments,
            onAdd = onAddComment,
        )
    }
}

private fun TaskStatus.toZenStatus() = when (this) {
    TaskStatus.NotStarted -> ZenStatus.OnTrack
    TaskStatus.InProgress -> ZenStatus.AtRisk
    TaskStatus.Completed  -> ZenStatus.OnTrack
    TaskStatus.Cancelled  -> ZenStatus.OverBudget
}
