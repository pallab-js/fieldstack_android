package com.fieldstack.android.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.fieldstack.android.ui.theme.BudgetZenTypography
import com.fieldstack.android.ui.theme.Mint
import com.fieldstack.android.ui.theme.MintLight
import com.fieldstack.android.ui.theme.Radius
import com.fieldstack.android.ui.theme.Sky
import com.fieldstack.android.ui.theme.SkyLight
import com.fieldstack.android.ui.theme.Stone
import com.fieldstack.android.ui.theme.StoneLight
import androidx.compose.ui.graphics.Color

sealed interface SyncBadgeState {
    data object Synced : SyncBadgeState
    data object Offline : SyncBadgeState
    data class Pending(val count: Int) : SyncBadgeState
}

@Composable
fun SyncBadge(state: SyncBadgeState, modifier: Modifier = Modifier) {
    val (icon, tint, bg, border, desc) = when (state) {
        SyncBadgeState.Synced        -> BadgeStyle(Icons.Default.CheckCircle, Mint,  MintLight,  Color(0x3310B981), "Synced")
        SyncBadgeState.Offline       -> BadgeStyle(Icons.Default.CloudOff,    Sky,   SkyLight,   Color(0x3338BDF8), "Offline — changes saved locally")
        is SyncBadgeState.Pending    -> BadgeStyle(Icons.Default.Sync,        Stone, StoneLight, Color(0x33A8A29E), "${state.count} pending sync")
    }
    val label = when (state) {
        SyncBadgeState.Synced     -> "Synced"
        SyncBadgeState.Offline    -> "Offline"
        is SyncBadgeState.Pending -> "${state.count} pending"
    }
    Surface(
        shape = RoundedCornerShape(Radius.full),
        color = bg,
        border = BorderStroke(1.dp, border),
        modifier = modifier.semantics { contentDescription = desc },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(5.dp))
            Text(label, style = BudgetZenTypography.labelSmall, color = tint)
        }
    }
}

private data class BadgeStyle(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val tint: Color,
    val bg: Color,
    val border: Color,
    val desc: String,
)
