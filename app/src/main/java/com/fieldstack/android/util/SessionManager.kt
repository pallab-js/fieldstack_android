package com.fieldstack.android.util

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(@ApplicationContext ctx: Context) {

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

    var userRole: com.fieldstack.android.domain.model.UserRole
        get() = prefs.getString(KEY_ROLE, null)
            ?.let { runCatching { com.fieldstack.android.domain.model.UserRole.valueOf(it) }.getOrNull() }
            ?: com.fieldstack.android.domain.model.UserRole.FieldTech
        set(v) = prefs.edit().putString(KEY_ROLE, v.name).apply()

    val isLoggedIn: Boolean get() = token != null

    fun clear() = prefs.edit().clear().apply()

    companion object {
        private const val KEY_TOKEN     = "auth_token"
        private const val KEY_USER_ID   = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_ROLE      = "user_role"
    }
}
