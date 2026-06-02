package com.example.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.utils.NotificationHelper

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) return
        val alarmId = intent.getIntExtra("ALARM_ID", -1)
        val title = intent.getStringExtra("ALARM_TITLE") ?: "NexTime Alarm"
        val category = intent.getStringExtra("ALARM_CATEGORY") ?: "Morning"

        Log.d("AlarmReceiver", "Alarm Fired! ID: $alarmId, Header: $title")

        val notificationHelper = NotificationHelper(context)
        notificationHelper.triggerAlarmNotification(
            title = "⏰ NexTime 3D: $title",
            body = "[$category Mode] Wake up and master your life. Offline AI Smart Watch OS says: Stand up and unlock your goals!",
            alarmId = alarmId
        )
    }
}
