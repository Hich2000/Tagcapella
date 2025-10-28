package com.hich2000.tagcapella.music.mediaController

import android.app.Application
import android.content.ComponentName
import androidx.concurrent.futures.await
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.hich2000.tagcapella.music.queueManager.Song
import com.hich2000.tagcapella.music.playerState.PlayerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaControllerManager @Inject constructor(
    private val application: Application,
) {

    private val _isMediaControllerInitialized = MutableStateFlow(false)
    val isMediaControllerInitialized: StateFlow<Boolean> get() = _isMediaControllerInitialized

    private val _mediaController: MutableStateFlow<MediaController?> =
        MutableStateFlow(null)
    val mediaController: StateFlow<MediaController?> get() = _mediaController

    suspend fun initialize() {
        initMediaController()
    }

    private suspend fun initMediaController() {
        // Reuse existing MediaController if it's already connected
        if (_mediaController.value != null) {
            _isMediaControllerInitialized.value = true
            return
        }

        _isMediaControllerInitialized.value = try {
            val sessionToken =
                SessionToken(application, ComponentName(application, PlaybackService::class.java))
            val controllerFuture = MediaController.Builder(application, sessionToken).buildAsync()
            _mediaController.value = controllerFuture.await()

            true
        } catch (_: Throwable) {
            false
        }
    }

    fun setPlayerState(playerState: PlayerState) {
        _mediaController.value?.shuffleModeEnabled = playerState.shuffleModeEnabled
        _mediaController.value?.repeatMode = playerState.repeatMode

        _mediaController.value?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                val lastPlayedSongIndex = findMediaItemIndexById(playerState.currentSong)
                if (playerState.currentSong.isNotEmpty() && lastPlayedSongIndex != -1) {
                    _mediaController.value?.seekTo(lastPlayedSongIndex, playerState.position)
                } else {
                    _mediaController.value?.seekTo(playerState.position)
                }

                _mediaController.value?.removeListener(this)
            }
        })
    }

    fun findMediaItemIndexById(mediaId: String): Int {
        if (_mediaController.value == null) {
            return -1
        }

        for (i: Int in 0 until _mediaController.value!!.mediaItemCount) {
            if (_mediaController.value!!.getMediaItemAt(i).mediaId == mediaId) {
                return i
            }
        }

        return -1
    }

    fun setQueue(queue: List<Song>) {
        val mediaItems = queue.map {
            MediaItem.Builder()
                .setMediaId(it.path)
                .setUri(it.path)
                .setMediaMetadata(
                    MediaMetadata.Builder().setTitle(File(it.path).nameWithoutExtension).build()
                )
                .build()
        }
        _mediaController.value?.clearMediaItems()
        _mediaController.value?.addMediaItems(mediaItems)
    }

    fun prepare() = _mediaController.value?.prepare()
    fun play() = _mediaController.value?.play()
    fun pause() = _mediaController.value?.pause()
    fun next() = _mediaController.value?.seekToNext()
    fun previous() = _mediaController.value?.seekToPrevious()
    fun seek(position: Long) = _mediaController.value?.seekTo(position)
    fun seek(queueIndex: Int) = _mediaController.value?.seekTo(queueIndex, 0L)

    fun shuffleMode() {
        _mediaController.value?.shuffleModeEnabled?.let {
            _mediaController.value?.shuffleModeEnabled = !it
        }
    }

    fun loopMode() {
        when (_mediaController.value?.repeatMode) {
            Player.REPEAT_MODE_OFF -> {
                _mediaController.value?.repeatMode = Player.REPEAT_MODE_ALL
            }

            Player.REPEAT_MODE_ALL -> {
                _mediaController.value?.repeatMode = Player.REPEAT_MODE_ONE
            }

            Player.REPEAT_MODE_ONE -> {
                _mediaController.value?.repeatMode = Player.REPEAT_MODE_OFF
            }
        }
    }
}