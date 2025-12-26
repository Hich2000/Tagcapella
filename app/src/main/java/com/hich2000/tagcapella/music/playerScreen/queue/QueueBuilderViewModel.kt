package com.hich2000.tagcapella.music.playerScreen.queue

import androidx.lifecycle.ViewModel
import com.hich2000.tagcapella.music.mediaController.MediaPlayerCoordinator
import com.hich2000.tagcapella.tagsAndCategories.tags.Tag
import com.hich2000.tagcapella.tagsAndCategories.tags.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class QueueBuilderViewModel @Inject constructor(
    private val tagRepository: TagRepository,
    private val mediaPlayerCoordinator: MediaPlayerCoordinator,
) : ViewModel() {
    val tags: StateFlow<List<Tag>> get() = tagRepository.tags
    val includedTags: StateFlow<List<Tag>> get() = mediaPlayerCoordinator.includedTags
    val excludedTags: StateFlow<List<Tag>> get() = mediaPlayerCoordinator.excludedTags

    fun toggleTagFilter(tag: Tag) {
        mediaPlayerCoordinator.toggleTagInFilter(tag)
    }

    fun updateQueue() = mediaPlayerCoordinator.updateQueue()
}