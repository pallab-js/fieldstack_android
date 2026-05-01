package com.fieldstack.android.ui.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldstack.android.data.repository.FakeData
import com.fieldstack.android.domain.usecase.InsightsUseCase
import com.fieldstack.android.domain.usecase.WeeklyInsights
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.io.File
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class InsightsViewModel @Inject constructor(
    @ApplicationContext private val ctx: Context,
    insightsUseCase: InsightsUseCase,
) : ViewModel() {

    val insights = insightsUseCase(FakeData.USER_ID)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WeeklyInsights())

    fun exportCsv(): File {
        val data = insights.value
        val csv = buildString {
            appendLine("Metric,Value")
            appendLine("Tasks Completed,${data.tasksCompleted}")
            appendLine("Total Tasks,${data.totalTasks}")
            appendLine("Reports Submitted,${data.reportsSubmitted}")
            appendLine("Completion Rate,${(data.completionRate * 100).toInt()}%")
            appendLine("Week Ending,${LocalDate.now()}")
        }
        return File(ctx.filesDir, "insights_${LocalDate.now()}.csv")
            .also { it.writeText(csv) }
    }
}
