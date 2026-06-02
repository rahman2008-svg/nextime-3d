package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_records")
data class SleepRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,             // e.g. "2026-06-02"
    val sleepTimeMillis: Long,     // Start bedtime
    val wakeTimeMillis: Long,      // Stop wake up
    val sleepScore: Int,           // Sleep score computation (0-100)
    val disruptionCount: Int       // Simulated midnight disruptions
)
