package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return

        when (action) {
            ACTION_MARK_SAFE -> {
                NotificationHelper.dismissFraudAlertNotification(context)
                _actionEvents.tryEmit(NotificationEvent.MarkAsSafe)
            }
            ACTION_DISMISS_ALERT -> {
                NotificationHelper.dismissFraudAlertNotification(context)
                _actionEvents.tryEmit(NotificationEvent.DismissAlert)
            }
        }
    }

    companion object {
        const val ACTION_MARK_SAFE = "com.example.ACTION_MARK_SAFE"
        const val ACTION_DISMISS_ALERT = "com.example.ACTION_DISMISS_ALERT"

        private val _actionEvents = MutableSharedFlow<NotificationEvent>(extraBufferCapacity = 1)
        val actionEvents: SharedFlow<NotificationEvent> = _actionEvents.asSharedFlow()
    }
}

sealed interface NotificationEvent {
    object MarkAsSafe : NotificationEvent
    object DismissAlert : NotificationEvent
}
