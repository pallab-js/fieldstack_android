package com.fieldstack.android.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.fieldstack.android.ui.theme.BudgetZenTypography
import com.fieldstack.android.ui.theme.Error
import com.fieldstack.android.ui.theme.ErrorDark
import com.fieldstack.android.ui.theme.ErrorLight
import com.fieldstack.android.ui.theme.Mint
import com.fieldstack.android.ui.theme.MintDark
import com.fieldstack.android.ui.theme.MintLight
import com.fieldstack.android.ui.theme.Radius
import com.fieldstack.android.ui.theme.StoneDark
import com.fieldstack.android.ui.theme.StoneLight
import com.fieldstack.android.ui.theme.SurfaceBorder
import com.fieldstack.android.ui.theme.Warning
import com.fieldstack.android.ui.theme.WarningDark
import com.fieldstack.android.ui.theme.WarningLight
import androidx.compose.ui.graphics.Color

// ── Filter Chip ────────────────────────────────────────────────────────────
@Composable
fun ZenFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, style = BudgetZenTypography.bodySmall) },
        shape = RoundedCornerShape(Radius.small),
        modifier = modifier.semantics { contentDescription = label },
        colors = FilterChipDefaults.filterChipColors(
            containerColor         = StoneLight,
            labelColor             = StoneDark,
            selectedContainerColor = MintLight,
            selectedLabelColor     = MintDark,
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor         = SurfaceBorder,
            selectedBorderColor = Mint,
        ),
    )
}

// ── Status Chip ────────────────────────────────────────────────────────────
enum class ZenStatus { OnTrack, AtRisk, OverBudget }

@Composable
fun ZenStatusChip(status: ZenStatus, modifier: Modifier = Modifier) {
    val (bg, text, border, label) = when (status) {
        ZenStatus.OnTrack    -> StatusStyle(MintLight,    MintDark,    Color(0x3310B981), "On Track")
        ZenStatus.AtRisk     -> StatusStyle(WarningLight, WarningDark, Color(0x33F59E0B), "At Risk")
        ZenStatus.OverBudget -> StatusStyle(ErrorLight,   ErrorDark,   Color(0x33EF4444), "Over Budget")
    }
    Surface(
        shape = RoundedCornerShape(Radius.small),
        color = bg,
        border = BorderStroke(1.dp, border),
        modifier = modifier.semantics { contentDescription = label },
    ) {
        Text(
            text = label,
            style = BudgetZenTypography.labelSmall,
            color = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

private data class StatusStyle(val bg: Color, val text: Color, val border: Color, val label: String)
