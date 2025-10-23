package com.hich2000.tagcapella.newmusic.playerScreen

import androidx.lifecycle.ViewModel
import com.hich2000.tagcapella.newmusic.mediaController.MediaPlayerCoordinator
import com.hich2000.tagcapella.newmusic.mediaController.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PlayerScreenViewModel @Inject constructor(
    private val mediaPlayerCoordinator: MediaPlayerCoordinator
) : ViewModel() {

    val playerState: StateFlow<PlayerState> get() = mediaPlayerCoordinator.playerState

    fun pausePlay() {
        if (playerState.value.isPlaying) {
            mediaPlayerCoordinator.pause()
        } else {
            mediaPlayerCoordinator.play()
        }
    }

    fun next() = mediaPlayerCoordinator.next()
    fun previous() = mediaPlayerCoordinator.previous()
    fun shuffleMode() = mediaPlayerCoordinator.shuffleMode()
    fun loopMode() = mediaPlayerCoordinator.loopMode()
    fun seek(position: Long) = mediaPlayerCoordinator.seek(position)
}