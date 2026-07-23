package com.example.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

class RetentionNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra(RetentionNotificationScheduler.EXTRA_NOTIFICATION_TYPE)
            ?: RetentionNotificationScheduler.TYPE_MORNING

        val (title, body, notificationId) = when (type) {
            RetentionNotificationScheduler.TYPE_EVENING -> Triple(
                "💳 Is your Credit Card devaluing?",
                "Read 60-sec update on Lounge & Reward changes.",
                2002
            )
            RetentionNotificationScheduler.TYPE_CRITICAL_NEWS -> Triple(
                "🚨 Critical Financial Update",
                "Major market shift or tax policy change detected. Tap to read the breaking news.",
                2003
            )
            else -> Triple(
                "⚡ Today's Finance Digest",
                "New RBI Rules & Tax Updates in 60 seconds.",
                2001
            )
        }

        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, RetentionNotificationScheduler.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notificationBuilder.build())

        // Re-arm for tomorrow
        RetentionNotificationScheduler.scheduleDailyNotifications(context)
    }
}
