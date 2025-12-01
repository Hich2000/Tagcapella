package com.hich2000.tagcapella.music.playerScreen.queue

import androidx.lifecycle.ViewModel
import com.hich2000.tagcapella.music.mediaController.MediaPlayerCoordinator
import com.hich2000.tagcapella.music.playerState.PlayerState
import com.hich2000.tagcapella.music.queueManager.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class QueueViewModel @Inject constructor(
    private val mediaPlayerCoordinator: MediaPlayerCoordinator,
) : ViewModel() {
    val currentQueue: StateFlow<List<Song>> get() = mediaPlayerCoordinator.currentQueue
    val playerState: StateFlow<PlayerState> get() = mediaPlayerCoordinator.playerState

    fun seek(song: Song) {
        val index = currentQueue.value.indexOf(song)
        mediaPlayerCoordinator.seek(index)
        if (!playerState.value.isPlaying) {
            mediaPlayerCoordinator.play()
        }
    }
}