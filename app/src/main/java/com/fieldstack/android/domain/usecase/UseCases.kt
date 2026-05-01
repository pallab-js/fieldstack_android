package com.fieldstack.android.domain.usecase

import com.fieldstack.android.data.repository.FieldStackRepository
import com.fieldstack.android.domain.model.Report
import com.fieldstack.android.domain.model.Task
import com.fieldstack.android.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(private val repo: FieldStackRepository) {
    operator fun invoke(userId: String): Flow<List<Task>> = repo.observeTasks(userId)
}

class GetTaskByIdUseCase @Inject constructor(private val repo: FieldStackRepository) {
    operator fun invoke(id: String): Flow<Task?> = repo.observeTaskById(id)
}

class UpdateTaskStatusUseCase @Inject constructor(private val repo: FieldStackRepository) {
    suspend operator fun invoke(taskId: String, status: TaskStatus) =
        repo.updateTaskStatus(taskId, status)
}

class SaveReportUseCase @Inject constructor(private val repo: FieldStackRepository) {
    suspend operator fun invoke(report: Report) = repo.saveReport(report)
}
