@file:SuppressLint("UnsafeOptInUsageError")

package com.example.musicplayer.ui.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.session.MediaController
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.ui.player.SongPlaybackController
import com.example.musicplayer.ui.states.SongsPlaybackUiState
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongsSharedViewModel @Inject constructor(
    private val playbackController: SongPlaybackController,
) : ViewModel() {

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> get() = _isPlaying

    fun setPlayingState(isPlaying: Boolean) {
        _isPlaying.value = isPlaying
    }

    private val _mediaControllerFlow: MutableStateFlow<MediaController?> = MutableStateFlow(null)
    val mediaControllerFlow get() = _mediaControllerFlow

    private val _currentSongName: MutableStateFlow<String?> = MutableStateFlow(null)
    val currentSongName: StateFlow<String?> get() = _currentSongName

    private val _songsPlaybackUiState: StateFlow<SongsPlaybackUiState> = MutableStateFlow(
        SongsPlaybackUiState(
            songsFlow = MutableStateFlow(emptyList()),
            currentSong = MutableStateFlow(null)
        )
    )
    val songsPlaybackUiState: StateFlow<SongsPlaybackUiState>
        get() = _songsPlaybackUiState

    init {
        playbackController.mediaControllerFuture.addListener(
            this::loadSongsListener,
            MoreExecutors.directExecutor()
        )
        playbackController.mediaControllerFuture.addListener({
            _mediaControllerFlow.value = playbackController.mediaControllerFuture.get()
        }, MoreExecutors.directExecutor())

        viewModelScope.launch {
            playbackController.currentSong.collect { song ->
                _currentSongName.value = song?.title // Assuming `Song` has a `title` property
            }
        }
    }


    private fun uiStateModificationScope(block: suspend SongsPlaybackUiState.() -> Unit) =
        viewModelScope.launch {
            songsPlaybackUiState.collectLatest {
                block(it)
            }
        }

    private fun loadSongsListener() = viewModelScope.launch(Dispatchers.IO) {
        uiStateModificationScope {
            playbackController.songsList.collectLatest {
                songsFlow.value = it
            }
        }
    }


    fun onSongClicked(position: Int) {
        playbackController.play(position)
    }


    fun setCurrentSongFlow() = viewModelScope.launch {
        playbackController.currentSong.collect {
            uiStateModificationScope {
                currentSong.value = it
            }
        }
    }
}