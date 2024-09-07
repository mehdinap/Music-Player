package com.example.musicplayer.ui.player

import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.musicplayer.utils.NullifyField.resetField
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@UnstableApi
@AndroidEntryPoint
class PlayerService : MediaSessionService() {
    @Inject
    lateinit var mediaSession: MediaSession

    override fun onCreate() {
        super.onCreate()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession

    override fun onDestroy() {
        mediaSession.run {
            player.release()
            release()
        }
        resetField(this, "mediaSession")
        super.onDestroy()
    }
}