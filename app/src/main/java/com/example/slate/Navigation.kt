package com.example.slate

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.slate.ui.screens.DashboardScreen
import com.example.slate.ui.screens.NoteEditorScreen
import com.example.slate.ui.screens.MindMapScreen
import com.example.slate.ui.screens.VoiceRecorderScreen
import com.example.slate.ui.screens.AnalyticsScreen
import com.example.slate.theme.SlateBackground
import com.example.slate.theme.SlatePrimary
import com.example.slate.theme.SlateSurface

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main_tabs") {
        composable("main_tabs") {
            MainTabScreen(
                onNoteClick = { noteId ->
                    navController.navigate("editor/$noteId")
                }
            )
        }
        composable(
            route = "editor/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.StringType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: "new"
            NoteEditorScreen(
                noteId = noteId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun MainTabScreen(onNoteClick: (String) -> Unit) {
    val tabNavController = rememberNavController()
    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "dashboard"

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = SlateSurface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = currentRoute == "dashboard",
                    onClick = {
                        if (currentRoute != "dashboard") {
                            tabNavController.navigate("dashboard") {
                                popUpTo("dashboard") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SlatePrimary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Share, contentDescription = "Mind Map") },
                    label = { Text("Mind Map") },
                    selected = currentRoute == "mindmap",
                    onClick = {
                        if (currentRoute != "mindmap") {
                            tabNavController.navigate("mindmap") {
                                popUpTo("dashboard") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SlatePrimary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Mic, contentDescription = "Voice") },
                    label = { Text("Voice") },
                    selected = currentRoute == "voice",
                    onClick = {
                        if (currentRoute != "voice") {
                            tabNavController.navigate("voice") {
                                popUpTo("dashboard") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SlatePrimary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.BarChart, contentDescription = "Analytics") },
                    label = { Text("Stats") },
                    selected = currentRoute == "analytics",
                    onClick = {
                        if (currentRoute != "analytics") {
                            tabNavController.navigate("analytics") {
                                popUpTo("dashboard") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SlatePrimary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = tabNavController,
            startDestination = "dashboard",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("dashboard") {
                DashboardScreen(onNoteClick = onNoteClick)
            }
            composable("mindmap") {
                MindMapScreen(onNoteClick = onNoteClick)
            }
            composable("voice") {
                VoiceRecorderScreen(onNoteClick = onNoteClick)
            }
            composable("analytics") {
                AnalyticsScreen()
            }
        }
    }
}

