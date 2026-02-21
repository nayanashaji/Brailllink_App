package com.example.braillink

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class BraillinkNotificationListener : NotificationListenerService() {

    private var isFirstLoad = true

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.e("Braillink", "LISTENER CONNECTED")
        isFirstLoad = true
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {

        if (isFirstLoad) {
            isFirstLoad = false
            return   // skip existing notifications
        }

        val extras = sbn.notification.extras
        val title = extras.getCharSequence("android.title")?.toString() ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        val fullMessage = "$title $text".trim()

        if (fullMessage.isNotBlank()) {
            NotificationRepository.updateMessage(fullMessage)

        }
    }
}