package com.panthar.voicenotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.panthar.voicenotes.composables.NavigationTopBar
import com.panthar.voicenotes.ui.theme.VoiceNotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VoiceNotesTheme {
                Scaffold(
                    topBar = {
                        NavigationTopBar(
                            onBackPressed = { finish() },
                            onAccountPressed = {})
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Homepage(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Homepage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(Color.Red))
            Spacer(modifier = Modifier.width(8.dp))
            Text(context.getString(R.string.recording_on))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(
                    RoundedCornerShape(12.dp)
                )
                .background(Color.White)
                .height(100.dp)
        ) {
            Text(text = "Large floating action button", modifier = Modifier.padding(8.dp))
        }
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            SmallFloatingActionButton(
                onClick = { },
                shape = CircleShape,
                modifier = Modifier.size(40.dp),
                containerColor = Color.LightGray,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.PlayArrow, "Large floating action button")
            }
            Spacer(modifier = Modifier.width(8.dp))
            FloatingActionButton(
                onClick = { },
                shape = CircleShape,
                modifier = Modifier.size(80.dp),
                containerColor = Color.White,
                contentColor = Color.LightGray
            ) {
                Icon(rememberAsyncImagePainter(R.drawable.ic_baseline_mic_24), "Large floating action button")
            }
            Spacer(modifier = Modifier.width(8.dp))
            SmallFloatingActionButton(
                onClick = { },
                shape = CircleShape,
                modifier = Modifier.size(40.dp),
                containerColor = Color.LightGray,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Delete, "Large floating action button")
            }
        }
    }
}