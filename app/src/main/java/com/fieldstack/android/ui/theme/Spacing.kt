package com.fieldstack.android.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ── Spacing ────────────────────────────────────────────────────────────────
object Spacing {
    val xs  = 8.dp
    val sm  = 16.dp
    val md  = 24.dp
    val lg  = 32.dp
    val xl  = 40.dp
    val xxl = 48.dp
    val xxxl = 64.dp

    val componentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    val sectionSpacing   = 40.dp
    val groupSpacing     = 24.dp
}

// ── Border Radius ──────────────────────────────────────────────────────────
object Radius {
    val none   = 0.dp
    val small  = 6.dp    // Chips, badges
    val medium = 12.dp   // Buttons, inputs, cards
    val large  = 16.dp   // Modals, panels
    val xl     = 24.dp   // Hero sections, goal cards
    val full   = 9999.dp // Avatars, toggles
}

// ── Elevation (dp approximations of the BudgetZen shadow spec) ─────────────
data class ElevationTokens(
    val subtle: Dp,
    val medium: Dp,
    val large: Dp,
    val overlay: Dp,
)

val BudgetZenElevation = ElevationTokens(
    subtle  = 2.dp,
    medium  = 4.dp,
    large   = 8.dp,
    overlay = 16.dp,
)

val LocalElevation = staticCompositionLocalOf { BudgetZenElevation }
