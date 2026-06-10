package com.travelcompanion.core.ui

/**
 * Wrapper generico dello stato UI per i ViewModel, con supporto per load, success, error e idle.
 */
sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(
        val exception: Throwable,
        val message: String? = exception.message
    ) : UiState<Nothing>()

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error

    val isLoading: Boolean
        get() = this is Loading

    val isIdle: Boolean
        get() = this is Idle

    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    inline fun <R> map(transform: (T) -> R): UiState<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> this
        is Idle -> this
    }

    inline fun onSuccess(action: (T) -> Unit): UiState<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (Throwable, String?) -> Unit): UiState<T> {
        if (this is Error) action(exception, message)
        return this
    }

    inline fun onLoading(action: () -> Unit): UiState<T> {
        if (this is Loading) action()
        return this
    }

    companion object {
        inline fun <T> runCatching(block: () -> T): UiState<T> = try {
            Success(block())
        } catch (e: Throwable) {
            Error(e)
        }
    }
}

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
