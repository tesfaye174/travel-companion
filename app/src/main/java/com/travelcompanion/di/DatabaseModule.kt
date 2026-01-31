package com.travelcompanion.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Reuse the existing AppDatabase in the project (no duplicate Room database binding).
// This module provides a direct provider for TripDao to simplify injection where needed.
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideTripDao(database: com.travelcompanion.data.db.AppDatabase): com.travelcompanion.data.db.dao.TripDao =
        database.tripDao()
}
