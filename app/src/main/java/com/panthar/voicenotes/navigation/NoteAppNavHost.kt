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
import com.panthar.voicenotes.ui.screens.viewmodel.NoteViewModel
import com.panthar.voicenotes.ui.screens.viewmodel.SettingViewModel

@Composable
fun NoteAppNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Home.route,
    modifier: Modifier,
    noteViewModel: NoteViewModel,
    settingViewModel: SettingViewModel
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(navController, noteViewModel, settingViewModel)
        }
        composable(
            route = Screen.Home.route + "/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId")
            HomeScreen(
                navController = navController,
                noteId = noteId,
                noteViewModel = noteViewModel,
                settingViewModel = settingViewModel
            )
        }
        composable(route = Screen.Notes.route) {
            NotesScreen(
                navController = navController,
                noteViewModel = noteViewModel,
                settingViewModel = settingViewModel
            )
        }
        composable(route = Screen.Settings.route) {
            SettingsScreen(settingViewModel = settingViewModel, noteViewModel = noteViewModel)
        }
        composable(
            route = Screen.NoteDetail.route + "/{noteId}/{isEdit}",
            arguments = listOf(
                navArgument("noteId") { type = NavType.IntType },
                navArgument("isEdit") { type = NavType.BoolType },
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId")
            val isEdit = backStackEntry.arguments?.getBoolean("isEdit") == true
            NoteDetailScreen(
                noteId = noteId,
                navController = navController,
                noteViewModel = noteViewModel,
                settingViewModel = settingViewModel,
                isEdit = isEdit
            )
        }
    }
}