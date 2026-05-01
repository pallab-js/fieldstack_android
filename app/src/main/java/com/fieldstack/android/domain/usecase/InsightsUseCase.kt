package com.fieldstack.android.domain.usecase

import com.fieldstack.android.data.repository.FieldStackRepository
import com.fieldstack.android.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class WeeklyInsights(
    val tasksCompleted: Int = 0,
    val totalTasks: Int = 0,
    val reportsSubmitted: Int = 0,
    val completionRate: Float = 0f,
)

class InsightsUseCase @Inject constructor(
    private val repository: FieldStackRepository,
) {
    operator fun invoke(userId: String): Flow<WeeklyInsights> =
        repository.observeTasks(userId)
            .distinctUntilChanged()
            .debounce(300)
            .map { tasks ->
                // Computed fresh on each emission so the window never goes stale
                val weekAgo = Instant.now().minus(7, ChronoUnit.DAYS)
                val weekTasks = tasks.filter { it.createdAt >= weekAgo }
                val completed = weekTasks.count { it.status == TaskStatus.Completed }
                WeeklyInsights(
                    tasksCompleted   = completed,
                    totalTasks       = weekTasks.size,
                    reportsSubmitted = weekTasks.count { it.status == TaskStatus.Completed },
                    completionRate   = if (weekTasks.isEmpty()) 0f else completed / weekTasks.size.toFloat(),
                )
            }
}
