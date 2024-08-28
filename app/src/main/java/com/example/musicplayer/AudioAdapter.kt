package com.example.musicplayer

import android.annotation.SuppressLint
import android.provider.MediaStore.Audio
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AudioAdapter(private val audios: ArrayList<AudioModel>) :
    RecyclerView.Adapter<AudioAdapter.AudioViewHolder>() {

    var audio: ArrayList<AudioModel> = ArrayList<AudioModel>()

    init {
        audio = audios
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): AudioAdapter.AudioViewHolder {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.items, parent, false)
        return AudioViewHolder(item)
    }

    override fun onBindViewHolder(holder: AudioAdapter.AudioViewHolder, position: Int) {
        val currentItem = audio[position]
        holder.rvTitle.text = currentItem.title
        holder.rvArtist.text = currentItem.artist
    }

    override fun getItemCount(): Int {
        return audio.size
    }

    @SuppressLint("CutPasteId")
    inner class AudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //        val rvImage: ImageView
//            rvImage = itemView.findViewById(R.id.image)
        val rvTitle: TextView = itemView.findViewById(R.id.title)
        val rvArtist: TextView = itemView.findViewById(R.id.artiste)
//        val rvDuration: TextView

//        init {
//            rvDuration = itemView.findViewById(R.id.artiste)
//        }
    }
}