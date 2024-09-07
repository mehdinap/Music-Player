package com.example.musicplayer.ui.player

interface PlaybackController {
    fun play(songIndex: Int)
    fun destroy()
}