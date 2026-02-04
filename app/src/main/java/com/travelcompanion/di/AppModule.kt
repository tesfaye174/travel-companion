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
 * Modulo Hilt per l'injection delle dipendenze.
 * Qui definisco come creare le varie istanze che servono nell'app:
 * database, notification manager, dispatcher per le coroutine, ecc.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    // creo il database room, uso fallbackToDestructiveMigration
    // perchè in sviluppo mi fa comodo ricreare il db se cambio schema
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "travel_companion_db"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider {
        return DispatcherProviderImpl()
    }

    // dispatcher per le coroutine, li separo cosi posso testarli

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    // provider per la location - uso play services se disponibile,
    // altrimenti il location manager di android base
    @Provides
    fun provideLocationProvider(@ApplicationContext context: Context): LocationProvider {
        return if (BuildConfig.USE_PLAY_SERVICES_LOCATION) {
            PlayServicesLocationProvider(context)
        } else {
            PlatformLocationProvider(context)
        }
    }

    // stessa cosa per il geofencing
    @Provides
    fun provideGeofenceProvider(@ApplicationContext context: Context, database: AppDatabase): GeofenceProvider {
        return if (BuildConfig.USE_PLAY_SERVICES_LOCATION) {
            PlayServicesGeofenceProvider(context)
        } else {
            PlatformGeofenceProvider(context, database)
        }
    }
}

// annotation per distinguere i vari dispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class MainDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DefaultDispatcher
