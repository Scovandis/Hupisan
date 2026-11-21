package com.hupisan.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.hupisan.R
import com.hupisan.database.entity.DataModel
import com.hupisan.di.database.RealmDatabase
import com.hupisan.notification.service.NotificationService
import com.hupisan.view.detail_hutang.DetailHutangActivity
import io.github.xilinjia.krdb.ext.query
import java.net.URLEncoder
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

object ReminderScheduler {
    private const val TAG = "ReminderScheduler"

    const val CHANNEL_ID = "hupisan_due_reminder_alarm"
    private const val LEGACY_CHANNEL_ID = "10001"

    const val ACTION_TRIGGER_REMINDER = "com.hupisan.ACTION_TRIGGER_REMINDER"
    const val ACTION_SNOOZE_1H = "com.hupisan.ACTION_SNOOZE_1H"
    const val ACTION_MARK_LUNAS = "com.hupisan.ACTION_MARK_LUNAS"

    const val EXTRA_ITEM_ID = "extra_item_id"
    const val EXTRA_IS_SNOOZE = "extra_is_snooze"

    private const val DEFAULT_REMINDER_TIME = "09:00"
    private const val SNOOZE_ONE_HOUR_MS = 60L * 60L * 1000L

    fun ensureNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: return

            // Remove legacy unnamed channel if present
            try {
                nm.deleteNotificationChannel(LEGACY_CHANNEL_ID)
            } catch (_: Exception) {
            }

