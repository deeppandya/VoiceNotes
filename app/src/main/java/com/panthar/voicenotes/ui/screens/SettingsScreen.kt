package com.panthar.voicenotes.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.panthar.voicenotes.R
import com.panthar.voicenotes.ui.components.SwitchWithLabel
import com.panthar.voicenotes.ui.screens.viewmodel.NoteViewModel
import com.panthar.voicenotes.ui.theme.ThemeMode
import com.panthar.voicenotes.ui.screens.viewmodel.SettingViewModel

@Composable
fun SettingsScreen(settingViewModel: SettingViewModel, noteViewModel: NoteViewModel) {
    val context = LocalContext.current
    val themeMode by settingViewModel.themeMode.collectAsState()
    noteViewModel.setTitle(context.getString(R.string.settings))

    val speechBubble by settingViewModel.speechBubble.collectAsState()

    Column(
        Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        SwitchWithLabel(label = context.getString(R.string.speech_bubble), state = speechBubble, onStateChange = {
            if (!Settings.canDrawOverlays(context)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    "package:${context.packageName}".toUri()
                )
                context.startActivity(intent)
            } else {
                settingViewModel.setSpeechBubble(it)
            }
        })

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            context.getString(R.string.choose_theme), style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        ThemeMode.entries.forEach { mode ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { settingViewModel.setThemeMode(mode) }) {
                RadioButton(
                    selected = (mode == themeMode), onClick = { settingViewModel.setThemeMode(mode) })
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (mode) {
                        ThemeMode.SYSTEM -> context.getString(R.string.system_default)
                        ThemeMode.LIGHT -> context.getString(R.string.light)
                        ThemeMode.DARK -> context.getString(R.string.dark)
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}