package com.fieldstack.android.ui.settings

import app.cash.turbine.test
import com.fieldstack.android.data.remote.FieldStackApi
import com.fieldstack.android.data.remote.RoleUpdateRequest
import com.fieldstack.android.data.remote.UserDto
import com.fieldstack.android.domain.model.UserRole
import com.fieldstack.android.util.SessionManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdminViewModelTest {

    private val api = mockk<FieldStackApi>()
    private val session = mockk<SessionManager>()
    private val dispatcher = StandardTestDispatcher()

    private val userDtos = listOf(
        UserDto("u1", "Alice", "alice@example.com", "Admin"),
        UserDto("u2", "Bob", "bob@example.com", "FieldTech"),
    )

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        every { session.userRole } returns UserRole.Admin
    }

    @After
    fun teardown() = Dispatchers.resetMain()

    @Test
    fun `loadUsers populates users on success`() = runTest {
        coEvery { api.getUsers() } returns userDtos
        val vm = AdminViewModel(api, session)
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals(2, state.users.size)
        assertEquals("Alice", state.users[0].name)
        assertNull(state.error)
    }

    @Test
    fun `loadUsers sets error on failure`() = runTest {
        coEvery { api.getUsers() } throws RuntimeException("Network error")
        val vm = AdminViewModel(api, session)
        advanceUntilIdle()

        assertNotNull(vm.uiState.value.error)
        assertTrue(vm.uiState.value.users.isEmpty())
    }

    @Test
    fun `assignRole updates user role in state`() = runTest {
        coEvery { api.getUsers() } returns userDtos
        coEvery { api.updateUserRole("u2", RoleUpdateRequest("Supervisor")) } returns
            UserDto("u2", "Bob", "bob@example.com", "Supervisor")

        val vm = AdminViewModel(api, session)
        advanceUntilIdle()

        vm.assignRole("u2", UserRole.Supervisor)
        advanceUntilIdle()

        val bob = vm.uiState.value.users.first { it.id == "u2" }
        assertEquals(UserRole.Supervisor, bob.role)
        coVerify { api.updateUserRole("u2", RoleUpdateRequest("Supervisor")) }
    }

    @Test
    fun `assignRole sets error on API failure`() = runTest {
        coEvery { api.getUsers() } returns userDtos
        coEvery { api.updateUserRole(any(), any()) } throws RuntimeException("Forbidden")

        val vm = AdminViewModel(api, session)
        advanceUntilIdle()

        vm.assignRole("u2", UserRole.Supervisor)
        advanceUntilIdle()

        assertNotNull(vm.uiState.value.error)
    }
}
