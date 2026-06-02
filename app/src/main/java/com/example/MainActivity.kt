package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.ui.AlarmHome
import com.example.ui.FocusMode
import com.example.ui.MainViewModel
import com.example.ui.WatchScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainContent(viewModel)
            }
        }
    }
}

@Composable
fun MainContent(viewModel: MainViewModel) {
    val currentTheme by viewModel.currentTheme.collectAsState()
    val themesList by viewModel.themes.collectAsState()
    
    val accentColor = remember(currentTheme) {
        try {
            Color(android.graphics.Color.parseColor(currentTheme.accentColor))
        } catch (e: Exception) {
            Color(0xFF00FFC2)
        }
    }

    val backgroundColor = remember(currentTheme) {
        try {
            Color(android.graphics.Color.parseColor(currentTheme.backgroundColor))
        } catch (e: Exception) {
            Color(0xFF0B0C10)
        }
    }

    var selectedTab by remember { mutableStateOf("Watch") }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        containerColor = backgroundColor,
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
            ) {
                if (themesList.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item {
                            Text(
                                text = "THEMES:",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color.White.copy(0.4f),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
                        items(themesList, key = { it.id }) { theme ->
                            val isSelected = currentTheme.id == theme.id
                            val themeColor = try {
                                Color(android.graphics.Color.parseColor(theme.accentColor))
                            } catch (e: Exception) {
                                accentColor
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) themeColor.copy(0.2f) else Color.White.copy(0.04f))
                                    .clickable { viewModel.selectTheme(theme) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .testTag("theme_select_${theme.id}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(themeColor)
                                    )
                                    Text(
                                        text = theme.name,
                                        fontSize = 10.sp,
                                        color = if (isSelected) Color.White else Color.White.copy(0.6f),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                NavigationBar(
                    containerColor = backgroundColor.copy(alpha = 0.95f),
                    contentColor = accentColor,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .testTag("main_navigation_bar")
                ) {
                    NavigationBarItem(
                        selected = selectedTab == "Watch",
                        onClick = { selectedTab = "Watch" },
                        icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "WatchFace") },
                        label = { Text("WatchFace", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = accentColor,
                            indicatorColor = accentColor,
                            unselectedTextColor = Color.White.copy(0.5f),
                            unselectedIconColor = Color.White.copy(0.5f)
                        ),
                        modifier = Modifier.testTag("navigation_item_watch")
                    )

                    NavigationBarItem(
                        selected = selectedTab == "Alarm",
                        onClick = { selectedTab = "Alarm" },
                        icon = { Icon(imageVector = Icons.Default.Notifications, contentDescription = "Alarms") },
                        label = { Text("Alarms", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = accentColor,
                            indicatorColor = accentColor,
                            unselectedTextColor = Color.White.copy(0.5f),
                            unselectedIconColor = Color.White.copy(0.5f)
                        ),
                        modifier = Modifier.testTag("navigation_item_alarm")
                    )

                    NavigationBarItem(
                        selected = selectedTab == "Focus",
                        onClick = { selectedTab = "Focus" },
                        icon = { Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Focus") },
                        label = { Text("Focus OS", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = accentColor,
                            indicatorColor = accentColor,
                            unselectedTextColor = Color.White.copy(0.5f),
                            unselectedIconColor = Color.White.copy(0.5f)
                        ),
                        modifier = Modifier.testTag("navigation_item_focus")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .background(backgroundColor)
        ) {
            when (selectedTab) {
                "Watch" -> WatchScreen(
                    viewModel = viewModel,
                    onTabSelect = { selectedTab = it },
                    accentColor = accentColor,
                    bgColor = backgroundColor
                )
                "Alarm" -> AlarmHome(
                    viewModel = viewModel,
                    accentColor = accentColor,
                    bgColor = backgroundColor
                )
                "Focus" -> FocusMode(
                    viewModel = viewModel,
                    accentColor = accentColor,
                    bgColor = backgroundColor
                )
            }
        }
    }
}
