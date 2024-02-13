package com.example.musicplayer


import android.media.MediaPlayer
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.app.Application
import android.content.Context
import android.content.res.AssetFileDescriptor
import androidx.lifecycle.AndroidViewModel

class MusicViewModel(application: Application) : AndroidViewModel(application) {
    private var mediaPlayer: MediaPlayer? = null
    private val _sliderPosition = MutableStateFlow(0f)
    val sliderPosition: StateFlow<Float> = _sliderPosition

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    fun useApplicationContext() {
        val context: Context = getApplication<Application>().applicationContext
        val audio = context.resources.openRawResource(R.raw.audio01)

    }


    init {
        initializeMediaPlayer()
    }

    fun initializeMediaPlayer() {
        val context: Context = getApplication<Application>().applicationContext
        val afd: AssetFileDescriptor = context.resources.openRawResourceFd(R.raw.audio01)
        // val audio = context.resources.openRawResource(R.raw.audio01)
        mediaPlayer = MediaPlayer().apply {
            setDataSource(
                afd.fileDescriptor,
                afd.startOffset,
                afd.length
            ) // Set your audio file path or URI here
            prepare() // Prepare the MediaPlayer asynchronously if your source is a network stream
            afd.close()
        }
    }

    fun playMusic() {
        mediaPlayer?.start()
        _isPlaying.value = true
        updatePlaybackPosition()
    }

    fun pauseMusic() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            _isPlaying.value = false
        }
    }

    fun seekTo(position: Float) {
        val newPosition = (position * (mediaPlayer?.duration ?: 0)).toInt()
        mediaPlayer?.seekTo(newPosition)
    }

    fun updatePlaybackPosition() {
        viewModelScope.launch(Dispatchers.IO) {
            while (_isPlaying.value) {
                val currentPosition = mediaPlayer?.currentPosition?.toFloat() ?: 0f
                val duration = mediaPlayer?.duration?.toFloat() ?: 1f
                _sliderPosition.value = currentPosition / duration
                kotlinx.coroutines.delay(1000) // Update every second
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release() // Release the MediaPlayer when the ViewModel is cleared
        mediaPlayer = null
    }
}
