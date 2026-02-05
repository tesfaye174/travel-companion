package com.travelcompanion.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Modulino semplice: fornisce i DAO (es. `TripDao`) usando l'istanza di `AppDatabase`.
// Nota per lo studente: preferisco separare i provider per chiarezza e testabilità.
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideTripDao(database: com.travelcompanion.data.db.AppDatabase): com.travelcompanion.data.db.dao.TripDao =
        database.tripDao()
}
