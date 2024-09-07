package com.example.musicplayer.ui.di

import com.example.musicplayer.ui.player.MediaSessionCallback
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object MediaSessionCallbackProvider {
    @Singleton
    @Provides
    fun provideMediaLibrarySessionCallback() = MediaSessionCallback()
}