            if (nm.getNotificationChannel(CHANNEL_ID) == null) {
                val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()

                val channel = NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.notif_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = context.getString(R.string.notif_channel_desc)
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 400, 250, 400)
                    setSound(alarmSound, audioAttributes)
                }
                nm.createNotificationChannel(channel)
            }
        }
    }

    fun parseReminderTimeMillis(jatuhTempo: String?, jamJatuhTempo: String?): Long? {
        if (jatuhTempo.isNullOrBlank()) return null
        val timePart = if (jamJatuhTempo.isNullOrBlank() || jamJatuhTempo == "-") {
            DEFAULT_REMINDER_TIME
        } else {
            jamJatuhTempo.trim()
        }
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            sdf.isLenient = false
            sdf.parse("${jatuhTempo.trim()} $timePart")?.time
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse reminder date/time: $jatuhTempo $timePart", e)
            null
        }
    }

    fun schedule(context: Context, item: DataModel): Boolean {
        if (item.statusDataSelesai || !item.notif || item.id <= 0) {
            cancel(context, item.id)
            return false
        }
        return schedule(context, item.id, item.jatuhTempo, item.jam_jatuh_tempo)
    }

    fun schedule(
        context: Context,
        itemId: Int,
        jatuhTempo: String?,
        jamJatuhTempo: String?
    ): Boolean {
        if (itemId <= 0) return false
        val targetMillis = parseReminderTimeMillis(jatuhTempo, jamJatuhTempo) ?: return false
        if (targetMillis <= System.currentTimeMillis()) {
            Log.d(TAG, "Skipping past reminder for itemId=$itemId ($jatuhTempo $jamJatuhTempo)")
            return false
        }
        return scheduleAt(context, itemId, targetMillis, isSnooze = false)
    }

    fun scheduleSnooze(
        context: Context,
        itemId: Int,
        delayMillis: Long = SNOOZE_ONE_HOUR_MS
    ): Boolean {
        if (itemId <= 0) return false
        val targetMillis = System.currentTimeMillis() + delayMillis
        return scheduleAt(context, itemId, targetMillis, isSnooze = true)
    }

    private fun scheduleAt(
        context: Context,
        itemId: Int,
        targetMillis: Long,
        isSnooze: Boolean
    ): Boolean {
        ensureNotificationChannel(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            ?: return false
        val pendingIntent = buildAlarmPendingIntent(context, itemId, isSnooze)

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        targetMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        targetMillis,
                        pendingIntent
                    )
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    targetMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    targetMillis,
                    pendingIntent
                )
            }
            Log.d(TAG, "Scheduled alarm itemId=$itemId at $targetMillis (snooze=$isSnooze)")
            true
        } catch (se: SecurityException) {
            Log.w(TAG, "Exact alarm permission denied, falling back to inexact alarm", se)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    targetMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetMillis, pendingIntent)
            }
            true
        }
    }

    fun cancel(context: Context, itemId: Int) {
        if (itemId <= 0) return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val intent = Intent(context, NotificationService::class.java).apply {
            action = ACTION_TRIGGER_REMINDER
            putExtra(EXTRA_ITEM_ID, itemId)
        }
        val flags = PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        val existing = PendingIntent.getBroadcast(context, itemId, intent, flags)
        if (existing != null) {
            alarmManager?.cancel(existing)
            existing.cancel()
            Log.d(TAG, "Cancelled alarm for itemId=$itemId")
        }

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        nm?.cancel(itemId)
    }

    fun rescheduleAll(context: Context) {
        ensureNotificationChannel(context)
        try {
            val realm = RealmDatabase.getRealm()
            val activeItems = realm.query<DataModel>(
                "statusDataSelesai == false AND notif == true"
            ).find()
            var count = 0
            for (item in activeItems) {
                if (schedule(context, item.id, item.jatuhTempo, item.jam_jatuh_tempo)) {
                    count++
                }
            }
            Log.d(TAG, "Rescheduled $count active reminders out of ${activeItems.size}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to reschedule reminders", e)
        }
    }

    fun showReminderNotification(context: Context, item: DataModel) {
        ensureNotificationChannel(context)
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            ?: return

        val jenisLabel = if (item.jenisPinjaman) {
            context.getString(R.string.piutang)
        } else {
            context.getString(R.string.hutang)
        }

        val decimalFormat = DecimalFormat("##,###,###")
        val amountText = context.getString(R.string.prefix_rp) + decimalFormat.format(item.jumlah)
        val cleanDesc = item.deskripsi.trim().takeIf { it.isNotEmpty() && it != "-" }

        val title = context.getString(R.string.notif_title_format, jenisLabel, item.username)
        val body = if (cleanDesc != null) {
            context.getString(
                R.string.notif_body_with_desc_format,
                amountText,
                item.jatuhTempo,
                cleanDesc
            )
        } else {
            context.getString(R.string.notif_body_format, amountText, item.jatuhTempo)
        }

        val piFlags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        // Tap notification -> open DetailHutangActivity directly for this item
        val detailIntent = Intent(context, DetailHutangActivity::class.java).apply {
            putExtra(DetailHutangActivity.EXTRA_UID, item.id)
            putExtra(DetailHutangActivity.EXTRA_TYPE_JENIS, item.jenisPinjaman)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val detailPendingIntent = PendingIntent.getActivity(
            context,
            item.id * 10 + 1,
            detailIntent,
            piFlags
        )

        // Action 1: Snooze 1 hour
        val snoozeIntent = Intent(context, NotificationService::class.java).apply {
            action = ACTION_SNOOZE_1H
            putExtra(EXTRA_ITEM_ID, item.id)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            item.id * 10 + 2,
            snoozeIntent,
            piFlags
        )

        // Action 2: WhatsApp Reminder (distinct wording for Hutang vs Piutang, unique requestCode)
        val descSuffix = if (cleanDesc != null) " ($cleanDesc)" else ""
        val waMessage = if (item.jenisPinjaman) {
            context.getString(
                R.string.wa_reminder_piutang_format,
                item.username,
                amountText,
                item.jatuhTempo,
                descSuffix
            )
        } else {
            context.getString(
                R.string.wa_reminder_hutang_format,
                item.username,
                amountText,
                item.jatuhTempo,
                descSuffix
            )
        }
        val waUrl = "https://api.whatsapp.com/send?text=" + URLEncoder.encode(waMessage, "UTF-8")
        val waIntent = Intent(Intent.ACTION_VIEW, Uri.parse(waUrl)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val waPendingIntent = PendingIntent.getActivity(
            context,
            item.id * 10 + 3,
            waIntent,
            piFlags
        )

        // Action 3: Mark Paid (Lunas)
        val lunasIntent = Intent(context, NotificationService::class.java).apply {
            action = ACTION_MARK_LUNAS
            putExtra(EXTRA_ITEM_ID, item.id)
        }
        val lunasPendingIntent = PendingIntent.getBroadcast(
            context,
            item.id * 10 + 4,
            lunasIntent,
            piFlags
        )

        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_foreground_apps)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(alarmSound)
            .setVibrate(longArrayOf(0, 400, 250, 400))
            .setAutoCancel(true)
            .setContentIntent(detailPendingIntent)
            .addAction(
                R.drawable.ic_hupisan_bell,
                context.getString(R.string.notif_action_snooze_1h),
                snoozePendingIntent
            )
            .addAction(
                R.drawable.ic_baseline_check_24,
                context.getString(R.string.ingatkan_via_wa),
                waPendingIntent
            )
            .addAction(
                R.drawable.ic_hupisan_check_circle,
                context.getString(R.string.notif_action_mark_lunas),
                lunasPendingIntent
            )
            .build()

        try {
            nm.notify(item.id, notification)
        } catch (se: SecurityException) {
            Log.e(TAG, "POST_NOTIFICATIONS permission not granted", se)
        }
    }

    private fun buildAlarmPendingIntent(
        context: Context,
        itemId: Int,
        isSnooze: Boolean
    ): PendingIntent {
        val intent = Intent(context, NotificationService::class.java).apply {
            action = ACTION_TRIGGER_REMINDER
            putExtra(EXTRA_ITEM_ID, itemId)
            putExtra(EXTRA_IS_SNOOZE, isSnooze)
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getBroadcast(context, itemId, intent, flags)
    }
}
