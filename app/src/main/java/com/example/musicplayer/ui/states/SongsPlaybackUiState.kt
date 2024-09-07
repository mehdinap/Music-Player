package com.example.musicplayer.ui.states

import com.example.musicplayer.data.model.Song
import kotlinx.coroutines.flow.MutableStateFlow

data class SongsPlaybackUiState(
    val songsFlow: MutableStateFlow<List<Song>>,
    val currentSong: MutableStateFlow<Song?>,
)
