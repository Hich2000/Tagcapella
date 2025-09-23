package com.hich2000.tagcapella.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object ToastEventBus {
    private val _toastFlow = MutableSharedFlow<String>(replay = 0)
    val toastFlow = _toastFlow.asSharedFlow()

    suspend fun send(message: String) {
        _toastFlow.emit(message)
    }
}