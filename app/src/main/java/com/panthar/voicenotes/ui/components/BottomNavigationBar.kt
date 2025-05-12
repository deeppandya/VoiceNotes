package com.panthar.voicenotes.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.panthar.voicenotes.R
import com.panthar.voicenotes.navigation.Screen
import com.panthar.voicenotes.util.navigateTo


@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current

    BottomAppBar(
        containerColor = Color.White,
        contentColor = Color.Black,
        modifier = Modifier.wrapContentHeight()
    ) {
        BottomNavItem(
            label = context.getString(R.string.home),
            icon = Icons.Default.Home,
            isSelected = currentRoute == Screen.Home.route,
            onClick = {
                navigateTo(navController, Screen.Home.route)
            }
        )
        BottomNavItem(
            label = context.getString(R.string.notes),
            icon = Icons.AutoMirrored.Filled.List,
            isSelected = currentRoute == Screen.Notes.route,
            onClick = {
                navigateTo(navController, Screen.Notes.route)
            }
        )
        BottomNavItem(
            label = context.getString(R.string.settings),
            icon = Icons.Default.Settings,
            isSelected = currentRoute == Screen.Settings.route,
            onClick = {
                navigateTo(navController, Screen.Settings.route)
            }
        )
    }
}

@Composable
fun RowScope.BottomNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = if (isSelected) Color.Blue else Color.Black

    Box(
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = label, tint = color)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, color = color, style = MaterialTheme.typography.labelSmall)
        }
    }
}