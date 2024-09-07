package com.example.musicplayer.ui.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.data.repository.SongsRepository
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi
@Singleton
class SongPlaybackController @Inject constructor(
    @ApplicationContext private val context: Context,
    val mediaControllerFuture: ListenableFuture<MediaController>,
    songsRepository: SongsRepository,
    private val coroutineScope: CoroutineScope
) : PlaybackController {

    private val mediaController: MediaController?
        get() = if (mediaControllerFuture.isDone) mediaControllerFuture.get()
        else null


    val songsList = songsRepository.getSongsList()

    private var _currentSong: MutableStateFlow<Song?> = MutableStateFlow(null)
    val currentSong get() = _currentSong


    init {
        setMediaControllerListener()
    }


    private fun setMediaControllerListener() {
        mediaControllerFuture.addListener({
            addMediaTransitionListener(mediaController)
            coroutineScope.launch(Dispatchers.Main) {
                songsList.collect { songsList ->
                    mediaController?.setMediaItems(songsList.map { song ->
                        song.mediaItemFromSong()
                    })
                }
            }
        }, MoreExecutors.directExecutor())
    }


    private fun addMediaTransitionListener(mediaController: MediaController?) {
        mediaController?.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                if (mediaItem != null) _currentSong.value = Song(mediaItem, context)
            }
        })
    }


    override fun play(songIndex: Int) {
        mediaController?.apply {
            seekToDefaultPosition(songIndex)
            playWhenReady = true
            prepare()
        }
    }


    override fun destroy() {
        MediaController.releaseFuture(mediaControllerFuture)
    }
}