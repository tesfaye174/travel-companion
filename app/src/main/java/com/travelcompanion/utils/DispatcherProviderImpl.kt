package com.travelcompanion.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DispatcherProviderImpl : DispatcherProvider {
    override val Main: CoroutineDispatcher = Dispatchers.Main
    override val IO: CoroutineDispatcher = Dispatchers.IO
    override val Default: CoroutineDispatcher = Dispatchers.Default
}
