package com.example.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationHelper(private val context: Context) {
    companion object {
        private const val CHANNEL_ALARM_ID = "nextime_alarms"
        private const val CHANNEL_ALARM_NAME = "NexTime Active Alarms"
        private const val CHANNEL_FOCUS_ID = "nextime_focus"
        private const val CHANNEL_FOCUS_NAME = "NexTime Focus Pulse"
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val alarmChannel = NotificationChannel(
                CHANNEL_ALARM_ID,
                CHANNEL_ALARM_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Triggered when active alarms fire off in NexTime 3D"
                enableVibration(true)
                setVibrationPattern(longArrayOf(0, 600, 150, 600, 150, 600))
            }

            val focusChannel = NotificationChannel(
                CHANNEL_FOCUS_ID,
                CHANNEL_FOCUS_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Pomodoro updates and focus session completed notifications"
                enableVibration(true)
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(alarmChannel)
            manager.createNotificationChannel(focusChannel)
        }
    }

    fun triggerAlarmNotification(title: String, body: String, alarmId: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, CHANNEL_ALARM_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 600, 150, 600, 150, 600))
            .build()

        manager.notify(alarmId, notification)
    }

    fun triggerFocusCompletedNotification(title: String, body: String) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, CHANNEL_FOCUS_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        manager.notify(999, notification)
    }

    fun cancelNotification(id: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(id)
    }
}
