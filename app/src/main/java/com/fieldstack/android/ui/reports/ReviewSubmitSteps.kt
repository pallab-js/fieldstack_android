package com.fieldstack.android.ui.reports

import android.graphics.Bitmap
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fieldstack.android.ui.components.ZenButton
import com.fieldstack.android.ui.components.ZenButtonVariant
import com.fieldstack.android.ui.components.ZenCard
import com.fieldstack.android.ui.theme.InputBorder
import com.fieldstack.android.ui.theme.Mint
import com.fieldstack.android.ui.theme.Radius
import com.fieldstack.android.ui.theme.Spacing
import com.fieldstack.android.ui.theme.Stone
import com.fieldstack.android.ui.theme.StoneDeep
import com.fieldstack.android.ui.theme.StoneLight
import java.io.File

// ── Step 5: Signature ──────────────────────────────────────────────────────

@Composable
fun Step5Signature(
    draft: ReportDraft,
    onSignatureSaved: (String) -> Unit,
) {
    val context = LocalContext.current
    val paths = remember { mutableStateListOf<Pair<Path, Offset>>() }
    var currentPath by remember { mutableStateOf(Path()) }
    var hasSig by remember { mutableStateOf(draft.signatureUri != null) }

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Text("Signature", style = MaterialTheme.typography.headlineMedium)
        Text("Sign below to confirm this report",
            style = MaterialTheme.typography.bodySmall, color = Stone)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(Radius.medium))
                .background(StoneLight)
                .border(1.dp, InputBorder, RoundedCornerShape(Radius.medium))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentPath = Path().also { it.moveTo(offset.x, offset.y) }
                        },
                        onDrag = { change, _ ->
                            currentPath.lineTo(change.position.x, change.position.y)
                            paths.add(Pair(currentPath, change.position))
                        },
                        onDragEnd = { hasSig = paths.isNotEmpty() },
                    )
                },
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                paths.forEach { (path, _) ->
                    drawPath(
                        path = path,
                        color = Color(StoneDeep.toArgb()),
                        style = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round),
                    )
                }
            }
            if (paths.isEmpty()) {
                Text(
                    "Sign here",
                    style = MaterialTheme.typography.bodySmall,
                    color = Stone,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ZenButton(
                text = "Clear",
                onClick = {
                    paths.clear()
                    currentPath = Path()
                    hasSig = false
                },
                variant = ZenButtonVariant.Ghost,
                modifier = Modifier.weight(1f),
            )
            ZenButton(
                text = "Save Signature",
                onClick = {
                    // Render paths to bitmap and save to file
                    val bmp = Bitmap.createBitmap(800, 300, Bitmap.Config.ARGB_8888)
                    val canvas = android.graphics.Canvas(bmp)
                    canvas.drawColor(android.graphics.Color.WHITE)
                    val paint = Paint().apply {
                        color = StoneDeep.toArgb()
                        strokeWidth = 6f
                        style = Paint.Style.STROKE
                        strokeCap = Paint.Cap.ROUND
                        isAntiAlias = true
                    }
                    paths.forEach { (path, _) ->
                        val ap = android.graphics.Path()
                        path.asAndroidPath().let { ap.set(it) }
                        canvas.drawPath(ap, paint)
                    }
                    val file = File(context.filesDir, "sig_${System.currentTimeMillis()}.png")
                    file.outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
                    onSignatureSaved(file.absolutePath)
                },
                enabled = hasSig,
                modifier = Modifier.weight(1f),
            )
        }

        if (draft.signatureUri != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, null, tint = Mint, modifier = Modifier.size(16.dp))
                Text(" Signature saved", style = MaterialTheme.typography.bodySmall, color = Mint)
            }
        }
    }
}

// ── Review Screen ──────────────────────────────────────────────────────────

@Composable
fun ReviewScreen(
    draft: ReportDraft,
    isOnline: Boolean,
    isSaving: Boolean,
    onSubmit: () -> Unit,
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text("Review & Submit", style = MaterialTheme.typography.headlineMedium)

        ReviewSection("Basic Info") {
            ReviewRow("Title", draft.title)
            ReviewRow("Category", draft.category.name)
            ReviewRow("Priority", draft.priority.name)
        }

        ReviewSection("Details") {
            ReviewRow("Findings", draft.details)
            if (draft.notes.isNotBlank()) ReviewRow("Notes", draft.notes)
        }

        ReviewSection("Media") {
            ReviewRow("Photos", "${draft.photoUris.size} attached") {
                Icon(Icons.Default.Image, null, tint = Stone, modifier = Modifier.size(16.dp))
            }
        }

        ReviewSection("Location") {
            if (draft.latitude != null && draft.longitude != null) {
                ReviewRow("Coordinates", "%.4f°, %.4f°".format(draft.latitude, draft.longitude)) {
                    Icon(Icons.Default.LocationOn, null, tint = Mint, modifier = Modifier.size(16.dp))
                }
            } else {
                ReviewRow("Location", "Not set")
            }
        }

        ReviewSection("Signature") {
            ReviewRow("Status", if (draft.signatureUri != null) "✅ Signed" else "Not signed")
        }

        Spacer(Modifier.height(Spacing.xs))

        ZenButton(
            text = if (isSaving) "Saving…"
                   else if (isOnline) "Submit" else "Save & Queue Sync",
            onClick = onSubmit,
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ReviewSection(title: String, content: @Composable () -> Unit) {
    ZenCard(modifier = Modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.labelMedium, color = Stone)
        Spacer(Modifier.height(6.dp))
        HorizontalDivider()
        Spacer(Modifier.height(6.dp))
        content()
    }
}

@Composable
private fun ReviewRow(
    label: String,
    value: String,
    icon: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Stone)
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon?.invoke()
            if (icon != null) Spacer(Modifier.size(4.dp))
            Text(value, style = MaterialTheme.typography.bodySmall)
        }
    }
}
