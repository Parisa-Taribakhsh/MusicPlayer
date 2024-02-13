package com.example.musicplayer

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.Slider
import androidx.compose.runtime.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


@Composable
fun MusicPlayerUI(musicViewModel: MusicViewModel) {
    val context = LocalContext.current
    val sliderPosition = musicViewModel.sliderPosition.collectAsState()
    val _isPlaying = MutableStateFlow(false) // Backing property which is mutable
    var isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow() // Publicly exposed as read-only
    var played by remember {
        mutableStateOf(false)
    }

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
        played = _isPlaying.value
    }

    // Dummy data for illustration
    val songTitle = "Song Title"
    val artistName = "Artist Name"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        // Album Art
        Image(
            painter = painterResource(id = R.drawable.baseline_smart_display_24),
            contentDescription = "Album Art",
            modifier = Modifier
                .size(200.dp)
                .fillMaxSize()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Track Info
        Text(text = songTitle, style = MaterialTheme.typography.headlineMedium)
        Text(text = artistName, style = MaterialTheme.typography.titleSmall)

        Spacer(modifier = Modifier.height(24.dp))

        // Progress Bar
        Slider(value = sliderPosition.value,
            onValueChange = { newPosition ->
                musicViewModel.seekTo(newPosition)
            },
            onValueChangeFinished = {
                musicViewModel.updatePlaybackPosition()
            })

        Spacer(modifier = Modifier.height(24.dp))

        // Playback Controls
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { /* TODO: Implement previous track action */ }) {
                Icon(
                    Icons.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous",
                    tint = Color.Gray
                )
            }
            Button(onClick = {
                togglePlayPause()
                val playIntent = Intent(context, MusicService::class.java).apply {
                    action = if (isPlaying.value) "PLAY" else "PAUSE"
                }
                context.startService(playIntent)
            }) {
                Icon(
                    if (played) Icons.Filled.Delete else Icons.Filled.PlayArrow,
                    contentDescription = if (played) "Pause" else "Play"
                )
            }
            IconButton(onClick = { /* TODO: Implement next track action */ }) {
                Icon(
                    Icons.Filled.KeyboardArrowRight,
                    contentDescription = "Next",
                    tint = Color.Gray
                )
            }
        }
    }
}