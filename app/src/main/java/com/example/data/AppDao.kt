package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Alarms
    @Query("SELECT * FROM alarms ORDER BY hour ASC, minute ASC")
    fun getAllAlarms(): Flow<List<AlarmModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmModel): Long

    @Update
    suspend fun updateAlarm(alarm: AlarmModel)

    @Delete
    suspend fun deleteAlarm(alarm: AlarmModel)

    @Query("SELECT * FROM alarms WHERE id = :id LIMIT 1")
    suspend fun getAlarmById(id: Int): AlarmModel?

    // Themes
    @Query("SELECT * FROM themes")
    fun getAllThemes(): Flow<List<ThemeModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTheme(theme: ThemeModel)

    @Query("SELECT * FROM themes WHERE id = :id LIMIT 1")
    suspend fun getThemeById(id: Int): ThemeModel?

    // Sleep Records
    @Query("SELECT * FROM sleep_records ORDER BY date DESC LIMIT 30")
    fun getRecentSleepRecords(): Flow<List<SleepRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleepRecord(record: SleepRecord)
}
