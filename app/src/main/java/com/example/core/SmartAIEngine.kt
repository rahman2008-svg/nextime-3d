package com.example.core

import com.example.data.AlarmModel
import com.example.data.SleepRecord

object SmartAIEngine {
    fun generateSuggestions(alarms: List<AlarmModel>, sleepRecords: List<SleepRecord>): List<String> {
        val suggestions = mutableListOf<String>()

        if (alarms.isEmpty()) {
            suggestions.add("🚀 You have no scheduled alarms! Set a premium 'Morning' alarm to align with your body clock.")
            return suggestions
        }

        // Rule 1: Screen lag / Weekend sleep lag
        val hasWeekendLateAlarms = alarms.any { alarm ->
            val days = alarm.getRepeatDaysList()
            val isWeekend = days.isNotEmpty() && days.all { it == 1 || it == 7 }
            isWeekend && alarm.hour >= 9
        }
        if (hasWeekendLateAlarms) {
            suggestions.add("💡 Sleep Sync Alert: Weekday/weekend offset detected. We suggest moving weekend alarms closer to weekday wakeup to balance sleep debt.")
        }

        // Rule 2: Snoozing clusters
        val morningAlarms = alarms.filter { it.isEnabled && it.hour in 5..8 }
        if (morningAlarms.size >= 3) {
            suggestions.add("⚠️ Chronotype Inertia: You have ${morningAlarms.size} close morning alarms. Adjusting snooze volume intensity is recommended.")
        }

        // Rule 3: Focus sync suggestion
        val hasStudyFocusAlarm = alarms.any { 
            it.category.equals("Study", ignoreCase = true) || 
            it.title.contains("Study", ignoreCase = true) ||
            it.title.contains("Focus", ignoreCase = true)
        }
        if (!hasStudyFocusAlarm) {
            suggestions.add("📚 Routine Optimizer: No quiet deep-work timers scheduled. Add a dedicated 'Study' block to cultivate your focus routine.")
        }

        // Rule 4: Sleep Score integration
        if (sleepRecords.isNotEmpty()) {
            val avgScore = sleepRecords.map { it.sleepScore }.average().toInt()
            if (avgScore < 75) {
                suggestions.add("🌙 Bedtime warning: Your average sleep score ($avgScore%) indicates fragmented sleep. Try going to sleep 30 minutes earlier.")
            } else {
                suggestions.add("🌟 Circadian alignment optimum ($avgScore%): Your sleep health is on point! Maintain this streak.")
            }
        } else {
            suggestions.add("📊 NexVora Engine: Stand by, logs are initializing. Enter bedtime records below to receive personalized insights.")
        }

        return suggestions
    }
}
