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

// Extension property che crea il DataStore come delegate.
// "settings" è il nome del file in cui verranno salvate le preferenze.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Gestione delle preferenze utente con DataStore.
 *
 * Ho scelto DataStore invece di SharedPreferences perché:
 * - È completamente asincrono (basato su coroutine)
 * - È type-safe (non posso leggere un Int come String)
 * - Non blocca mai il main thread
 * - Gestisce automaticamente la consistenza dei dati
 *
 * Ogni impostazione è esposta come Flow per permettere
 * alla UI di osservare i cambiamenti in tempo reale.
 *
 * @see <a href="https://developer.android.com/topic/libraries/architecture/datastore">DataStore</a>
 */
@Singleton
class SettingsDataStore @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    // ==================== CHIAVI DELLE PREFERENZE ====================
    // Definisco le chiavi nel companion object per poterle usare anche
    // dall'esterno se necessario (es. nei test)
    companion object {
        val NOTIFY_POI = booleanPreferencesKey("notify_poi")
        val NOTIFY_REMINDERS = booleanPreferencesKey("notify_reminders")
        val AUTO_TRACKING = booleanPreferencesKey("auto_tracking")
        val DISTANCE_UNIT = stringPreferencesKey("distance_unit")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    /**
     * Data class che rappresenta tutte le impostazioni dell'app.
     * Utile per leggere tutte le impostazioni in un colpo solo.
     */
    data class AppSettings(
        val notifyPoi: Boolean = true,        // Notifiche punti di interesse
        val notifyReminders: Boolean = true,  // Promemoria viaggi
        val autoTracking: Boolean = false,    // Tracking automatico all'avvio
        val distanceUnit: String = "km",      // Unità di misura (km/mi)
        val themeMode: String = "system"      // Tema app (system/light/dark)
    )

    // ==================== FLOW DI LETTURA ====================

    /**
     * Flow che emette tutte le impostazioni ogni volta che cambiano.
     * L'operatore Elvis (?:) fornisce i valori di default.
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

    /** Flow per la singola impostazione notifiche POI */
    val notifyPoiFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NOTIFY_POI] ?: true
    }

    /** Flow per la singola impostazione promemoria */
    val notifyRemindersFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NOTIFY_REMINDERS] ?: true
    }

    /** Flow per la singola impostazione auto-tracking */
    val autoTrackingFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[AUTO_TRACKING] ?: false
    }

    // ==================== FUNZIONI DI SCRITTURA ====================
    // Tutte le funzioni di scrittura sono suspend perché DataStore
    // esegue le operazioni in modo asincrono su disco.

    /** Abilita/disabilita le notifiche per punti di interesse vicini */
    suspend fun setNotifyPoi(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFY_POI] = enabled
        }
    }

    /** Abilita/disabilita i promemoria per i viaggi */
    suspend fun setNotifyReminders(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFY_REMINDERS] = enabled
        }
    }

    /** Abilita/disabilita il tracking GPS automatico */
    suspend fun setAutoTracking(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_TRACKING] = enabled
        }
    }

    /** Imposta l'unità di misura per le distanze ("km" o "mi") */
    @Suppress("unused")
    suspend fun setDistanceUnit(unit: String) {
        context.dataStore.edit { preferences ->
            preferences[DISTANCE_UNIT] = unit
        }
    }

    /** Imposta il tema dell'app ("system", "light" o "dark") */
    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode
        }
    }

    /**
     * Resetta tutte le impostazioni ai valori di default.
     * Utile per la funzione "Ripristina impostazioni" nelle settings.
     */
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
