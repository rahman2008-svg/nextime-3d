package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "themes")
data class ThemeModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val backgroundColor: String, // Hex code (e.g. "#12131A")
    val accentColor: String,     // Hex code (e.g. "#00FFC2")
    val watchStyle: String       // Style selection: "Nebula Pulse", "AMOLED Green", "Cyberpunk Glow", "Minimal Gray"
)
