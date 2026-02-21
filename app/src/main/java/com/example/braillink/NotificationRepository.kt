package com.example.braillink

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object NotificationRepository {
    private val _latestMessage = MutableStateFlow("")
    val latestMessage: StateFlow<String> = _latestMessage

    fun updateMessage(message: String) {
        _latestMessage.value = message
    }
}