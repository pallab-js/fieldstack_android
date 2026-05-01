package com.fieldstack.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.fieldstack.android.ui.theme.BudgetZenTheme
import com.fieldstack.android.util.AppPrefsStore
import com.fieldstack.android.util.BiometricHelper
import com.fieldstack.android.util.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var session: SessionManager
    @Inject lateinit var prefs: AppPrefsStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BudgetZenTheme {
                FieldStackNavHost()
            }
        }
    }

    // Fix #5: enforce biometric re-auth every time the app comes to the foreground
    override fun onResume() {
        super.onResume()
        if (!session.isLoggedIn) return
        lifecycleScope.launch {
            val biometricEnabled = prefs.biometricEnabled.first()
            if (biometricEnabled && BiometricHelper.isAvailable(this@MainActivity)) {
                BiometricHelper.prompt(
                    activity = this@MainActivity,
                    onSuccess = { /* proceed normally */ },
                    onFail = {
                        // Lock the app by clearing the session and finishing
                        session.clear()
                        finish()
                    },
                )
            }
        }
    }
}
