package com.example.musicplayer


import android.media.MediaPlayer
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.app.Application
import android.content.res.AssetFileDescriptor
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive

class MusicViewModel(application: Application) : AndroidViewModel(application) {
    private var mediaPlayer: MediaPlayer? = null
    private val _isPlaying = MutableStateFlow(false)
    private val _sliderPosition =
        MutableStateFlow(0f) // Represents the slider position as a percentage.
    val sliderPosition: StateFlow<Float> = _sliderPosition
    private val _duration = MutableStateFlow(0f) // Duration in milliseconds
    val duration: StateFlow<Float> = _duration
    private val _currentPosition = MutableStateFlow(0f) // Current position in milliseconds
    val currentPosition: StateFlow<Float> = _currentPosition.asStateFlow()


    init {
        initializeMediaPlayer()
        observePlaybackPosition()
    }

    private fun initializeMediaPlayer() {
        val afd: AssetFileDescriptor =
            getApplication<Application>().resources.openRawResourceFd(R.raw.audio01) ?: return
        mediaPlayer = MediaPlayer().apply {
            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            setOnPreparedListener {
                // Media player is prepared and now has a valid duration.
                _duration.value = duration.toFloat()
                _isPlaying.value = true // Consider starting playback only after preparation.
            }
            prepare() // Prepare synchronously for local files.
            afd.close()
            _duration.value = duration.toFloat() // Set the audio file duration
        }
    }

    private fun observePlaybackPosition() {
        viewModelScope.launch(Dispatchers.IO) {
            while (isActive) { // Keep the loop running as long as the ViewModel is active
                if (_isPlaying.value) {
                    val position = mediaPlayer?.currentPosition ?: 0
                    _currentPosition.value = position.toFloat()
                    val totalDuration = mediaPlayer?.duration ?: -1
                    if (totalDuration > 0) {
                        val newPosition = position.toFloat()
                        _sliderPosition.value = newPosition
                    } else {
                        Log.d("MusicViewModel", "Invalid duration: $totalDuration")
                    }
                    delay(1000) // Update every second or adjust based on your needs
                } else {
                    delay(500) // Less frequent checks when paused
                }
            }
        }
    }

    fun playMusic() {
        mediaPlayer?.start()
        _isPlaying.value = true
        observePlaybackPosition()
    }

    fun pauseMusic() {
        mediaPlayer?.pause()
        _isPlaying.value = false
    }

    fun seekTo(positionPercentage: Float) {
        val newPosition = ((positionPercentage) * (mediaPlayer?.duration ?: 0)).toInt()
        mediaPlayer?.seekTo(newPosition)
    }

    override fun onCleared() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onCleared()
    }
}
