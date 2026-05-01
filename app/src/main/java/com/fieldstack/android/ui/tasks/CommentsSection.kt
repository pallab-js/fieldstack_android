package com.fieldstack.android.ui.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.fieldstack.android.domain.model.Comment
import com.fieldstack.android.ui.components.ZenCard
import com.fieldstack.android.ui.components.ZenTextField
import com.fieldstack.android.ui.theme.Sky
import com.fieldstack.android.ui.theme.Spacing
import com.fieldstack.android.ui.theme.Stone
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val TIME_FMT = DateTimeFormatter.ofPattern("MMM d, h:mm a").withZone(ZoneId.systemDefault())
private val MENTION_REGEX = Regex("""@\w+""")

@Composable
fun CommentsSection(
    taskId: String,
    comments: List<Comment>,
    onAdd: (String) -> Unit,
) {
    var input by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Text("Comments (${comments.size})",
            style = MaterialTheme.typography.labelMedium, color = Stone)
        HorizontalDivider()

        if (comments.isEmpty()) {
            Text("No comments yet — be the first!",
                style = MaterialTheme.typography.bodySmall, color = Stone,
                modifier = Modifier.padding(vertical = 8.dp))
        } else {
            comments.forEach { comment -> CommentRow(comment) }
        }

        // Input row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            ZenTextField(
                value = input,
                onValueChange = { input = it },
                placeholder = "Add a comment… @mention teammates",
                contentDesc = "Comment input",
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = { onAdd(input); input = "" },
                enabled = input.isNotBlank(),
                modifier = Modifier.semantics { contentDescription = "Send comment" },
            ) {
                Icon(Icons.Default.Send, null,
                    tint = if (input.isNotBlank()) Sky else Stone,
                    modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun CommentRow(comment: Comment) {
    ZenCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(comment.authorName,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold))
                    Spacer(Modifier.width(6.dp))
                    Text(TIME_FMT.format(comment.createdAt),
                        style = MaterialTheme.typography.labelSmall, color = Stone)
                }
                Spacer(Modifier.height(3.dp))
                Text(
                    text = buildAnnotatedString {
                        val body = comment.body
                        var last = 0
                        MENTION_REGEX.findAll(body).forEach { match ->
                            append(body.substring(last, match.range.first))
                            withStyle(SpanStyle(color = Sky)) { append(match.value) }
                            last = match.range.last + 1
                        }
                        append(body.substring(last))
                    },
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
