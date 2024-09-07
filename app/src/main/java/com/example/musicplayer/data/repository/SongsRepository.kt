package com.example.musicplayer.data.repository

import com.example.musicplayer.data.local.SongsDatasource
import com.example.musicplayer.data.model.Song
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SongsRepository @Inject constructor(
    private val songsDatasource: SongsDatasource
) {
    fun getSongsList(): Flow<List<Song>> = songsDatasource.songsListFlow
}