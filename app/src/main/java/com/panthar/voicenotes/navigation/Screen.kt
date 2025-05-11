package com.panthar.voicenotes.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Notes : Screen("notes")
    object Settings : Screen("settings")
    object NoteDetail : Screen("note_detail")
}