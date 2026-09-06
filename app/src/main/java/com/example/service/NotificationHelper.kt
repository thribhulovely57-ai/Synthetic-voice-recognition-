package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity

object NotificationHelper {

    const val CHANNEL_PROTECTION_ID = "voiceshield_protection_status"
    const val CHANNEL_FRAUD_ALERTS_ID = "voiceshield_fraud_critical_alerts"

    const val NOTIFICATION_ID_PROTECTION = 1001
    const val NOTIFICATION_ID_FRAUD_ALERT = 1002
    const val NOTIFICATION_ID_AI_VOICE_ALERT = 1003

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // 1. Persistent Protection Channel (Low/Default Importance, non-intrusive)
            val protectionChannel = NotificationChannel(
                CHANNEL_PROTECTION_ID,
                "Synthetic Voice Recognition Protection Status",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows persistent status when Synthetic Voice Recognition call protection is active"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(protectionChannel)

            // 2. High-Priority Fraud Alert Channel (High Importance, heads-up)
            val alertChannel = NotificationChannel(
                CHANNEL_FRAUD_ALERTS_ID,
                "Synthetic Voice Recognition Fraud Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Urgent warnings when high-risk fraud patterns or synthetic AI voice clones are detected"
                enableVibration(true)
                setShowBadge(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(alertChannel)
        }
    }

    /**
     * Builds the persistent notification for the Foreground Service
     */
    fun buildProtectionServiceNotification(context: Context, isProtectionActive: Boolean): Notification {
        createNotificationChannels(context)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (isProtectionActive) "Synthetic Voice Recognition Active" else "Synthetic Voice Recognition Paused"
        val content = if (isProtectionActive)
            "Monitoring for suspicious incoming calls & synthetic audio"
        else
            "Call sentry on standby. Tap to open and resume protection."

        return NotificationCompat.Builder(context, CHANNEL_PROTECTION_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(isProtectionActive)
            .setContentIntent(openAppPendingIntent)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    /**
     * Displays a High-Priority Fraud Alert notification with the 3 required actions:
     * 1. Open VoiceShield
     * 2. Mark as Safe
     * 3. View Analysis
     */
    fun showFraudAlertNotification(
        context: Context,
        riskScore: Int,
        warningReason: String,
        callerName: String = "Incoming / Active Call"
    ) {
        createNotificationChannels(context)

        // Action 1: Open VoiceShield
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            1,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action 2: Mark as Safe
        val markSafeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "com.example.ACTION_MARK_SAFE"
        }
        val markSafePendingIntent = PendingIntent.getBroadcast(
            context,
            2,
            markSafeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action 3: View Analysis
        val viewAnalysisIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "live_call")
        }
        val viewAnalysisPendingIntent = PendingIntent.getActivity(
            context,
            3,
            viewAnalysisIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_FRAUD_ALERTS_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("⚠ Potential Fraud Detected")
            .setContentText("High Risk ($riskScore/100): $warningReason")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Caller: $callerName\nHigh Risk ($riskScore/100): $warningReason\nImmediate action recommended to prevent credential loss or unauthorized transfer.")
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(viewAnalysisPendingIntent)
            .addAction(android.R.drawable.ic_menu_view, "Open App", openAppPendingIntent)
            .addAction(android.R.drawable.checkbox_on_background, "Mark as Safe", markSafePendingIntent)
            .addAction(android.R.drawable.ic_menu_info_details, "View Analysis", viewAnalysisPendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_FRAUD_ALERT, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun dismissFraudAlertNotification(context: Context) {
        try {
            NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID_FRAUD_ALERT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Displays exact notification requested:
     * App Header: Synthetic Voice Recognition
     * Title: "Suspicious voice detected"
     * Text: "This call may be using an AI-generated or synthetic voice."
     * Action: "Tap to learn more"
     */
    fun showAiVoiceDetectedNotification(
        context: Context,
        callerNumber: String = "+91 98765 43210",
        callerName: String = "Unknown Number"
    ) {
        createNotificationChannels(context)

        // Action: Tap to learn more -> Opens app directly to the forensic breakdown
        val learnMoreIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "live_call")
            putExtra("open_learn_more", true)
        }
        val learnMorePendingIntent = PendingIntent.getActivity(
            context,
            4,
            learnMoreIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_FRAUD_ALERTS_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setSubText("Synthetic Voice Recognition")
            .setContentTitle("Suspicious voice detected")
            .setContentText("This call may be using an AI-generated or synthetic voice.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("This call may be using an AI-generated or synthetic voice.\n\nCaller: $callerName ($callerNumber)\nTap to learn more about acoustic anomalies and safety precautions.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setAutoCancel(true)
            .setContentIntent(learnMorePendingIntent)
            .addAction(android.R.drawable.ic_menu_info_details, "Tap to learn more", learnMorePendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_AI_VOICE_ALERT, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun dismissAiVoiceNotification(context: Context) {
        try {
            NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID_AI_VOICE_ALERT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
