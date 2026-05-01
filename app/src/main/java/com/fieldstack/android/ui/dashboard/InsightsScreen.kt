package com.fieldstack.android.ui.dashboard

import android.content.Intent
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fieldstack.android.domain.usecase.WeeklyInsights
import com.fieldstack.android.ui.components.ZenButton
import com.fieldstack.android.ui.components.ZenButtonVariant
import com.fieldstack.android.ui.components.ZenCard
import com.fieldstack.android.ui.components.ZenProgressBar
import com.fieldstack.android.ui.components.ZenStatus
import com.fieldstack.android.ui.components.ZenStatusChip
import com.fieldstack.android.ui.theme.Spacing
import com.fieldstack.android.ui.theme.Stone

@Composable
fun InsightsScreen(viewModel: InsightsViewModel = hiltViewModel()) {
    val insights by viewModel.insights.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text("Weekly Insights", style = MaterialTheme.typography.headlineLarge)
        Text("Last 7 days", style = MaterialTheme.typography.bodySmall, color = Stone)
        Spacer(Modifier.height(4.dp))

        WeeklySummaryCard(insights)

        ZenButton(
            text = "Export CSV",
            onClick = {
                val file = viewModel.exportCsv()
                val uri = FileProvider.getUriForFile(
                    context, "${context.packageName}.fileprovider", file)
                context.startActivity(
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/csv"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }.let { Intent.createChooser(it, "Share Insights CSV") }
                )
            },
            variant = ZenButtonVariant.Secondary,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun WeeklySummaryCard(insights: WeeklyInsights, modifier: Modifier = Modifier) {
    // Performance chip — never guilt-inducing red for minor misses
    val status = when {
        insights.completionRate >= 0.8f -> ZenStatus.OnTrack
        insights.completionRate >= 0.5f -> ZenStatus.AtRisk
        else                            -> ZenStatus.AtRisk  // never OverBudget for <50%
    }

    ZenCard(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("📊 This Week", style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f))
            ZenStatusChip(status)
        }
        Spacer(Modifier.height(Spacing.xs))
        HorizontalDivider()
        Spacer(Modifier.height(Spacing.xs))

        InsightRow("Tasks completed", "${insights.tasksCompleted} / ${insights.totalTasks}")
        Spacer(Modifier.height(6.dp))
        ZenProgressBar(
            progress = insights.completionRate,
            label = "Completion rate: ${(insights.completionRate * 100).toInt()}%",
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(6.dp))
        InsightRow("Reports submitted", "${insights.reportsSubmitted}")

        if (insights.completionRate >= 1f && insights.totalTasks > 0) {
            Spacer(Modifier.height(8.dp))
            Text("✨ Perfect week — outstanding work!",
                style = MaterialTheme.typography.bodySmall,
                color = com.fieldstack.android.ui.theme.Mint)
        }
    }
}

@Composable
private fun InsightRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Stone)
        Text(value, style = MaterialTheme.typography.bodySmall)
    }
}
