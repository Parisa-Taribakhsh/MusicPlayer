package com.example.musicplayer

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun MusicPlayerUI() {
    val context = LocalContext.current // Access LocalContext.current directly inside a Composable
    Column {
        Button(onClick = {
            val playIntent = Intent(context, MusicService::class.java).apply {
                action = "PLAY"
            }
            context.startService(playIntent)
        }) {
            Text("Play")
        }
        Button(onClick = {
            val pauseIntent = Intent(context, MusicService::class.java).apply {
                action = "PAUSE"
            }
            context.startService(pauseIntent)
        }) {
            Text("Pause")
        }
    }
}
