package com.example.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.Calendar

object RetentionNotificationScheduler {

    const val CHANNEL_ID = "fintax_retention_channel"
    const val CHANNEL_NAME = "FinTax Daily Finance Digest"
    const val EXTRA_NOTIFICATION_TYPE = "extra_notification_type"

    const val TYPE_MORNING = "TYPE_MORNING"
    const val TYPE_EVENING = "TYPE_EVENING"
    const val TYPE_CRITICAL_NEWS = "TYPE_CRITICAL_NEWS"

    const val REQUEST_CODE_MORNING = 1001
    const val REQUEST_CODE_EVENING = 1002
    const val REQUEST_CODE_CRITICAL = 1003

    fun setupNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Daily 60-second personal finance & tax digest retention notifications"
                enableVibration(true)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleDailyNotifications(context: Context) {
        setupNotificationChannel(context)

        // Schedule Morning Digest at 8:30 AM
        scheduleAlarm(
            context = context,
            requestCode = REQUEST_CODE_MORNING,
            notificationType = TYPE_MORNING,
            hourOfDay = 8,
            minute = 30
        )

        // Schedule Evening Digest at 7:00 PM (19:00)
        scheduleAlarm(
            context = context,
            requestCode = REQUEST_CODE_EVENING,
            notificationType = TYPE_EVENING,
            hourOfDay = 19,
            minute = 0
        )

        // Schedule Critical News at 1:00 PM (13:00)
        scheduleAlarm(
            context = context,
            requestCode = REQUEST_CODE_CRITICAL,
            notificationType = TYPE_CRITICAL_NEWS,
            hourOfDay = 13,
            minute = 0
        )

        Log.d("RetentionScheduler", "Daily Morning, Afternoon Critical, and Evening retention alarms set.")
    }

    private fun scheduleAlarm(
        context: Context,
        requestCode: Int,
        notificationType: String,
        hourOfDay: Int,
        minute: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, RetentionNotificationReceiver::class.java).apply {
            putExtra(EXTRA_NOTIFICATION_TYPE, notificationType)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // If time already passed today, set for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            Log.e("RetentionScheduler", "Exact alarm permission error, falling back to inexact repeating", e)
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }
    }

    fun sendInstantTestNotification(context: Context, type: String) {
        setupNotificationChannel(context)
        val intent = Intent(context, RetentionNotificationReceiver::class.java).apply {
            putExtra(EXTRA_NOTIFICATION_TYPE, type)
        }
        context.sendBroadcast(intent)
    }
}
