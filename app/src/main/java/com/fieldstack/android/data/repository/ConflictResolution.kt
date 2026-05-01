package com.fieldstack.android.data.repository

import com.fieldstack.android.domain.model.Task

/**
 * Last-write-wins: whichever version has the later updatedAt wins.
 * Returns the winning entity and a flag indicating if the local version was overwritten.
 */
object ConflictResolutionStrategy {
    fun resolveTask(local: Task, remote: Task): ConflictResult<Task> {
        return if (remote.updatedAt > local.updatedAt) {
            ConflictResult(winner = remote, localOverwritten = true)
        } else {
            ConflictResult(winner = local, localOverwritten = false)
        }
    }
}

data class ConflictResult<T>(val winner: T, val localOverwritten: Boolean)
