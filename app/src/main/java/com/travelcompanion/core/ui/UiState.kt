package com.travelcompanion.core.ui

/**
 * Wrapper generico dello stato UI per i ViewModel: idle, caricamento, successo o errore.
 */
sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(
        val exception: Throwable,
        val message: String? = exception.message
    ) : UiState<Nothing>()
}
