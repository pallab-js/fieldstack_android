package com.fieldstack.android.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("app_prefs")

@Singleton
class AppPrefsStore @Inject constructor(@ApplicationContext private val ctx: Context) {

    private val WIFI_ONLY      = booleanPreferencesKey("wifi_only_sync")
    private val BIOMETRIC      = booleanPreferencesKey("biometric_enabled")
    private val LAST_SYNC_TS   = androidx.datastore.preferences.core.longPreferencesKey("last_sync_ts")

    val wifiOnlySync: Flow<Boolean> = ctx.dataStore.data.map { it[WIFI_ONLY] ?: false }
    val biometricEnabled: Flow<Boolean> = ctx.dataStore.data.map { it[BIOMETRIC] ?: false }
    val lastSyncTimestamp: Flow<Long?> = ctx.dataStore.data.map { it[LAST_SYNC_TS] }

    suspend fun setWifiOnly(v: Boolean) { ctx.dataStore.edit { it[WIFI_ONLY] = v } }
    suspend fun setBiometric(v: Boolean) { ctx.dataStore.edit { it[BIOMETRIC] = v } }
    suspend fun setLastSyncTimestamp(ts: Long) { ctx.dataStore.edit { it[LAST_SYNC_TS] = ts } }
}
