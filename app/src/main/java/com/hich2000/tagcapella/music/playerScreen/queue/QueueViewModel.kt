package com.hich2000.tagcapella.music.playerScreen.queue

import androidx.lifecycle.ViewModel
import com.hich2000.tagcapella.music.mediaController.MediaPlayerCoordinator
import com.hich2000.tagcapella.music.queueManager.Song
import com.hich2000.tagcapella.tagsAndCategories.tags.Tag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class QueueViewModel @Inject constructor(
    private val mediaPlayerCoordinator: MediaPlayerCoordinator,
) : ViewModel() {

    private val _showDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> get() = _showDialog

    val currentQueue: StateFlow<List<Song>> get() = mediaPlayerCoordinator.currentQueue
    val includedTags: StateFlow<List<Tag>> get() = mediaPlayerCoordinator.includedTags
    val excludedTags: StateFlow<List<Tag>> get() = mediaPlayerCoordinator.excludedTags

    fun seek(song: Song) {
        val index = currentQueue.value.indexOf(song)
        mediaPlayerCoordinator.seek(index)
    }

    fun openDialog() {
        _showDialog.value = true
    }

    fun closeDialog() {
        _showDialog.value = false
    }

    fun toggleTagFilter(tag: Tag) {
        mediaPlayerCoordinator.toggleTagInFilter(tag)
    }

    fun updateQueue() = mediaPlayerCoordinator.updateQueue()
}