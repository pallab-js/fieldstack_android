package com.fieldstack.android

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MvpFlowTest {

    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1) val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() = hiltRule.inject()

    @Test
    fun loginScreen_isDisplayedOnFirstLaunch() {
        composeRule.onNodeWithContentDescription("Login screen").assertIsDisplayed()
    }

    @Test
    fun login_withValidCredentials_navigatesToDashboard() {
        composeRule.onNodeWithContentDescription("Email input").performTextInput("test@example.invalid")
        composeRule.onNodeWithContentDescription("Password input").performTextInput("not-a-real-password")
        composeRule.onNodeWithText("Sign In").performClick()
        composeRule.waitUntil(3_000) {
            composeRule.onAllNodes(
                androidx.compose.ui.test.hasText("Dashboard")
            ).fetchSemanticsNodes().isNotEmpty()
        }
    }

    // ── Accessibility checks ───────────────────────────────────────────────

    @Test
    fun loginScreen_emailInput_hasContentDescription() {
        composeRule.onNodeWithContentDescription("Email input").assertIsDisplayed()
    }

    @Test
    fun loginScreen_passwordInput_hasContentDescription() {
        composeRule.onNodeWithContentDescription("Password input").assertIsDisplayed()
    }

    @Test
    fun loginScreen_signInButton_isAccessible() {
        composeRule.onNodeWithText("Sign In").assertIsDisplayed()
    }
}
