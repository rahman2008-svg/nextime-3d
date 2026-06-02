package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.AlarmEngine
import com.example.core.SleepAnalyzer
import com.example.core.SmartAIEngine
import com.example.data.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val repository = AppRepository(db.appDao())
    private val alarmEngine = AlarmEngine(application)

    val alarms: StateFlow<List<AlarmModel>> = repository.allAlarms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val themes: StateFlow<List<ThemeModel>> = repository.allThemes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sleepRecords: StateFlow<List<SleepRecord>> = repository.recentSleepRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentTheme = MutableStateFlow(
        ThemeModel(
            id = 1,
            name = "Sleek Interface",
            backgroundColor = "#0F1113",
            accentColor = "#FFB74D",
            watchStyle = "Sleek Gold"
        )
    )
    val currentTheme: StateFlow<ThemeModel> = _currentTheme.asStateFlow()

    private val _focusTimeRemaining = MutableStateFlow(25 * 60)
    val focusTimeRemaining = _focusTimeRemaining.asStateFlow()

    private val _focusTotalTime = MutableStateFlow(25 * 60)
    val focusTotalTime = _focusTotalTime.asStateFlow()

    private val _isFocusRunning = MutableStateFlow(false)
    val isFocusRunning = _isFocusRunning.asStateFlow()

    private val _focusCompletedCount = MutableStateFlow(0)
    val focusCompletedCount = _focusCompletedCount.asStateFlow()

    private var focusJob: Job? = null

    val suggestions: StateFlow<List<String>> = combine(alarms, sleepRecords) { alarmList, sleepList ->
        SmartAIEngine.generateSuggestions(alarmList, sleepList)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("Initializing NexVora AI suggestions..."))

    init {
        viewModelScope.launch {
            repository.allThemes.first().let { currentList ->
                val sleekTheme = ThemeModel(
                    name = "Sleek Interface",
                    backgroundColor = "#0F1113",
                    accentColor = "#FFB74D",
                    watchStyle = "Sleek Gold"
                )
                if (currentList.isEmpty()) {
                    val defaultThemes = listOf(
                        sleekTheme,
                        ThemeModel(name = "Cosmic Teal", backgroundColor = "#0B0C10", accentColor = "#00FFC2", watchStyle = "Nebula Pulse"),
                        ThemeModel(name = "AMOLED Matrix", backgroundColor = "#000000", accentColor = "#39FF14", watchStyle = "AMOLED Green"),
                        ThemeModel(name = "Cyberpunk Crimson", backgroundColor = "#150202", accentColor = "#FF3366", watchStyle = "Cyberpunk Glow"),
                        ThemeModel(name = "Slate Monochrome", backgroundColor = "#1C1C1E", accentColor = "#E5E5EA", watchStyle = "Minimal Gray")
                    )
                    for (theme in defaultThemes) {
                        repository.insertTheme(theme)
                    }
                    val updated = repository.allThemes.first()
                    _currentTheme.value = updated.firstOrNull { it.name == "Sleek Interface" } ?: sleekTheme
                } else {
                    val existingSleek = currentList.find { it.name == "Sleek Interface" }
                    if (existingSleek == null) {
                        repository.insertTheme(sleekTheme)
                        val updated = repository.allThemes.first()
                        _currentTheme.value = updated.firstOrNull { it.name == "Sleek Interface" } ?: sleekTheme
                    } else {
                        _currentTheme.value = existingSleek
                    }
                }
            }
        }
    }

    fun addAlarm(title: String, hour: Int, minute: Int, category: String, repeatDays: List<Int>) {
        viewModelScope.launch {
            val repeatStr = repeatDays.joinToString(",")
            val newAlarm = AlarmModel(
                title = title.ifBlank { "$category Alarm" },
                hour = hour,
                minute = minute,
                repeatDays = repeatStr,
                isEnabled = true,
                category = category
            )
            val id = repository.insertAlarm(newAlarm)
            val savedAlarm = newAlarm.copy(id = id.toInt())
            alarmEngine.scheduleAlarm(savedAlarm)
        }
    }

    fun toggleAlarm(alarm: AlarmModel) {
        viewModelScope.launch {
            val updated = alarm.copy(isEnabled = !alarm.isEnabled)
            repository.updateAlarm(updated)
            if (updated.isEnabled) {
                alarmEngine.scheduleAlarm(updated)
            } else {
                alarmEngine.cancelAlarm(updated.id)
            }
        }
    }

    fun deleteAlarm(alarm: AlarmModel) {
        viewModelScope.launch {
            alarmEngine.cancelAlarm(alarm.id)
            repository.deleteAlarm(alarm)
        }
    }

    fun selectTheme(theme: ThemeModel) {
        _currentTheme.value = theme
    }

    fun logSleepSession(durationHours: Double, disruptionCount: Int) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val sleepDurationMs = (durationHours * 60 * 60 * 1000).toLong()
            val sleepStart = now - sleepDurationMs

            val score = SleepAnalyzer.analyzeSleep(
                sleepTimeMillis = sleepStart,
                wakeTimeMillis = now,
                disruptionsCount = disruptionCount
            )

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateStr = sdf.format(Date())

            val record = SleepRecord(
                date = dateStr,
                sleepTimeMillis = sleepStart,
                wakeTimeMillis = now,
                sleepScore = score,
                disruptionCount = disruptionCount
            )

            repository.insertSleepRecord(record)
        }
    }

    fun startFocusSession(minutes: Int) {
        focusJob?.cancel()
        _focusTotalTime.value = minutes * 60
        _focusTimeRemaining.value = minutes * 60
        _isFocusRunning.value = true

        focusJob = viewModelScope.launch {
            while (_focusTimeRemaining.value > 0) {
                delay(1000)
                _focusTimeRemaining.value -= 1
            }
            _isFocusRunning.value = false
            _focusCompletedCount.value += 1
            val notificationHelper = com.example.utils.NotificationHelper(getApplication())
            notificationHelper.triggerFocusCompletedNotification(
                title = "🎯 Focus completed!",
                body = "Phenomenal focus! You have successfully finished your NexTime focus session."
            )
        }
    }

    fun pauseFocusSession() {
        _isFocusRunning.value = false
        focusJob?.cancel()
    }

    fun resumeFocusSession() {
        if (_focusTimeRemaining.value > 0) {
            _isFocusRunning.value = true
            focusJob = viewModelScope.launch {
                while (_focusTimeRemaining.value > 0) {
                    delay(1000)
                    _focusTimeRemaining.value -= 1
                }
                _isFocusRunning.value = false
                _focusCompletedCount.value += 1
                val notificationHelper = com.example.utils.NotificationHelper(getApplication())
                notificationHelper.triggerFocusCompletedNotification(
                    title = "🎯 Focus completed!",
                    body = "Fantastic determination! Your Pomodoro focus timer is finished."
                )
            }
        }
    }

    fun resetFocusSession() {
        _isFocusRunning.value = false
        focusJob?.cancel()
        _focusTimeRemaining.value = _focusTotalTime.value
    }

    override fun onCleared() {
        super.onCleared()
        focusJob?.cancel()
    }
}
