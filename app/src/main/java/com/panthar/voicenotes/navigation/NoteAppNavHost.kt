package com.panthar.voicenotes.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.panthar.voicenotes.ui.screens.HomeScreen
import com.panthar.voicenotes.ui.screens.NoteDetailScreen
import com.panthar.voicenotes.ui.screens.NotesScreen
import com.panthar.voicenotes.ui.screens.SettingsScreen

@Composable
fun NoteAppNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Home.route,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(route = Screen.Notes.route) {
            NotesScreen(navController = navController)
        }
        composable(route = Screen.Settings.route) {
            SettingsScreen(navController)
        }
        composable(route = Screen.NoteDetail.route + "/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()
            NoteDetailScreen(noteId = noteId, navController = navController)
        }
    }
}