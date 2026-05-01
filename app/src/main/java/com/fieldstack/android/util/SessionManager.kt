package com.fieldstack.android.util

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.fieldstack.android.domain.model.UserRole
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(@ApplicationContext private val ctx: Context) {

    private val prefs = EncryptedSharedPreferences.create(
        ctx,
        "fieldstack_session",
        MasterKey.Builder(ctx).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    var token: String?
        get() = prefs.getString(KEY_TOKEN, null)
        set(v) = prefs.edit().putString(KEY_TOKEN, v).apply()

    var userId: String?
        get() = prefs.getString(KEY_USER_ID, null)
        set(v) = prefs.edit().putString(KEY_USER_ID, v).apply()

    var userName: String?
        get() = prefs.getString(KEY_USER_NAME, null)
        set(v) = prefs.edit().putString(KEY_USER_NAME, v).apply()

    var userEmail: String?
        get() = prefs.getString(KEY_USER_EMAIL, null)
        set(v) = prefs.edit().putString(KEY_USER_EMAIL, v).apply()

    // Fix #6: persisted lockout state
    var failedLoginAttempts: Int
        get() = prefs.getInt(KEY_FAILED_ATTEMPTS, 0)
        set(v) = prefs.edit().putInt(KEY_FAILED_ATTEMPTS, v).apply()

    var lockedUntilMs: Long
        get() = prefs.getLong(KEY_LOCKED_UNTIL, 0L)
        set(v) = prefs.edit().putLong(KEY_LOCKED_UNTIL, v).apply()

    // Fix #2: role is read from JWT claims, not from a plain response field
    val userRole: UserRole
        get() = jwtClaim(token, "role")
            ?.let { runCatching { UserRole.valueOf(it) }.getOrNull() }
            ?: UserRole.FieldTech

    // Fix #1: token is only valid if present AND not expired per JWT exp claim
    val isLoggedIn: Boolean
        get() {
            val t = token ?: return false
            return !isTokenExpired(t)
        }

    fun clear() {
        prefs.edit().clear().apply()
        ctx.cacheDir.listFiles()?.forEach { it.delete() }
        ctx.filesDir.listFiles { f ->
            (f.name.startsWith("insights_") && f.name.endsWith(".csv")) ||
            (f.name.startsWith("report_") && f.name.endsWith(".pdf"))
        }?.forEach { it.delete() }
    }

    private fun isTokenExpired(jwt: String): Boolean {
        val exp = jwtClaim(jwt, "exp")?.toLongOrNull() ?: return true
        return System.currentTimeMillis() / 1000 >= exp
    }

    companion object {
        private const val KEY_TOKEN           = "auth_token"
        private const val KEY_USER_ID         = "user_id"
        private const val KEY_USER_NAME       = "user_name"
        private const val KEY_USER_EMAIL      = "user_email"
        private const val KEY_FAILED_ATTEMPTS = "failed_attempts"
        private const val KEY_LOCKED_UNTIL    = "locked_until"

        /** Decodes a JWT payload and returns the string value of [claim], or null on any error. */
        fun jwtClaim(jwt: String?, claim: String): String? {
            jwt ?: return null
            return try {
                val payload = jwt.split(".").getOrNull(1) ?: return null
                val decoded = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_PADDING)
                JSONObject(String(decoded)).optString(claim).takeIf { it.isNotEmpty() }
            } catch (e: Exception) {
                android.util.Log.w("SessionManager", "Failed to parse JWT claim '$claim'", e)
                null
            }
        }
    }
}
