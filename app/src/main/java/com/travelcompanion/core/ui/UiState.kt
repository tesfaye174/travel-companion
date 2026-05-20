package com.travelcompanion.core.ui

/**
 * Generic UI state wrapper for ViewModels.
 * Provides consistent state management across the app.
 *
 * @param T The type of data to hold in Success state
 */
sealed class UiState<out T> {
    /**
     * Initial state before any operation.
     */
    object Idle : UiState<Nothing>()

    /**
     * Loading state during async operations.
     */
    object Loading : UiState<Nothing>()

    /**
     * Success state with data.
     */
    data class Success<T>(val data: T) : UiState<T>()

    /**
     * Error state with exception and optional message.
     */
    data class Error(
        val exception: Throwable,
        val message: String? = exception.message
    ) : UiState<Nothing>()

    /**
     * Checks if the state is Success.
     */
    val isSuccess: Boolean
        get() = this is Success

    /**
     * Checks if the state is Error.
     */
    val isError: Boolean
        get() = this is Error

    /**
     * Checks if the state is Loading.
     */
    val isLoading: Boolean
        get() = this is Loading

    /**
     * Checks if the state is Idle.
     */
    val isIdle: Boolean
        get() = this is Idle

    /**
     * Returns data if Success, null otherwise.
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    /**
     * Maps the success value to a new type.
     */
    inline fun <R> map(transform: (T) -> R): UiState<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> this
        is Idle -> this
    }

    /**
     * Performs action if Success.
     */
    inline fun onSuccess(action: (T) -> Unit): UiState<T> {
        if (this is Success) action(data)
        return this
    }

    /**
     * Performs action if Error.
     */
    inline fun onError(action: (Throwable, String?) -> Unit): UiState<T> {
        if (this is Error) action(exception, message)
        return this
    }

    /**
     * Performs action if Loading.
     */
    inline fun onLoading(action: () -> Unit): UiState<T> {
        if (this is Loading) action()
        return this
    }

    companion object {
        /**
         * Creates a UiState by executing the given block, catching any exceptions.
         */
        inline fun <T> runCatching(block: () -> T): UiState<T> = try {
            Success(block())
        } catch (e: Throwable) {
            Error(e)
        }
    }
}

/**
 * Extension to convert Flow<T> to Flow<UiState<T>> with loading and error handling.
 */
fun <T> kotlinx.coroutines.flow.Flow<T>.asUiState(): kotlinx.coroutines.flow.Flow<UiState<T>> {
    return kotlinx.coroutines.flow.flow {
        emit(UiState.Loading)
        try {
            collect { value ->
                emit(UiState.Success(value))
            }
        } catch (e: Throwable) {
            emit(UiState.Error(e))
        }
    }
}
