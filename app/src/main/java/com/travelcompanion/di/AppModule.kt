package com.travelcompanion.di

import android.content.Context
import androidx.room.Room
import com.travelcompanion.data.db.AppDatabase
import com.travelcompanion.utils.DispatcherProvider
import com.travelcompanion.utils.DispatcherProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton
import javax.inject.Qualifier

import android.app.NotificationManager
import com.travelcompanion.location.GeofenceProvider
import com.travelcompanion.location.LocationProvider
import com.travelcompanion.location.PlayServicesGeofenceProvider
import com.travelcompanion.location.PlayServicesLocationProvider
import com.travelcompanion.location.PlatformGeofenceProvider
import com.travelcompanion.location.PlatformLocationProvider
import com.travelcompanion.BuildConfig

/**
 * Modulo Hilt principale per l'iniezione delle dipendenze.
 *
 * Hilt è il framework DI raccomandato da Google per Android.
 * Questo modulo fornisce le dipendenze "singleton" che esistono
 * per tutta la vita dell'applicazione.
 *
 * @InstallIn(SingletonComponent::class) indica che le dipendenze
 * qui definite vivono a livello di Application (non Activity/Fragment).
 *
 * Dipendenze fornite:
 * - Database Room
 * - NotificationManager
 * - Dispatcher per coroutine
 * - Provider per localizzazione e geofencing
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Fornisce il NotificationManager di sistema per inviare notifiche.
     */
    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    /**
     * Fornisce l'istanza singleton del database Room.
     *
     * @Singleton garantisce che esista una sola istanza del database.
     * fallbackToDestructiveMigration() ricrea il DB se la versione cambia.
     * Questo è accettabile in sviluppo, in produzione userei migrazioni.
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "travel_companion_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    /**
     * Fornisce l'interfaccia DispatcherProvider per i test.
     * Nei test posso iniettare un'implementazione fake che usa TestDispatcher.
     */
    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider {
        return DispatcherProviderImpl()
    }

    // ==================== DISPATCHER QUALIFICATI ====================
    // Uso @Qualifier per distinguere i diversi dispatcher.
    // Questo permette di iniettare il dispatcher specifico dove serve.

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    // ==================== PROVIDER LOCATION ====================

    /**
     * Fornisce il LocationProvider appropriato in base alla configurazione.
     *
     * Se USE_PLAY_SERVICES_LOCATION è true (default), usa Google Play Services.
     * Altrimenti usa il LocationManager di Android (per dispositivi senza Play Services).
     *
     * Questo pattern "Strategy" permette di cambiare implementazione
     * senza modificare il codice che usa LocationProvider.
     */
    @Provides
    fun provideLocationProvider(@ApplicationContext context: Context): LocationProvider {
        return if (BuildConfig.USE_PLAY_SERVICES_LOCATION) {
            PlayServicesLocationProvider(context)
        } else {
            PlatformLocationProvider(context)
        }
    }

    /**
     * Fornisce il GeofenceProvider appropriato.
     * Stessa logica del LocationProvider - supporta dispositivi con e senza Play Services.
     */
    @Provides
    fun provideGeofenceProvider(@ApplicationContext context: Context, database: AppDatabase): GeofenceProvider {
        return if (BuildConfig.USE_PLAY_SERVICES_LOCATION) {
            PlayServicesGeofenceProvider(context)
        } else {
            PlatformGeofenceProvider(context, database)
        }
    }
}

// ==================== QUALIFIER ANNOTATIONS ====================
// Queste annotazioni servono per distinguere le diverse istanze
// dello stesso tipo (CoroutineDispatcher) durante l'iniezione.

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher      // Per operazioni I/O (database, network, file)

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class MainDispatcher    // Per aggiornamenti UI

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DefaultDispatcher // Per computazioni CPU-intensive
