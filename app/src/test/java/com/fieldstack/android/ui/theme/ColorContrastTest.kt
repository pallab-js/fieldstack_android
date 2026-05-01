package com.fieldstack.android.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Verifies key BudgetZen color pairs meet WCAG AA contrast ratio (≥4.5:1 for normal text).
 * Formula: https://www.w3.org/TR/WCAG21/#dfn-contrast-ratio
 */
class ColorContrastTest {

    private fun relativeLuminance(c: Color): Double {
        fun channel(v: Float): Double {
            val s = v.toDouble()
            return if (s <= 0.03928) s / 12.92 else Math.pow((s + 0.055) / 1.055, 2.4)
        }
        return 0.2126 * channel(c.red) + 0.7152 * channel(c.green) + 0.0722 * channel(c.blue)
    }

    private fun contrastRatio(fg: Color, bg: Color): Double {
        val l1 = relativeLuminance(fg)
        val l2 = relativeLuminance(bg)
        val lighter = maxOf(l1, l2)
        val darker  = minOf(l1, l2)
        return (lighter + 0.05) / (darker + 0.05)
    }

    @Test
    fun `Mint on white meets WCAG AA large text (3 to 1)`() {
        // Mint (#10B981) on white — used for headings/CTAs (large text threshold = 3:1)
        val ratio = contrastRatio(Mint, Surface)
        assertTrue("Mint on white ratio $ratio should be ≥ 3.0", ratio >= 3.0)
    }

    @Test
    fun `StoneDeep on AppBackground meets WCAG AA (4_5 to 1)`() {
        val ratio = contrastRatio(StoneDeep, AppBackground)
        assertTrue("StoneDeep on background ratio $ratio should be ≥ 4.5", ratio >= 4.5)
    }

    @Test
    fun `Error on white meets WCAG AA (4_5 to 1)`() {
        val ratio = contrastRatio(Error, Surface)
        assertTrue("Error on white ratio $ratio should be ≥ 4.5", ratio >= 4.5)
    }

    @Test
    fun `Stone on white meets WCAG AA large text (3 to 1)`() {
        val ratio = contrastRatio(Stone, Surface)
        assertTrue("Stone on white ratio $ratio should be ≥ 3.0", ratio >= 3.0)
    }
}
