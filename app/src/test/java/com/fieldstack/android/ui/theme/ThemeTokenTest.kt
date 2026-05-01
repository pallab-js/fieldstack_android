package com.fieldstack.android.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeTokenTest {

    @Test
    fun `spacing scale is 8dp base`() {
        assertEquals(8f, Spacing.xs.value, 0f)
        assertEquals(16f, Spacing.sm.value, 0f)
        assertEquals(24f, Spacing.md.value, 0f)
    }

    @Test
    fun `radius tokens match BudgetZen spec`() {
        assertEquals(6f, Radius.small.value, 0f)
        assertEquals(12f, Radius.medium.value, 0f)
        assertEquals(16f, Radius.large.value, 0f)
        assertEquals(24f, Radius.xl.value, 0f)
    }

    @Test
    fun `primary color is BudgetZen mint`() {
        assertEquals(0xFF10B981, Mint.value)
    }

    @Test
    fun `error color is BudgetZen red`() {
        assertEquals(0xFFEF4444, Error.value)
    }
}
