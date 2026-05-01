package com.fieldstack.android.ui.components

import com.fieldstack.android.domain.model.UserRole
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PermissionGuardTest {

    @Test
    fun `FieldTech is not in SupervisorOnly roles`() {
        val allowed = setOf(UserRole.Supervisor, UserRole.Admin)
        assertFalse(UserRole.FieldTech in allowed)
    }

    @Test
    fun `Supervisor is in SupervisorOnly roles`() {
        val allowed = setOf(UserRole.Supervisor, UserRole.Admin)
        assertTrue(UserRole.Supervisor in allowed)
    }

    @Test
    fun `Admin is in AdminOnly roles`() {
        val allowed = setOf(UserRole.Admin)
        assertTrue(UserRole.Admin in allowed)
    }

    @Test
    fun `Supervisor is not in AdminOnly roles`() {
        val allowed = setOf(UserRole.Admin)
        assertFalse(UserRole.Supervisor in allowed)
    }

    @Test
    fun `FieldTech is not in AdminOnly roles`() {
        val allowed = setOf(UserRole.Admin)
        assertFalse(UserRole.FieldTech in allowed)
    }
}
