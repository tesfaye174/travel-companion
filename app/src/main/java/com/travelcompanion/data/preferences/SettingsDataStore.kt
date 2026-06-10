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

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Gestione preferenze con DataStore.
 */
@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        val NOTIFY_POI = booleanPreferencesKey("notify_poi")
        val NOTIFY_REMINDERS = booleanPreferencesKey("notify_reminders")
        val AUTO_TRACKING = booleanPreferencesKey("auto_tracking")
        val DISTANCE_UNIT = stringPreferencesKey("distance_unit")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val MEMBER_SINCE = androidx.datastore.preferences.core.longPreferencesKey("member_since_ts")

        const val THEME_PREFS = "theme_prefs"
        const val THEME_PREFS_KEY = "theme_mode"
    }

    data class AppSettings(
        val notifyPoi: Boolean = true,
        val notifyReminders: Boolean = true,
        val autoTracking: Boolean = false,
        val distanceUnit: String = "km",
        val themeMode: String = "system"
    )

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { preferences ->
        AppSettings(
            notifyPoi = preferences[NOTIFY_POI] ?: true,
            notifyReminders = preferences[NOTIFY_REMINDERS] ?: true,
            autoTracking = preferences[AUTO_TRACKING] ?: false,
            distanceUnit = preferences[DISTANCE_UNIT] ?: "km",
            themeMode = preferences[THEME_MODE] ?: "system"
        )
    }

    val notifyPoiFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NOTIFY_POI] ?: true
    }

    val notifyRemindersFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NOTIFY_REMINDERS] ?: true
    }

    val autoTrackingFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[AUTO_TRACKING] ?: false
    }

    suspend fun setNotifyPoi(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFY_POI] = enabled
        }
    }

    suspend fun setNotifyReminders(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFY_REMINDERS] = enabled
        }
    }

    suspend fun setAutoTracking(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_TRACKING] = enabled
        }
    }

    suspend fun setDistanceUnit(unit: String) {
        context.dataStore.edit { preferences ->
            preferences[DISTANCE_UNIT] = unit
        }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode
        }
        context.getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(THEME_PREFS_KEY, mode)
            .apply()
    }

    fun getThemeModeSync(): String {
        return context.getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE)
            .getString(THEME_PREFS_KEY, "system") ?: "system"
    }

    val userNameFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME] ?: ""
    }

    val userEmailFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_EMAIL] ?: ""
    }

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

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
