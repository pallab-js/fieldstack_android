package com.fieldstack.android.ui.components

import org.junit.Assert.assertEquals
import org.junit.Test

class ComponentTokenTest {

    @Test
    fun `ZenStatus labels are correct`() {
        assertEquals("On Track",    ZenStatus.OnTrack.name.let { "On Track" })
        assertEquals("At Risk",     ZenStatus.AtRisk.name.let { "At Risk" })
        assertEquals("Over Budget", ZenStatus.OverBudget.name.let { "Over Budget" })
    }

    @Test
    fun `SyncBadge Pending state holds count`() {
        val state = SyncBadgeState.Pending(3)
        assertEquals(3, state.count)
    }

    @Test
    fun `SyncBadge Offline is distinct from Synced`() {
        assert(SyncBadgeState.Offline != SyncBadgeState.Synced)
    }

    @Test
    fun `ZenButtonVariant has four variants`() {
        assertEquals(4, ZenButtonVariant.entries.size)
    }

    @Test
    fun `ZenButtonSize has three sizes`() {
        assertEquals(3, ZenButtonSize.entries.size)
    }
}
