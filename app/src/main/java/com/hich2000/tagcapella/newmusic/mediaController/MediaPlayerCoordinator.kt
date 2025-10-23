package com.hich2000.tagcapella.newmusic.mediaController

import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.hich2000.tagcapella.newmusic.Song
import com.hich2000.tagcapella.utils.applicationScope.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaPlayerCoordinator @Inject constructor(
    private val mediaControllerManager: MediaControllerManager,
    private val playerStateManager: PlayerStateManager,
    private val queueManager: QueueManager,
    private val mediaOutputChangeReceiver: MediaOutputChangeReceiver,
    @ApplicationScope private val applicationScope: CoroutineScope
) {

    val mediaControllerInit: StateFlow<Boolean> get() = mediaControllerManager.isMediaControllerInitialized
    val mediaController: StateFlow<MediaController?> get() = mediaControllerManager.mediaController
    val queue: StateFlow<List<Song>> get() = queueManager.currentQueue
    val playerState: StateFlow<PlayerState> get() = playerStateManager.playerState

    init {
        applicationScope.launch {
            playerStateManager.initPlayerState()
            mediaControllerManager.initialize()
            mediaControllerInit.first()
            mediaController.value?.addListener(
                playerStateManager.createListener(
                    onIsPlayingChanged = {
                        val controller = mediaController.value ?: return@createListener
                        if (controller.playbackState != Player.STATE_READY) return@createListener
                        playerStateManager.setIsPlaying(controller.isPlaying)
                    },
                    onPlaybackStateReady = {
                        mediaController.value?.duration?.let {
                            playerStateManager.updateTimeline(
                                playerState.value.position,
                                it
                            )
                        }
                    }
                ))
            mediaController.value?.let { mediaOutputChangeReceiver.setGettingNoisyReceiver(it) }

            withContext(Dispatchers.Main) {
                queueManager.initFilters()
                mediaControllerManager.setQueue(queue.value)
                mediaControllerManager.setPlayerState(playerState.value)
                //get the duration and position of the current song every second
                while (true) {
                    val controller = mediaController.value ?: continue
                    if (controller.playbackState != Player.STATE_READY) {
                        delay(1000)
                        continue
                    }
                    playerStateManager.updateTimeline(
                        controller.currentPosition,
                        controller.duration
                    )

                    delay(1000)
                }
            }
        }
    }

    fun cleanup() {
        mediaOutputChangeReceiver.cleanup()
    }

    fun play() = mediaControllerManager.play()
    fun pause() = mediaControllerManager.pause()
    fun next() = mediaControllerManager.next()
    fun previous() = mediaControllerManager.previous()
    fun shuffleMode() = mediaControllerManager.shuffleMode()
    fun loopMode() = mediaControllerManager.loopMode()
    fun seek(position: Long) = mediaControllerManager.seek(position)
}