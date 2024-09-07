package com.example.musicplayer.ui.adapter

import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.data.model.getAsMinutesColonSeconds
import com.bumptech.glide.Glide
import com.example.musicplayer.R
import com.example.musicplayer.databinding.SongViewHolderBinding
import java.io.FileNotFoundException

class SongsAdapter(
    private val songOnClickListener: ((Int) -> Unit)
) : ListAdapter<Song, SongsAdapter.SongViewHolder>(DiffCallback) {
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Song>() {
            override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder =
        SongViewHolder(
            SongViewHolderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
        )


    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.onSongClickListener = songOnClickListener
    }

    class SongViewHolder(
        private val binding: SongViewHolderBinding,
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        lateinit var onSongClickListener: ((Int) -> Unit)

        private val songCoverResId = listOf(R.dimen.song_cover_width, R.dimen.song_cover_height)
        private val size: Size
            get() {
                val (width, height) = songCoverResId.map { resId ->
                    binding.root.resources.getDimension(resId).toInt()
                }

                return Size(width, height)
            }

        fun bind(song: Song) {
            binding.apply {
                songTitle.text = song.title
                artistName.text = song.artist
                songDuration.text = song.duration.getAsMinutesColonSeconds()
            }
            bindSongCover(song)
            binding.root.clipToOutline = true
            binding.root.setOnClickListener(this)
        }

        private fun bindSongCover(song: Song) {
            try {
                val songCoverBitmap =
                    binding.root.context.contentResolver.loadThumbnail(song.uri, size, null)
                Glide.with(binding.root.context).load(songCoverBitmap)
                    .placeholder(R.drawable.music_cover_placeholder).dontTransform()
                    .into(binding.songCover)
            } catch (e: FileNotFoundException) {
                Glide.with(binding.root.context).load(R.drawable.music_cover_placeholder)
                    .into(binding.songCover)
            }
        }

        override fun onClick(p0: View?) {
            onSongClickListener(absoluteAdapterPosition)
        }
    }
}