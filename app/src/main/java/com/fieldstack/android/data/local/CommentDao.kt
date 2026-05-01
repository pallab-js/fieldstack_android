package com.fieldstack.android.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.fieldstack.android.domain.model.Comment
import com.fieldstack.android.domain.model.SyncStatus
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val taskId: String,
    val authorId: String,
    val authorName: String,
    val body: String,
    val createdAt: Instant,
    val syncStatus: SyncStatus,
)

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE taskId = :taskId ORDER BY createdAt ASC")
    fun observeByTask(taskId: String): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comment: CommentEntity)

    @Query("DELETE FROM comments WHERE id = :id")
    suspend fun deleteById(id: String)
}

fun CommentEntity.toDomain() = Comment(id, taskId, authorId, authorName, body, createdAt, syncStatus)
fun Comment.toEntity() = CommentEntity(id, taskId, authorId, authorName, body, createdAt, syncStatus)
