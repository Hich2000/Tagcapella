package com.hich2000.tagcapella.music_player

import android.app.Application
import android.content.ComponentName
import androidx.concurrent.futures.await
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaControllerManager @Inject constructor(
    private val application: Application
) {
    private var mediaController: MediaController? = null

    suspend fun initializeMediaController(): MediaController {

        // Reuse existing MediaController if it's already connected
        mediaController?.let { return it }

        val sessionToken = SessionToken(application, ComponentName(application, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(application, sessionToken).buildAsync()
        mediaController = controllerFuture.await()


        val audioAttributes: AudioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        mediaController!!.setAudioAttributes(audioAttributes, true)

        return mediaController!!
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


@Module
@InstallIn(SingletonComponent::class)
object MediaControllerModule {

    @Provides
    @Singleton
    fun provideMediaControllerManager(application: Application): MediaControllerManager {
        return MediaControllerManager(application)
    }
}