package com.panthar.voicenotes.util

import androidx.navigation.NavController

fun navigateTo(navController: NavController, route : String) {
    navController.navigate(route) {
        popUpTo(navController.graph.navigatorName) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}