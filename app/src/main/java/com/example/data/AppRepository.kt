package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {
    val allAlarms: Flow<List<AlarmModel>> = appDao.getAllAlarms()
    val allThemes: Flow<List<ThemeModel>> = appDao.getAllThemes()
    val recentSleepRecords: Flow<List<SleepRecord>> = appDao.getRecentSleepRecords()

    suspend fun insertAlarm(alarm: AlarmModel): Long = appDao.insertAlarm(alarm)
    suspend fun updateAlarm(alarm: AlarmModel) = appDao.updateAlarm(alarm)
    suspend fun deleteAlarm(alarm: AlarmModel) = appDao.deleteAlarm(alarm)
    suspend fun getAlarmById(id: Int): AlarmModel? = appDao.getAlarmById(id)

    suspend fun insertTheme(theme: ThemeModel) = appDao.insertTheme(theme)
    suspend fun getThemeById(id: Int): ThemeModel? = appDao.getThemeById(id)

    suspend fun insertSleepRecord(record: SleepRecord) = appDao.insertSleepRecord(record)
}
