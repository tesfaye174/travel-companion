package com.travelcompanion.utils

import kotlinx.coroutines.CoroutineDispatcher

interface DispatcherProvider {
    val Main: CoroutineDispatcher
    val IO: CoroutineDispatcher
    val Default: CoroutineDispatcher
}
