package com.hich2000.tagcapella.music_player

import android.app.Application
import android.content.ComponentName
import androidx.concurrent.futures.await
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import javax.inject.Inject

class MediaControllerManager @Inject constructor(
    private val application: Application
) {
    private lateinit var mediaController: MediaController

    suspend fun initializeMediaController(): MediaController {
        val sessionToken = SessionToken(application, ComponentName(application, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(application, sessionToken).buildAsync()
        mediaController = controllerFuture.await()
        return mediaController
    }

    fun preparePlaylist(controller: MediaController, playlist: List<SongDTO>) {
        val mediaItems = playlist.map {
            MediaItem.Builder()
                .setMediaId(it.path)
                .setUri(it.path)
                .setMediaMetadata(MediaMetadata.Builder().setTitle(it.title).build())
                .build()
        }
        controller.clearMediaItems()
        controller.addMediaItems(mediaItems)
        controller.prepare()
    }
}