package com.example.utils

import java.util.Calendar

object TimeUtils {
    fun formatTime(hour: Int, minute: Int): String {
        val amPm = if (hour >= 12) "PM" else "AM"
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        return String.format("%02d:%02d %s", displayHour, minute, amPm)
    }

    fun getDayName(dayIndex: Int): String {
        return when (dayIndex) {
            Calendar.SUNDAY -> "Sun"
            Calendar.MONDAY -> "Mon"
            Calendar.TUESDAY -> "Tue"
            Calendar.WEDNESDAY -> "Wed"
            Calendar.THURSDAY -> "Thu"
            Calendar.FRIDAY -> "Fri"
            Calendar.SATURDAY -> "Sat"
            else -> ""
        }
    }

    fun calculateMsToNextAlarm(hour: Int, minute: Int, repeatDays: List<Int>): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (repeatDays.isEmpty()) {
            if (target.before(now)) {
                target.add(Calendar.DAY_OF_YEAR, 1)
            }
            return target.timeInMillis - now.timeInMillis
        }

        var minDiffMs = Long.MAX_VALUE
        val currentDayOfWeek = now.get(Calendar.DAY_OF_WEEK)

        for (day in repeatDays) {
            val potential = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            var daysToAdd = day - currentDayOfWeek
            if (daysToAdd < 0 || (daysToAdd == 0 && potential.before(now))) {
                daysToAdd += 7
            }
            potential.add(Calendar.DAY_OF_YEAR, daysToAdd)
            val diff = potential.timeInMillis - now.timeInMillis
            if (diff in 0 until minDiffMs) {
                minDiffMs = diff
            }
        }
        return if (minDiffMs == Long.MAX_VALUE) 0 else minDiffMs
    }

    fun formatDurationUntil(msDiff: Long): String {
        if (msDiff <= 0) return "Right now"
        val seconds = msDiff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        val remHours = hours % 24
        val remMinutes = minutes % 60

        return buildString {
            if (days > 0) append("$days d ")
            if (hours > 0 || days > 0) append("$remHours h ")
            append("$remMinutes m")
        }
    }
}
