package com.example.core

import java.util.Calendar
import java.util.concurrent.TimeUnit

object SleepAnalyzer {

    fun analyzeSleep(sleepTimeMillis: Long, wakeTimeMillis: Long, disruptionsCount: Int): Int {
        val durationMs = wakeTimeMillis - sleepTimeMillis
        if (durationMs <= 0) return 0

        val hoursOfSleep = TimeUnit.MILLISECONDS.toHours(durationMs).toDouble() + 
                (TimeUnit.MILLISECONDS.toMinutes(durationMs) % 60) / 60.0

        // Duration score (max 50 points)
        val durationScore = when {
            hoursOfSleep in 7.0..9.0 -> 50
            hoursOfSleep in 6.0..7.0 || hoursOfSleep in 9.0..10.0 -> 40
            hoursOfSleep in 5.0..6.0 || hoursOfSleep in 10.0..11.0 -> 25
            else -> 10
        }

        // Disruption penalty (30 points starting balance)
        val disruptionPenalty = disruptionsCount * 5
        val disruptionScore = (30 - disruptionPenalty).coerceAtLeast(0)

        // Bedtime scheduling score (max 20 points)
        val calendar = Calendar.getInstance().apply { timeInMillis = sleepTimeMillis }
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val consistencyScore = when {
            hour in 21..23 -> 20
            hour == 0 || hour == 20 -> 15
            hour in 1..4 -> 5
            else -> 10
        }

        return (durationScore + disruptionScore + consistencyScore).coerceIn(0, 100)
    }

    fun getSleepRecommendation(score: Int): String {
        return when {
            score >= 90 -> "Superior restoration! Your offline biometric alignment is at peak efficiency."
            score >= 80 -> "Optimal recovery. Circadian rhythm synchronized with natural cycles."
            score >= 70 -> "Moderate rest. Consider dimming room lights 45 minutes before bedtime."
            score >= 50 -> "Mild sleep shift. Try shifting your bedtime 30 minutes earlier."
            else -> "Rest deficit! Reduce caffeine/screen use in the evening to protect deep sleep."
        }
    }
}
