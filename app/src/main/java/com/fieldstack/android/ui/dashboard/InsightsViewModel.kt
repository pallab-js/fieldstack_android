package com.fieldstack.android.ui.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldstack.android.domain.usecase.InsightsUseCase
import com.fieldstack.android.domain.usecase.WeeklyInsights
import com.fieldstack.android.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class InsightsViewModel @Inject constructor(
    @ApplicationContext private val ctx: Context,
    insightsUseCase: InsightsUseCase,
    session: SessionManager,
) : ViewModel() {

    val insights = (session.userId?.let { insightsUseCase(it) } ?: flowOf(WeeklyInsights()))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WeeklyInsights())

    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState = _exportState.asStateFlow()

    fun exportCsv() = viewModelScope.launch {
        _exportState.value = ExportState.Exporting
        _exportState.value = try {
            val file = withContext(Dispatchers.IO) {
                val data = insights.value
                val csv = buildString {
                    appendLine("Metric,Value")
                    appendLine("Tasks Completed,${data.tasksCompleted}")
                    appendLine("Total Tasks,${data.totalTasks}")
                    appendLine("Reports Submitted,${data.reportsSubmitted}")
                    appendLine("Completion Rate,${(data.completionRate * 100).toInt()}%")
                    appendLine("Week Ending,${LocalDate.now()}")
                }
                ctx.filesDir.listFiles { f -> f.name.startsWith("insights_") && f.name.endsWith(".csv") }
                    ?.forEach { it.delete() }
                File(ctx.filesDir, "insights_${LocalDate.now()}.csv").also { it.writeText(csv) }
            }
            ExportState.Done(file)
        } catch (e: Exception) {
            ExportState.Error(e.message ?: "Export failed")
        }
    }
}

sealed interface ExportState {
    data object Idle : ExportState
    data object Exporting : ExportState
    data class Done(val file: File) : ExportState
    data class Error(val message: String) : ExportState
}
