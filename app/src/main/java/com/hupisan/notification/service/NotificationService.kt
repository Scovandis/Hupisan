package com.hupisan.notification.service

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.hupisan.R
import com.hupisan.database.entity.DataModel
import com.hupisan.di.database.RealmDatabase
import com.hupisan.notification.ReminderScheduler
import io.github.xilinjia.krdb.ext.query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationService : BroadcastReceiver() {

    companion object {
        private const val TAG = "NotificationService"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        val appContext = context.applicationContext

        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED -> {
                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        ReminderScheduler.rescheduleAll(appContext)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error rescheduling alarms on system broadcast", e)
                    } finally {
                        pendingResult.finish()
                    }
                }
            }

            ReminderScheduler.ACTION_TRIGGER_REMINDER -> {
                val itemId = intent.getIntExtra(ReminderScheduler.EXTRA_ITEM_ID, -1)
                if (itemId <= 0) return

                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val realm = RealmDatabase.getRealm()
                        val liveItem = realm.query<DataModel>("id == $0", itemId).first().find()
                        if (liveItem != null && !liveItem.statusDataSelesai && liveItem.notif) {
                            ReminderScheduler.showReminderNotification(appContext, liveItem)
                        } else {
                            Log.d(TAG, "Skipping notification for itemId=$itemId (deleted, settled, or disabled)")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error handling reminder trigger for itemId=$itemId", e)
                    } finally {
                        pendingResult.finish()
                    }
                }
            }

            ReminderScheduler.ACTION_SNOOZE_1H -> {
                val itemId = intent.getIntExtra(ReminderScheduler.EXTRA_ITEM_ID, -1)
                if (itemId <= 0) return

                val nm = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                nm?.cancel(itemId)
                ReminderScheduler.scheduleSnooze(appContext, itemId)
                Toast.makeText(
                    appContext,
                    appContext.getString(R.string.notif_snoozed_toast),
                    Toast.LENGTH_SHORT
                ).show()
            }

            ReminderScheduler.ACTION_MARK_LUNAS -> {
                val itemId = intent.getIntExtra(ReminderScheduler.EXTRA_ITEM_ID, -1)
                if (itemId <= 0) return

                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val realm = RealmDatabase.getRealm()
                        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                        realm.write {
                            val live = query<DataModel>("id == $0", itemId).first().find()
                            if (live != null && !live.statusDataSelesai) {
                                live.jumlah_dibayar += live.jumlah
                                live.jumlah = 0
                                live.statusDataSelesai = true
                                live.tanggal_lunas = today
                            }
                        }
                        ReminderScheduler.cancel(appContext, itemId)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                appContext,
                                appContext.getString(R.string.notif_marked_lunas_toast),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error marking itemId=$itemId as lunas", e)
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
        }
    }
}
