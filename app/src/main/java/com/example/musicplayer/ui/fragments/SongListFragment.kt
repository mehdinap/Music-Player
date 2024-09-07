@file:SuppressLint("UnsafeOptInUsageError")

package com.example.musicplayer.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ListAdapter
import com.example.musicplayer.R
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.databinding.FragmentSongListBinding
import com.example.musicplayer.ui.adapter.SongsAdapter
import com.example.musicplayer.ui.states.SongsPlaybackUiState
import com.example.musicplayer.ui.viewmodels.SongsSharedViewModel
import com.example.musicplayer.utils.PermissionUtil.checkAndAskPermission
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SongListFragment : Fragment() {

    private var _binding: FragmentSongListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SongsSharedViewModel by activityViewModels()

    private fun uiStateAccessScope(block: suspend SongsPlaybackUiState.() -> Unit) =
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.songsPlaybackUiState.collectLatest {
                block(it)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSongListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkAndAskPermission { setUiComponents() }
    }

    private fun setUiComponents() {
        // setting song list
        setSongsAdapter()
        collectSongs()

        lifecycleScope.launch { // setting player controller
            viewModel.mediaControllerFlow.collectLatest { mediaController ->
                binding.playbackControls.player = mediaController
            }
        }
        // Observe current song and update display name

        observeCurrentSong()
    }

    private fun observeCurrentSong() {
        lifecycleScope.launch {
            viewModel.currentSongName.collect { songName ->
                binding.playbackControls.findViewById<TextView>(R.id.display_name).text =
                    truncateSongName(songName)
            }
        }
    }

    private fun truncateSongName(songName: String?, maxWords: Int = 10): String {
        songName?.let {
            val words = it.split(" ")
            return if (words.size > maxWords) {
                words.take(maxWords).joinToString(" ") + "..."
            } else {
                it
            }
        }
        return "No song playing"
    }

    private fun setSongsAdapter() {
        val songsAdapter = SongsAdapter { position ->
            viewModel.onSongClicked(position)
        }

        binding.songList.adapter = songsAdapter

        binding.playbackControls.setOnClickListener {
            if ((binding.songList.adapter as SongsAdapter).itemCount > 0) {
                val direction =
                    ActionOnlyNavDirections(R.id.action_songListFragment_to_songViewFragment)
                findNavController().navigate(direction)
            }
        }
    }


    @Suppress("UNCHECKED_CAST")
    private fun collectSongs() = uiStateAccessScope {
        songsFlow.collectLatest { songs ->
            (binding.songList.adapter as ListAdapter<Song, *>).submitList(songs)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}