package com.fieldstack.android.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.fieldstack.android.R

val Manrope = FontFamily(
    Font(R.font.manrope_regular, FontWeight.Normal),
    Font(R.font.manrope_semibold, FontWeight.SemiBold),
    Font(R.font.manrope_bold, FontWeight.Bold),
    Font(R.font.manrope_extrabold, FontWeight.ExtraBold),
)

val Nunito = FontFamily(
    Font(R.font.nunito_regular, FontWeight.Normal),
    Font(R.font.nunito_semibold, FontWeight.SemiBold),
)

val SourceCodePro = FontFamily(
    Font(R.font.source_code_pro_regular, FontWeight.Normal),
)

// ── BudgetZen Type Scale ───────────────────────────────────────────────────
val BudgetZenTypography = Typography(
    // Display — 36sp ExtraBold, 1.15 lh, 0.02em
    displayLarge = TextStyle(
        fontFamily = Manrope, fontWeight = FontWeight.ExtraBold,
        fontSize = 36.sp, lineHeight = 41.4.sp, letterSpacing = 0.02.em,
    ),
    // Headline — 28sp Bold, 1.2 lh, 0.01em
    headlineLarge = TextStyle(
        fontFamily = Manrope, fontWeight = FontWeight.Bold,
        fontSize = 28.sp, lineHeight = 33.6.sp, letterSpacing = 0.01.em,
    ),
    // Subhead — 20sp SemiBold, 1.3 lh
    headlineMedium = TextStyle(
        fontFamily = Manrope, fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp, lineHeight = 26.sp,
    ),
    // Body Large — 18sp Regular, 1.6 lh
    bodyLarge = TextStyle(
        fontFamily = Nunito, fontWeight = FontWeight.Normal,
        fontSize = 18.sp, lineHeight = 28.8.sp,
    ),
    // Body — 16sp Regular, 1.6 lh
    bodyMedium = TextStyle(
        fontFamily = Nunito, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 25.6.sp,
    ),
    // Body Small — 14sp Regular, 1.5 lh, 0.01em
    bodySmall = TextStyle(
        fontFamily = Nunito, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 21.sp, letterSpacing = 0.01.em,
    ),
    // Caption — 12sp SemiBold, 1.4 lh, 0.02em
    labelSmall = TextStyle(
        fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp, lineHeight = 16.8.sp, letterSpacing = 0.02.em,
    ),
    // Overline — 11sp Bold, 1.2 lh, 0.06em
    labelMedium = TextStyle(
        fontFamily = Manrope, fontWeight = FontWeight.Bold,
        fontSize = 11.sp, lineHeight = 13.2.sp, letterSpacing = 0.06.em,
    ),
    // Button label — 15sp Medium (used by ZenButton)
    labelLarge = TextStyle(
        fontFamily = Manrope, fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp, lineHeight = 20.sp,
    ),
)

// Code style — used directly where needed
val CodeTextStyle = TextStyle(
    fontFamily = SourceCodePro, fontWeight = FontWeight.Normal,
    fontSize = 14.sp, lineHeight = 22.4.sp,
)
