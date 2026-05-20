package com.travelcompanion.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Extension property for DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * DataStore-based preferences management for app settings.
 * Provides type-safe access to user preferences with coroutine-based API.
 */
@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // Preference keys
    companion object {
        val NOTIFY_POI = booleanPreferencesKey("notify_poi")
        val NOTIFY_REMINDERS = booleanPreferencesKey("notify_reminders")
        val AUTO_TRACKING = booleanPreferencesKey("auto_tracking")
        val DISTANCE_UNIT = stringPreferencesKey("distance_unit")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val MEMBER_SINCE = androidx.datastore.preferences.core.longPreferencesKey("member_since_ts")

        // SharedPreferences for fast synchronous theme read on startup
        const val THEME_PREFS = "theme_prefs"
        const val THEME_PREFS_KEY = "theme_mode"
    }

    // Data class for all settings
    data class AppSettings(
        val notifyPoi: Boolean = true,
        val notifyReminders: Boolean = true,
        val autoTracking: Boolean = false,
        val distanceUnit: String = "km",
        val themeMode: String = "system"
    )

    /**
     * Flow of all app settings
     */
    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { preferences ->
        AppSettings(
            notifyPoi = preferences[NOTIFY_POI] ?: true,
            notifyReminders = preferences[NOTIFY_REMINDERS] ?: true,
            autoTracking = preferences[AUTO_TRACKING] ?: false,
            distanceUnit = preferences[DISTANCE_UNIT] ?: "km",
            themeMode = preferences[THEME_MODE] ?: "system"
        )
    }

    /**
     * Flow for POI notifications setting
     */
    val notifyPoiFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NOTIFY_POI] ?: true
    }

    /**
     * Flow for reminders setting
     */
    val notifyRemindersFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NOTIFY_REMINDERS] ?: true
    }

    /**
     * Flow for auto tracking setting
     */
    val autoTrackingFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[AUTO_TRACKING] ?: false
    }

    /**
     * Update POI notifications setting
     */
    suspend fun setNotifyPoi(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFY_POI] = enabled
        }
    }

    /**
     * Update reminders setting
     */
    suspend fun setNotifyReminders(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFY_REMINDERS] = enabled
        }
    }

    /**
     * Update auto tracking setting
     */
    suspend fun setAutoTracking(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_TRACKING] = enabled
        }
    }

    /**
     * Update distance unit setting
     */
    suspend fun setDistanceUnit(unit: String) {
        context.dataStore.edit { preferences ->
            preferences[DISTANCE_UNIT] = unit
        }
    }

    /**
     * Update theme mode setting.
     * Also mirrors to SharedPreferences for fast synchronous reads on startup.
     */
    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode
        }
        // Mirror to SharedPreferences for instant synchronous access in MainActivity
        context.getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(THEME_PREFS_KEY, mode)
            .apply()
    }

    /**
     * Read theme mode synchronously from SharedPreferences (no disk I/O latency like DataStore).
     * Used by MainActivity.onCreate() to avoid ANR from runBlocking on DataStore.
     */
    fun getThemeModeSync(): String {
        return context.getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE)
            .getString(THEME_PREFS_KEY, "system") ?: "system"
    }

    // Profile data flows
    val userNameFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME] ?: ""
    }

    val userEmailFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_EMAIL] ?: ""
    }

    /**
     * Returns the timestamp the user first opened the app. Seeded lazily on first read.
     */
    suspend fun getOrInitMemberSince(): Long {
        val existing = context.dataStore.data.first()[MEMBER_SINCE]
        if (existing != null) return existing
        val now = System.currentTimeMillis()
        context.dataStore.edit { it[MEMBER_SINCE] = now }
        return now
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = name
        }
    }

    suspend fun setUserEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_EMAIL] = email
        }
    }

    suspend fun clearProfile() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_NAME)
            preferences.remove(USER_EMAIL)
        }
    }

    /**
     * Clear all settings (reset to defaults)
     */
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
