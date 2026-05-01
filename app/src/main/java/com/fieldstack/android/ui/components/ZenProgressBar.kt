package com.fieldstack.android.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import com.fieldstack.android.ui.theme.BudgetZenTypography
import com.fieldstack.android.ui.theme.Mint
import com.fieldstack.android.ui.theme.Radius
import com.fieldstack.android.ui.theme.Stone
import com.fieldstack.android.ui.theme.WarmGray

private val MILESTONES = setOf(0.25f, 0.50f, 0.75f, 1.0f)

@Composable
fun ZenProgressBar(
    progress: Float,                          // 0f..1f
    modifier: Modifier = Modifier,
    label: String? = null,
    onMilestone: ((Float) -> Unit)? = null,   // called once per milestone crossed
) {
    val clamped = progress.coerceIn(0f, 1f)
    val animated by animateFloatAsState(
        targetValue = clamped,
        animationSpec = tween(durationMillis = 600),
        label = "progress",
    )

    // Fire milestone callback when a threshold is first crossed
    var lastMilestone by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(clamped) {
        MILESTONES.filter { it <= clamped && it > lastMilestone }.maxOrNull()?.let { hit ->
            lastMilestone = hit
            onMilestone?.invoke(hit)
        }
    }

    Column(modifier = modifier) {
        if (label != null) {
            Text(label, style = BudgetZenTypography.bodySmall, color = Stone)
            Spacer(Modifier.height(6.dp))
        }
        LinearProgressIndicator(
            progress = { animated },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(Radius.full))
                .clearAndSetSemantics {
                    contentDescription = label
                        ?: "Progress: ${(clamped * 100).toInt()}%"
                    stateDescription = "${(clamped * 100).toInt()} percent"
                },
            color = Mint,
            trackColor = WarmGray.copy(alpha = 0.25f),
        )
    }
}
