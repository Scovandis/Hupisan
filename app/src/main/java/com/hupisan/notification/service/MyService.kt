package com.hupisan.notification.service

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import kotlinx.coroutines.*
import androidx.core.app.NotificationCompat
import com.hupisan.R
import com.hupisan.view.main.MainActivity

class MyService : Service() {
    companion object {
        private const val TAG = "MyService"
        const val NOTIFICATION_CHANNEL_ID = "10001"
        private const val DEFAULT_NOTIFICATION_CHANNEL_ID = "default"
    }

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    override fun onBind(p0: Intent?): IBinder? {
        throw UnsupportedOperationException("Not Yet implementation")
    }

    private fun scheduleNotification(notification: Notification, delay: Int) {
        val notificationIntent = Intent(this, NotificationService::class.java)
        notificationIntent.putExtra(NotificationService.NOTIFICATION_ID, 1)
        notificationIntent.putExtra(NotificationService.NOTIFICATION, notification)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            notificationIntent,
            flags
        )
        val futureInMillis = SystemClock.elapsedRealtime() + delay
        val alarmManager = (getSystemService(ALARM_SERVICE) as AlarmManager)
        alarmManager[AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis] = pendingIntent
    }

    private fun getNotification(content: String): Notification? {
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, DEFAULT_NOTIFICATION_CHANNEL_ID)
        builder.setContentTitle(getString(R.string.jatuh_tempo))
        builder.setContentText(content)
        builder.setSmallIcon(R.drawable.ic_launcher_foreground)
        builder.setAutoCancel(true)
        builder.setChannelId(MainActivity.NOTIFICATION_CHANNEL_ID)
        return builder.build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "SERVICE DIJALANKAN")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
        Log.d(TAG, "onDestroy: ...")
    }
}