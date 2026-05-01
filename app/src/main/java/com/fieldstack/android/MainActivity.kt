package com.fieldstack.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.fieldstack.android.ui.auth.AuthViewModel
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
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BudgetZenTheme {
                FieldStackNavHost()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Surface token expiry as an explicit message rather than a silent logout.
        authViewModel.checkSession()

        if (!session.isLoggedIn) return
        lifecycleScope.launch {
            val biometricEnabled = prefs.biometricEnabled.first()
            if (biometricEnabled && BiometricHelper.isAvailable(this@MainActivity)) {
                BiometricHelper.prompt(
                    activity = this@MainActivity,
                    onSuccess = { /* proceed normally */ },
                    onFail = {
                        session.clear()
                        finish()
                    },
                )
            }
        }
    }
}
