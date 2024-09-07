package com.example.musicplayer.data.model

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import com.example.musicplayer.utils.MediaItemExts.getDuration


data class Song(
    val id: Long,
    val title: String,
    val album: String,
    val artist: String,
    val duration: Long,
    val uri: Uri) {
    constructor(mediaItem: MediaItem, context: Context) : this(
        id = mediaItem.mediaId.substringAfterLast("/").toLong(),
        title = mediaItem.mediaMetadata.title.toString(),
        album = mediaItem.mediaMetadata.albumTitle.toString(),
        artist = mediaItem.mediaMetadata.artist.toString(),
        duration = mediaItem.getDuration(context),
        uri = Uri.parse(mediaItem.mediaId)
    )

    @UnstableApi
    fun mediaItemFromSong(): MediaItem {
        return MediaItem.Builder()
            .setUri(uri)
            .setMediaId(uri.toString())
            .setMimeType(MimeTypes.BASE_TYPE_AUDIO)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setAlbumTitle(album)
                    .setArtist(artist)
                    .build()
            ).build()
    }
}

fun Long.getAsMinutesColonSeconds(): String = String.format("%02d:%02d", this / 60000, (this % 60000) / 1000)