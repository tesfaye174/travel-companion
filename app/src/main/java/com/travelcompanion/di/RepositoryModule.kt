package com.travelcompanion.di

import com.travelcompanion.data.repository.TripRepository
import com.travelcompanion.domain.repository.ITripRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTripRepository(
        impl: TripRepository
    ): ITripRepository
}
