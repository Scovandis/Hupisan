package com.hupisan.notification.service

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.content.IntentCompat
import com.hupisan.view.main.MainActivity.Companion.NOTIFICATION_CHANNEL_ID

class NotificationService : BroadcastReceiver() {
    companion object {
        const val NOTIFICATION_ID = "notification-id"
        const val NOTIFICATION = "notification"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

        val notification: Notification = IntentCompat.getParcelableExtra(intent, NOTIFICATION, Notification::class.java)
            ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "NOTIFICATION_CHANNEL_NAME",
                importance
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val id = intent.getIntExtra(NOTIFICATION_ID, 0)
        notificationManager.notify(id, notification)
    }
}