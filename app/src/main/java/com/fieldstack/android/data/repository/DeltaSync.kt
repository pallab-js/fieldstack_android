package com.fieldstack.android.data.repository

import com.fieldstack.android.domain.model.Task

/**
 * Merge strategy for delta sync.
 * Applies [ConflictResolutionStrategy] to each incoming remote task against the local copy.
 * Returns a list of tasks that should be written to Room (only those where remote wins or is new).
 */
object MergeStrategy {
    fun mergeTasks(
        localMap: Map<String, Task>,
        remoteDelta: List<Task>,
    ): MergeResult {
        val toUpsert = mutableListOf<Task>()
        val conflicts = mutableListOf<TaskConflict>()

        remoteDelta.forEach { remote ->
            val local = localMap[remote.id]
            if (local == null) {
                // New record from server — always accept
                toUpsert.add(remote)
            } else {
                val result = ConflictResolutionStrategy.resolveTask(local, remote)
                if (result.localOverwritten) {
                    toUpsert.add(result.winner)
                    conflicts.add(TaskConflict(local, remote, winner = result.winner))
                }
                // else local wins — no write needed
            }
        }
        return MergeResult(toUpsert, conflicts)
    }
}

data class TaskConflict(val local: Task, val remote: Task, val winner: Task)
data class MergeResult(val toUpsert: List<Task>, val conflicts: List<TaskConflict>)
