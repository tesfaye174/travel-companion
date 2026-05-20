package com.travelcompanion.di

import com.travelcompanion.domain.usecase.AnalyzePredictionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Hilt module for providing UseCase instances.
 * UseCases encapsulate business logic and are scoped to ViewModels.
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
