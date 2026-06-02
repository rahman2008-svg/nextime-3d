package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class AlarmModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val hour: Int,
    val minute: Int,
    val repeatDays: String, // Comma-separated day indices (e.g. "1,2,3" for Sun, Mon, Tue)
    val isEnabled: Boolean,
    val category: String
) {
    fun getRepeatDaysList(): List<Int> {
        if (repeatDays.isBlank()) return emptyList()
        return repeatDays.split(",").mapNotNull { it.trim().toIntOrNull() }
    }
}
