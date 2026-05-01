package com.fieldstack.android.domain.usecase

import com.fieldstack.android.data.repository.MergeResult
import com.fieldstack.android.data.repository.MergeStrategy
import com.fieldstack.android.domain.model.Task
import javax.inject.Inject

/**
 * Domain use case wrapping MergeStrategy so merge logic is independently testable
 * without a Worker context.
 */
class MergeTasksUseCase @Inject constructor() {
    operator fun invoke(localMap: Map<String, Task>, remoteDelta: List<Task>): MergeResult =
        MergeStrategy.mergeTasks(localMap, remoteDelta)
}
