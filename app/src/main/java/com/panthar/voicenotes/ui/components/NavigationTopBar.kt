package com.panthar.voicenotes.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.panthar.voicenotes.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationTopBar(onBackPressed:() -> Unit, onAccountPressed:() -> Unit, shouldShowAccount: Boolean = true) {
    val context = LocalContext.current
    CenterAlignedTopAppBar(
        title = {
            Text(context.getString(R.string.app_name))
        },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        },
        actions = {
            if (shouldShowAccount) {
                IconButton(onClick = onAccountPressed) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "Localized description",
                    )
                }
            }
        },
    )
}