package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.WatchRenderer
import com.example.utils.TimeUtils
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WatchScreen(
    viewModel: MainViewModel,
    onTabSelect: (String) -> Unit,
    accentColor: Color,
    bgColor: Color
) {
    val currentTheme by viewModel.currentTheme.collectAsState()
    var adjustHour by remember { mutableStateOf<Int?>(null) }
    var adjustMinute by remember { mutableStateOf<Int?>(null) }
    val suggestions by viewModel.suggestions.collectAsState()
    val alarms by viewModel.alarms.collectAsState()

    var tickCount by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            tickCount++
        }
    }

    val liveDateStr = remember(tickCount) {
        val sdf = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
        sdf.format(Date())
    }

    val liveTimeStr = remember(tickCount) {
        val sdf = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        sdf.format(Date())
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor),
        containerColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text(
                    text = "NEXTIME OS v1.0",
                    color = accentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )
                Text(
                    text = liveDateStr,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = liveTimeStr,
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(290.dp)
                    .clip(RoundedCornerShape(145.dp))
                    .background(Color.Black.copy(alpha = 0.3f))
                    .testTag("watch_renderer_container"),
                contentAlignment = Alignment.Center
            ) {
                WatchRenderer(
                    modifier = Modifier.fillMaxSize(),
                    accentColor = accentColor,
                    backgroundColor = bgColor,
                    watchStyle = currentTheme.watchStyle,
                    interactiveTimeAdjust = { hr, min ->
                        adjustHour = hr
                        adjustMinute = min
                    }
                )
            }

            AnimatedVisibility(
                visible = adjustHour != null,
                enter = fadeIn() + expandVertically(),
                exit = shrinkVertically() + fadeOut()
            ) {
                if (adjustHour != null && adjustMinute != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "MANUAL WATCH CALIBRATION",
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = accentColor,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = TimeUtils.formatTime(adjustHour!!, adjustMinute!!),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Button(
                                onClick = {
                                    viewModel.addAlarm(
                                        title = "Quick Watch Alarm",
                                        hour = adjustHour!!,
                                        minute = adjustMinute!!,
                                        category = "Custom",
                                        repeatDays = emptyList()
                                    )
                                    adjustHour = null
                                    adjustMinute = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .testTag("save_quick_alarm_button")
                                    .height(38.dp)
                            ) {
                                Text("SAVE", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(104.dp)
                        .clickable { onTabSelect("Alarm") }
                        .testTag("quick_alarm_action_button"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = if (currentTheme.name == "Sleek Interface") Color(0xFF23262B) else Color.White.copy(0.06f)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(listOf(Color.White.copy(0.08f), Color.White.copy(0.02f)))
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color(0xFFD1E4FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("⏰", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Set Alarm",
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(104.dp)
                        .clickable { onTabSelect("Focus") }
                        .testTag("quick_focus_action_button"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = if (currentTheme.name == "Sleek Interface") Color(0xFF23262B) else Color.White.copy(0.06f)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(listOf(Color.White.copy(0.08f), Color.White.copy(0.02f)))
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color(0xFFFFDDB3)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎯", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Focus Mode",
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = if (currentTheme.name == "Sleek Interface") Color(0xFF1C1F22) else Color.White.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(24.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(listOf(Color.White.copy(0.08f), Color.White.copy(0.02f)))
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(accentColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⚡",
                            fontSize = 18.sp,
                            color = accentColor
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "SLEEP INSIGHT / AI CHRONO",
                            color = accentColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )

                        val tip = remember(suggestions) {
                            try {
                                suggestions.getOrNull(tickCount % suggestions.size.coerceAtLeast(1)) ?: "Processing chronotypes..."
                            } catch (e: Exception) {
                                "Syncing biometric state..."
                            }
                        }

                        Crossfade(targetState = tip) { text ->
                            Text(
                                text = text,
                                color = Color.White.copy(0.75f),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "NexVora EcoSystem flagship app",
                    color = Color.White.copy(alpha = 0.3f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Developed by Prince AR Abdur Rahman",
                    color = Color.White.copy(alpha = 0.25f),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
