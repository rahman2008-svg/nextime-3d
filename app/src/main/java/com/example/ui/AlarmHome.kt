package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AlarmModel
import com.example.utils.TimeUtils
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmHome(
    viewModel: MainViewModel,
    accentColor: Color,
    bgColor: Color
) {
    val currentTheme by viewModel.currentTheme.collectAsState()
    val alarms by viewModel.alarms.collectAsState()
    val sleepRecords by viewModel.sleepRecords.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showSleepDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor),
        containerColor = Color.Transparent,
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                SmallFloatingActionButton(
                    onClick = { showSleepDialog = true },
                    containerColor = Color.White.copy(0.12f),
                    contentColor = accentColor,
                    shape = CircleShape,
                    modifier = Modifier.testTag("log_sleep_fab")
                ) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = "Log Sleep")
                }

                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = accentColor,
                    contentColor = Color.Black,
                    shape = CircleShape,
                    modifier = Modifier.testTag("add_alarm_fab")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Alarm")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
        ) {
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Text(
                        text = "ALARM HEADQUARTERS",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Configure sleep blocks, prayer sync or study blocks securely",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            }

            if (sleepRecords.isNotEmpty()) {
                item {
                    val averageScore = sleepRecords.map { it.sleepScore }.average().toInt()
                    val totalLogs = sleepRecords.size
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentTheme.name == "Sleek Interface") Color(0xFF1C1F22) else Color.White.copy(alpha = 0.04f)
                        ),
                        shape = RoundedCornerShape(24.dp),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                listOf(Color.White.copy(0.08f), Color.White.copy(0.02f))
                            )
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "OFFLINE SLEEP QUALITY STATUS",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = accentColor,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Sleep Score: $averageScore/100",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Computed over your last $totalLogs logs",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(0.4f)
                                )
                            }
                            CircularProgressIndicator(
                                progress = { averageScore / 100f },
                                color = accentColor,
                                strokeWidth = 4.dp,
                                modifier = Modifier.size(46.dp),
                                trackColor = Color.White.copy(0.12f)
                            )
                        }
                    }
                }
            }

            if (alarms.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Empty Alarms",
                                tint = Color.White.copy(0.2f),
                                modifier = Modifier.size(52.dp)
                            )
                            Text(
                                text = "All Clear! No alarms programmed",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "Program study blocks, prayer alerts or sunrise rise schedules offline.",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.35f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.width(260.dp)
                            )
                        }
                    }
                }
            } else {
                items(alarms, key = { it.id }) { alarm ->
                    AlarmItemCard(
                        alarm = alarm,
                        onToggle = { viewModel.toggleAlarm(alarm) },
                        onDelete = { viewModel.deleteAlarm(alarm) },
                        accentColor = accentColor,
                        themeName = currentTheme.name
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        var alarmTitle by remember { mutableStateOf("") }
        var hourStr by remember { mutableStateOf("07") }
        var minuteStr by remember { mutableStateOf("00") }
        var selectedCategory by remember { mutableStateOf("Morning") }
        val categories = listOf("Morning", "Study", "Work", "Prayer", "Custom")
        var selectedDays by remember { mutableStateOf(setOf<Int>()) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = Color(0xFF14151C),
            title = {
                Text("New Exact Alarm", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = alarmTitle,
                        onValueChange = { alarmTitle = it },
                        label = { Text("Alarm Label", color = Color.White.copy(0.5f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = Color.White.copy(0.15f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("alarm_title_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = hourStr,
                            onValueChange = { if (it.length <= 2 && (it.toIntOrNull() in 0..23 || it.isEmpty())) hourStr = it },
                            label = { Text("Hour (0-23)", fontSize = 10.sp, color = Color.White.copy(0.5f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = accentColor,
                                unfocusedBorderColor = Color.White.copy(0.15f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("alarm_hour_input")
                        )
                        Text(":", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = minuteStr,
                            onValueChange = { if (it.length <= 2 && (it.toIntOrNull() in 0..59 || it.isEmpty())) minuteStr = it },
                            label = { Text("Minute (0-59)", fontSize = 10.sp, color = Color.White.copy(0.5f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = accentColor,
                                unfocusedBorderColor = Color.White.copy(0.15f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("alarm_minute_input")
                        )
                    }

                    Text("Alarm Category", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSelected = selectedCategory == cat
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) accentColor else Color.White.copy(0.06f))
                                    .clickable { selectedCategory = cat }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else Color.White
                                )
                            }
                        }
                    }

                    Text("Repeat Days", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val weekDays = listOf("S" to 1, "M" to 2, "T" to 3, "W" to 4, "T" to 5, "F" to 6, "S" to 7)
                        weekDays.forEach { (label, value) ->
                            val isRepeat = selectedDays.contains(value)
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (isRepeat) accentColor else Color.White.copy(0.06f))
                                    .border(1.dp, if (isRepeat) accentColor else Color.Transparent, CircleShape)
                                    .clickable {
                                        selectedDays = if (isRepeat) selectedDays - value else selectedDays + value
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isRepeat) Color.Black else Color.White
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val h = hourStr.toIntOrNull() ?: 0
                        val m = minuteStr.toIntOrNull() ?: 0
                        viewModel.addAlarm(
                            title = alarmTitle,
                            hour = h,
                            minute = m,
                            category = selectedCategory,
                            repeatDays = selectedDays.toList()
                        )
                        showAddDialog = false
                    },
                    modifier = Modifier.testTag("save_dialog_alarm_button")
                ) {
                    Text("SAVE TO CHRONOS", color = accentColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("CANCEL", color = Color.White.copy(0.5f))
                }
            }
        )
    }

    if (showSleepDialog) {
        var sleepDurationHours by remember { mutableStateOf("7.5") }
        var disruptions by remember { mutableStateOf("0") }

        AlertDialog(
            onDismissRequest = { showSleepDialog = false },
            containerColor = Color(0xFF14151C),
            title = {
                Text("Log Sleep Session", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Report sleep measurements to construct circadian profiles offline.",
                        color = Color.White.copy(0.5f),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    OutlinedTextField(
                        value = sleepDurationHours,
                        onValueChange = { sleepDurationHours = it },
                        label = { Text("Duration (Hours)", color = Color.White.copy(0.5f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = Color.White.copy(0.15f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sleep_duration_field")
                    )

                    OutlinedTextField(
                        value = disruptions,
                        onValueChange = { disruptions = it },
                        label = { Text("Midnight Disruptions Count", color = Color.White.copy(0.5f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = Color.White.copy(0.15f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sleep_disruptions_field")
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val dur = sleepDurationHours.toDoubleOrNull() ?: 7.5
                        val dis = disruptions.toIntOrNull() ?: 0
                        viewModel.logSleepSession(dur, dis)
                        showSleepDialog = false
                    },
                    modifier = Modifier.testTag("save_sleep_log_button")
                ) {
                    Text("WRITE RECORD", color = accentColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSleepDialog = false }) {
                    Text("CLOSE", color = Color.White.copy(0.5f))
                }
            }
        )
    }
}

@Composable
fun AlarmItemCard(
    alarm: AlarmModel,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    accentColor: Color,
    themeName: String = ""
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("alarm_item_${alarm.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (themeName == "Sleek Interface") Color(0xFF1C1F22) else Color.White.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(24.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                listOf(Color.White.copy(0.08f), Color.White.copy(0.02f))
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(accentColor.copy(0.10f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = alarm.category.uppercase(),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Text(
                        text = alarm.title,
                        fontSize = 12.sp,
                        color = Color.White.copy(0.7f),
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = TimeUtils.formatTime(alarm.hour, alarm.minute),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (alarm.isEnabled) Color.White else Color.White.copy(alpha = 0.4f)
                )

                val repeatDaysList = alarm.getRepeatDaysList()
                val repeatLabel = if (repeatDaysList.isEmpty()) {
                    "Single Alarm Schedule"
                } else {
                    repeatDaysList.joinToString(" ") { TimeUtils.getDayName(it) }
                }

                Text(
                    text = repeatLabel,
                    fontSize = 10.sp,
                    color = Color.White.copy(0.4f),
                    fontFamily = FontFamily.Monospace
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Switch(
                    checked = alarm.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = accentColor,
                        uncheckedThumbColor = Color.White.copy(alpha = 0.5f),
                        uncheckedTrackColor = Color.White.copy(alpha = 0.08f)
                    ),
                    modifier = Modifier.testTag("alarm_switch_${alarm.id}")
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("alarm_delete_btn_${alarm.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove alarm Record",
                        tint = Color.White.copy(alpha = 0.35f)
                    )
                }
            }
        }
    }
}
