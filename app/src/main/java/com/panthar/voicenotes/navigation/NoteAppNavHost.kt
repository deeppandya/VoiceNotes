package com.panthar.voicenotes.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.panthar.voicenotes.ui.screens.HomeScreen
import com.panthar.voicenotes.ui.screens.NoteDetailScreen
import com.panthar.voicenotes.ui.screens.NotesScreen
import com.panthar.voicenotes.ui.screens.SettingsScreen
import com.panthar.voicenotes.ui.theme.ThemeViewModel

@Composable
fun NoteAppNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Home.route,
    modifier: Modifier,
    themeViewModel: ThemeViewModel
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(
            route = Screen.Home.route + "/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId")
            HomeScreen(navController = navController, noteId = noteId)
        }
        composable(route = Screen.Notes.route) {
            NotesScreen(navController = navController)
        }
        composable(route = Screen.Settings.route) {
            SettingsScreen(themeViewModel = themeViewModel)
        }
        composable(
            route = Screen.NoteDetail.route + "/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId")
            NoteDetailScreen(noteId = noteId, navController = navController)
        }
    }
}