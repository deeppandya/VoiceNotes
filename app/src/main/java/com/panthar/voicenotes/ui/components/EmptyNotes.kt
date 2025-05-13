package com.panthar.voicenotes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.panthar.voicenotes.R
import com.panthar.voicenotes.ui.screens.viewmodel.SettingViewModel
import com.panthar.voicenotes.ui.theme.IndigoVariant
import com.panthar.voicenotes.ui.theme.isDarkTheme

@Composable
fun EmptyNotes(
    onNewNoteClick:() -> Unit, settingViewModel: SettingViewModel
) {
    val context = LocalContext.current

    val themeMode by settingViewModel.themeMode.collectAsState()

    val stroke = Stroke(width = 2f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    )
    Box(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(32.dp)
            .drawBehind {
                drawRoundRect(color = Color.Gray, style = stroke, cornerRadius = CornerRadius(10f))
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal =  16.dp, vertical = 32.dp)
        ) {
            Icon(Icons.Default.Notifications, contentDescription = null)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = context.getString(R.string.no_notes), color = if (isDarkTheme(themeMode)) Color.White else Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = context.getString(R.string.no_notes_text), color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(4.dp)
                    )
                    .background(IndigoVariant)
                    .padding(8.dp)
                    .clickable(onClick = onNewNoteClick)
            ) {
                Icon(
                    Icons.Default.Add,
                    tint = Color.White,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = context.getString(R.string.new_note),
                    color = Color.White
                )
            }
        }
    }
}