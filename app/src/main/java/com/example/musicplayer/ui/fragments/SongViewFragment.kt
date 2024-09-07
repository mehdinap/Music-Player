@file:SuppressLint("UnsafeOptInUsageError")

package com.example.musicplayer.ui.fragments


import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.Player
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.ui.states.SongsPlaybackUiState
import com.example.musicplayer.utils.DefaultGlideLogger
import com.bumptech.glide.Glide
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentSongViewBinding
import com.example.musicplayer.ui.viewmodels.SongsSharedViewModel
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.FileNotFoundException


private const val TAG = "SongViewFragment"

class SongViewFragment : Fragment() {
    private var _binding: FragmentSongViewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SongsSharedViewModel by activityViewModels()

    private fun uiStateAccessScope(block: suspend SongsPlaybackUiState.() -> Unit) =
        lifecycleScope.launch {
            viewModel.songsPlaybackUiState.collectLatest {
                block(it)
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSongViewBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setCurrentSongFlow()
        setUiComponents()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setUiComponents() {
        setPlayerController()
        setBackground()
        setSongProperties()
        setSongCoverView()
        setHideViewButton()
    }

    private fun setSongProperties() = uiStateAccessScope {
        currentSong.collectLatest { song ->
            binding.apply {
                songTitle.text = song?.title
                songArtist.text = song?.artist
            }
        }
    }


    private fun setHideViewButton() = binding.hideButton.setOnClickListener {
        findNavController().popBackStack()
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setBackground() = uiStateAccessScope {
        currentSong.collectLatest { song ->
            val songCoverBitmap = loadSongCoverBitmap(song!!)
            loadBlurredBackground(songCoverBitmap)
        }
    }


    private fun loadBlurredBackground(songCoverBitmap: Bitmap?) =
        Glide.with(binding.root.context).asBitmap()
            .load(songCoverBitmap ?: R.drawable.music_cover_placeholder)
            .addListener(DefaultGlideLogger(TAG)).transform((BlurTransformation()))
            .into(binding.backgroundBlurred)


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setSongCoverView() = uiStateAccessScope {
        currentSong.collectLatest { song ->
            loadSongCover(song)
            applyPlayPauseAnimation()
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun loadSongCover(song: Song?) {
        val songCoverBitmap = song?.let {
            loadSongCoverBitmap(song)
        }
        Glide.with(binding.root.context).load(songCoverBitmap ?: R.drawable.music_cover_placeholder)
            .addListener(DefaultGlideLogger(TAG)).into(binding.songCover)
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun loadSongCoverBitmap(song: Song): Bitmap? = try {
        val (width, height) = listOf(
            R.dimen.song_view_song_cover_width, R.dimen.song_view_song_cover_height
        ).map { resId ->
            binding.root.resources.getDimension(resId).toInt()
        }
        binding.root.context.contentResolver.loadThumbnail(
            song.uri, Size(width, height), null
        )
    } catch (e: FileNotFoundException) {
        null
    }


    private fun setPlayerController() = lifecycleScope.launch {
        viewModel.mediaControllerFlow.collectLatest { mediaController ->
            binding.playbackControls.player = mediaController
            mediaController?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    viewModel.setPlayingState(isPlaying)
                }
            })
        }
    }

    private fun applyPlayPauseAnimation() = lifecycleScope.launch {
        viewModel.isPlaying.collect { isPlaying ->
            val scale = if (isPlaying) 1.0f else 0.8f
            binding.songCover.animate().scaleX(scale).scaleY(scale).setDuration(300).start()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}