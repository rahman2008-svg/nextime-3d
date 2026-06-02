package com.example.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.data.AppDatabase
import com.example.data.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Device rebooted. Re-scheduling NexTime alarms...")
            val db = AppDatabase.getDatabase(context)
            val repository = AppRepository(db.appDao())
            val alarmEngine = AlarmEngine(context)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val alarms = repository.allAlarms.first()
                    for (alarm in alarms) {
                        if (alarm.isEnabled) {
                            alarmEngine.scheduleAlarm(alarm)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("BootReceiver", "Error re-scheduling alarms on boot", e)
                }
            }
        }
    }
}
