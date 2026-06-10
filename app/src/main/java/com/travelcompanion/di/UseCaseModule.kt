package com.travelcompanion.di

import com.travelcompanion.domain.usecase.AnalyzePredictionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Modulo Hilt per i UseCase. Lo scope ViewModelScoped garantisce che ogni ViewModel
 * riceva la propria istanza, evitando condivisione di stato non intenzionale.
 */
@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    @ViewModelScoped
    fun provideAnalyzePredictionUseCase(): AnalyzePredictionUseCase {
        return AnalyzePredictionUseCase()
    }
}
