package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FocusMode(
    viewModel: MainViewModel,
    accentColor: Color,
    bgColor: Color
) {
    val currentTheme by viewModel.currentTheme.collectAsState()
    val remainingSeconds by viewModel.focusTimeRemaining.collectAsState()
    val totalSeconds by viewModel.focusTotalTime.collectAsState()
    val isRunning by viewModel.isFocusRunning.collectAsState()
    val completedCount by viewModel.focusCompletedCount.collectAsState()

    val progressDegrees = remember(remainingSeconds, totalSeconds) {
        if (totalSeconds > 0) {
            (remainingSeconds.toFloat() / totalSeconds.toFloat()) * 360f
        } else {
            360f
        }
    }

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text(
                    text = "FOCUS CHRONOMETER",
                    color = accentColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Shield distractions and focus on your goals offline",
                    color = Color.White.copy(0.5f),
                    fontSize = 11.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(RoundedCornerShape(120.dp))
                    .background(Color.White.copy(alpha = 0.02f))
                    .testTag("focus_circular_container"),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                    drawCircle(
                        color = Color.White.copy(0.06f),
                        style = Stroke(width = 8.dp.toPx())
                    )

                    drawArc(
                        color = accentColor,
                        startAngle = -90f,
                        sweepAngle = progressDegrees,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formattedTime,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completed Sessions",
                            tint = accentColor,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Sessions: $completedCount",
                            fontSize = 11.sp,
                            color = Color.White.copy(0.7f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val durations = listOf(15, 25, 45)
                durations.forEach { mins ->
                    val isCurrent = (totalSeconds / 60) == mins
                    Button(
                        onClick = { viewModel.startFocusSession(mins) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCurrent) accentColor else Color.White.copy(0.07f)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("duration_select_${mins}")
                    ) {
                        Text(
                            text = "${mins}M",
                            color = if (isCurrent) Color.Black else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (isRunning) {
                    Button(
                        onClick = { viewModel.pauseFocusSession() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.4f)
                            .height(50.dp)
                            .testTag("pause_focus_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Pause Focus Session",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pause", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                } else {
                    Button(
                        onClick = { viewModel.resumeFocusSession() },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.4f)
                            .height(50.dp)
                            .testTag("resume_focus_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Resume Focus Session",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Start / Resume", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                Button(
                    onClick = { viewModel.resetFocusSession() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.12f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("reset_focus_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Timer",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (currentTheme.name == "Sleek Interface") Color(0xFF1C1F22) else Color.White.copy(0.04f)
                ),
                shape = RoundedCornerShape(24.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                        listOf(Color.White.copy(0.08f), Color.White.copy(0.02f))
                    )
                )
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "FOCUS PROTOCOL",
                        color = accentColor,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Keep your smart watch active in alignment with work-cycles. Sound notifications and custom vibrations are managed in complete offline isolation.",
                        color = Color.White.copy(0.6f),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
