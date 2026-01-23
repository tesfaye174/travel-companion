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
     * Update theme mode setting
     */
    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode
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
