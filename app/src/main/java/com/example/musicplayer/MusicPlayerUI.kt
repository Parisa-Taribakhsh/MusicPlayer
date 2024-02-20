package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Composable
fun MusicPlayerUI(musicViewModel: MusicViewModel) {
    val context = LocalContext.current
    val _isPlaying = remember { MutableStateFlow(false) } // Use remember for state initialization
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    val sliderPosition by musicViewModel.sliderPosition.collectAsState()
    val duration by musicViewModel.duration.collectAsState()
    val currentPosition by musicViewModel.currentPosition.collectAsState()
    val formattedDuration = convertMillisecondsToMinutesSeconds(duration.toLong())
    val formattedPosition = convertMillisecondsToMinutesSeconds(currentPosition.toLong())
    var userIsSeeking by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        AlbumArt()
        Spacer(modifier = Modifier.height(24.dp))
        TrackInfo("Song Title", "Artist Name")
        Spacer(modifier = Modifier.height(24.dp))
        Slider(
            value = sliderPosition,
            onValueChange = { newPosition ->
                userIsSeeking = true
                musicViewModel.seekTo(newPosition / duration)

            },
            valueRange = 0f..duration, // Set the slider's range to the audio duration
            onValueChangeFinished = { userIsSeeking = false }
        )
        Text(
            text = "$formattedPosition/$formattedDuration",
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(24.dp))
        PlaybackControls(isPlaying, _isPlaying) {
            if (isPlaying.value) {
                musicViewModel.playMusic()
            } else musicViewModel.pauseMusic()
            //val action = if (isPlaying.value) "PLAY" else "PAUSE"
            context.startService(Intent(context, MusicService::class.java).apply {
                this.action = action
            })
        }
    }
}

@Composable
fun AlbumArt() {
    Image(
        painter = painterResource(id = R.drawable.baseline_smart_display_24),
        contentDescription = "Album Art",
        modifier = Modifier
            .size(200.dp)
            .fillMaxSize()
    )
}

@Composable
fun TrackInfo(songTitle: String, artistName: String) {
    Text(text = songTitle, style = MaterialTheme.typography.headlineMedium)
    Text(text = artistName, style = MaterialTheme.typography.titleSmall)
}

@Composable
fun PlaybackControls(
    isPlaying: StateFlow<Boolean>,
    _isPlaying: MutableStateFlow<Boolean>,
    onPlayPauseClicked: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        PlaybackButton(painterResource(id = R.drawable.previous), "Previous")
        PlayPauseButton(isPlaying) { _isPlaying.value = !_isPlaying.value; onPlayPauseClicked() }
        PlaybackButton(painterResource(id = R.drawable.next), "Next")
    }
}

@Composable
fun PlaybackButton(icon: Painter, contentDescription: String) {
    IconButton(onClick = { /* TODO: Implement action */ }) {
        Icon(painter = icon, contentDescription, tint = Color.Gray, modifier = Modifier.size(50.dp))
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun PlayPauseButton(isPlayingFlow: StateFlow<Boolean>, togglePlayPause: () -> Unit) {
    val isPlaying by isPlayingFlow.collectAsState()
    val icon =
        if (isPlaying) painterResource(id = R.drawable.pause) else painterResource(id = R.drawable.play)
    val contentDescription = if (isPlaying) "Pause" else "Play"
    IconButton(onClick = togglePlayPause) {
        Icon(painter = icon, contentDescription, modifier = Modifier.size(50.dp))
    }
}

fun convertMillisecondsToMinutesSeconds(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
