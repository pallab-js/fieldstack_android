package com.fieldstack.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val LightColorScheme = lightColorScheme(
    primary          = Mint,
    onPrimary        = Surface,
    primaryContainer = MintLight,
    secondary        = Sky,
    onSecondary      = Surface,
    secondaryContainer = SkyLight,
    tertiary         = WarmGray,
    onTertiary       = Surface,
    background       = AppBackground,
    onBackground     = StoneDeep,
    surface          = Surface,
    onSurface        = StoneDeep,
    surfaceVariant   = StoneLight,
    onSurfaceVariant = StoneDark,
    outline          = InputBorder,
    outlineVariant   = SurfaceBorder,
    error            = Error,
    onError          = Surface,
)

// Dark theme mirrors light for MVP; full dark palette in Phase 2
private val DarkColorScheme = LightColorScheme

@Composable
fun BudgetZenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalElevation provides BudgetZenElevation) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
            typography  = BudgetZenTypography,
            content     = content,
        )
    }
}
