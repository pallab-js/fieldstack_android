package com.fieldstack.android.ui.auth

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fieldstack.android.util.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel
    private lateinit var session: SessionManager
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        session = SessionManager(ctx)
        session.clear()
        viewModel = AuthViewModel(session)
    }

    @After
    fun teardown() {
        session.clear()
        Dispatchers.resetMain()
    }

    @Test
    fun `blank email shows error`() = runTest {
        viewModel.login("", "not-a-real-password")
        assertTrue(viewModel.state.value is LoginUiState.Error)
    }

    @Test
    fun `invalid email shows error`() = runTest {
        viewModel.login("not-an-email", "not-a-real-password")
        assertEquals(LoginUiState.Error("Enter a valid email address"), viewModel.state.value)
    }

    @Test
    fun `valid credentials transition to Success and persist session`() = runTest {
        viewModel.login("test@example.invalid", "not-a-real-password")
        advanceUntilIdle()
        assertEquals(LoginUiState.Success, viewModel.state.value)
        assertTrue(session.isLoggedIn)
    }

    @Test
    fun `logout clears session and resets state`() = runTest {
        viewModel.login("test@example.invalid", "not-a-real-password")
        advanceUntilIdle()
        viewModel.logout()
        assertEquals(LoginUiState.Idle, viewModel.state.value)
        assertTrue(!session.isLoggedIn)
    }
}
