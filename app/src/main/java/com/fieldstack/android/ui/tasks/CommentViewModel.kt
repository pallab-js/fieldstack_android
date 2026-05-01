package com.fieldstack.android.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldstack.android.data.local.CommentDao
import com.fieldstack.android.data.local.toDomain
import com.fieldstack.android.data.local.toEntity
import com.fieldstack.android.data.repository.FakeData
import com.fieldstack.android.domain.model.Comment
import com.fieldstack.android.domain.model.SyncStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val dao: CommentDao,
) : ViewModel() {

    fun commentsFor(taskId: String) = dao.observeByTask(taskId)
        .map { list -> list.map { it.toDomain() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addComment(taskId: String, body: String) = viewModelScope.launch {
        if (body.isBlank()) return@launch
        dao.insert(
            Comment(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                authorId = FakeData.USER_ID,
                authorName = "Alex",
                body = body.trim(),
                createdAt = Instant.now(),
                syncStatus = SyncStatus.Pending,
            ).toEntity()
        )
    }

    fun deleteComment(id: String) = viewModelScope.launch { dao.deleteById(id) }
}
