package com.example.musicplayer

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class MusicService : Service() {
    private lateinit var mediaPlayer: MediaPlayer

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        when (action) {
            "PLAY" -> playMusic()
            "PAUSE" -> pauseMusic()
        }
        return START_STICKY
    }

    private fun playMusic() {
        if (!this::mediaPlayer.isInitialized) {
            // Initialize MediaPlayer and start playback
            mediaPlayer = MediaPlayer.create(this, R.raw.audio01)
            mediaPlayer.isLooping = true
            mediaPlayer.start()
        } else if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    private fun pauseMusic() {
        if (this::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}