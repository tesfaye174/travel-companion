package com.travelcompanion.di

import android.app.NotificationManager
import android.content.Context
import androidx.room.Room
import com.travelcompanion.BuildConfig
import com.travelcompanion.data.db.AppDatabase
import com.travelcompanion.location.GeofenceProvider
import com.travelcompanion.location.LocationProvider
import com.travelcompanion.location.PlatformGeofenceProvider
import com.travelcompanion.location.PlatformLocationProvider
import com.travelcompanion.location.PlayServicesGeofenceProvider
import com.travelcompanion.location.PlayServicesLocationProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Main Hilt module: database, system services, location/geofence providers.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .addMigrations(
                AppDatabase.MIGRATION_1_2,
                AppDatabase.MIGRATION_2_3,
                AppDatabase.MIGRATION_3_4,
                AppDatabase.MIGRATION_4_5
            )
            .apply {
                // Destructive migration ONLY in debug builds to prevent data loss
                if (BuildConfig.DEBUG) {
                    fallbackToDestructiveMigration()
                }
            }
            .build()
    }

    @Provides
    fun provideLocationProvider(@ApplicationContext context: Context): LocationProvider {
        return if (BuildConfig.USE_PLAY_SERVICES_LOCATION) {
            PlayServicesLocationProvider(context)
        } else {
            PlatformLocationProvider(context)
        }
    }

    @Provides
    fun provideGeofenceProvider(@ApplicationContext context: Context, database: AppDatabase): GeofenceProvider {
        return if (BuildConfig.USE_PLAY_SERVICES_LOCATION) {
            PlayServicesGeofenceProvider(context)
        } else {
            PlatformGeofenceProvider(context, database)
        }
    }
}
