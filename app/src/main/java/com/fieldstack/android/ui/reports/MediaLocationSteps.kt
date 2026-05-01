package com.fieldstack.android.ui.reports

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.fieldstack.android.ui.components.TipChip
import com.fieldstack.android.ui.components.ZenButton
import com.fieldstack.android.ui.components.ZenButtonVariant
import com.fieldstack.android.ui.components.ZenCard
import com.fieldstack.android.ui.theme.Mint
import com.fieldstack.android.ui.theme.Radius
import com.fieldstack.android.ui.theme.Sky
import com.fieldstack.android.ui.theme.SkyLight
import com.fieldstack.android.ui.theme.Spacing
import com.fieldstack.android.ui.theme.Stone
import com.fieldstack.android.util.CameraHelper
import com.fieldstack.android.util.LocationHelper
import androidx.core.content.ContextCompat

// ── Step 3: Media ──────────────────────────────────────────────────────────

@Composable
fun Step3Media(
    draft: ReportDraft,
    onPhotoAdded: (String) -> Unit,
    onPhotoRemoved: (String) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var showCamera by remember { mutableStateOf(false) }

    val cameraPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) showCamera = true }

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Text("Attach Photos", style = MaterialTheme.typography.headlineMedium)

        // Tip chip
        TipChip("Photos help verify inspection conditions")

        // Camera preview or add button
        if (showCamera) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(Radius.medium)),
            ) {
                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).also { pv ->
                            CameraHelper.bindPreview(ctx, lifecycleOwner, pv) { cap ->
                                imageCapture = cap
                            }
                        }
                    },
                    modifier = Modifier.matchParentSize(),
                )
                ZenButton(
                    text = "Capture",
                    onClick = {
                        imageCapture?.let { cap ->
                            CameraHelper.takePhoto(
                                context = context,
                                imageCapture = cap,
                                executor = ContextCompat.getMainExecutor(context),
                                onSuccess = { uri ->
                                    onPhotoAdded(uri.toString())
                                    showCamera = false
                                },
                                onError = { showCamera = false },
                            )
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp),
                )
            }
        } else {
            ZenButton(
                text = "Add Photo",
                onClick = { cameraPermission.launch(Manifest.permission.CAMERA) },
                variant = ZenButtonVariant.Secondary,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // Thumbnails
        if (draft.photoUris.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(draft.photoUris) { uri ->
                    Box(modifier = Modifier.semantics { contentDescription = "Photo thumbnail" }) {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Captured photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(Radius.medium)),
                        )
                        IconButton(
                            onClick = { onPhotoRemoved(uri) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(24.dp),
                        ) {
                            Icon(Icons.Default.Close, "Remove photo",
                                tint = Stone, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

// ── Step 4: Location ───────────────────────────────────────────────────────

@Composable
fun Step4Location(
    draft: ReportDraft,
    onLocationSet: (Double, Double) -> Unit,
) {
    val context = LocalContext.current
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val locationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val granted = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            loading = true
            LocationHelper.getLastLocation(
                context = context,
                onResult = { lat, lng ->
                    onLocationSet(lat, lng)
                    loading = false
                },
                onError = { msg ->
                    error = msg
                    loading = false
                },
            )
        } else {
            error = "Location permission denied"
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Text("Location", style = MaterialTheme.typography.headlineMedium)

        TipChip("Location is attached automatically when online")

        ZenCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = Mint, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                } else if (draft.latitude != null && draft.longitude != null) {
                    Column {
                        Text("Location detected",
                            style = MaterialTheme.typography.bodySmall, color = Stone)
                        Text(
                            "%.4f° N, %.4f° E".format(draft.latitude, draft.longitude),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                } else {
                    Text(error ?: "No location set",
                        style = MaterialTheme.typography.bodySmall, color = Stone)
                }
            }
            Spacer(Modifier.height(Spacing.xs))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ZenButton(
                    text = "Use Current",
                    onClick = {
                        locationPermission.launch(arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                        ))
                    },
                    modifier = Modifier.weight(1f),
                )
                ZenButton(
                    text = "Clear",
                    onClick = { onLocationSet(0.0, 0.0) },
                    variant = ZenButtonVariant.Ghost,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}


