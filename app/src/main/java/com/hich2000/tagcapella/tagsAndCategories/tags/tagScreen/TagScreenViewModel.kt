package com.hich2000.tagcapella.tagsAndCategories.tags.tagScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hich2000.tagcapella.music.queueManager.Song
import com.hich2000.tagcapella.music.queueManager.SongRepository
import com.hich2000.tagcapella.tagsAndCategories.tags.Tag
import com.hich2000.tagcapella.tagsAndCategories.tags.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagScreenViewModel @Inject constructor(
    private val tagRepository: TagRepository,
    private val songRepository: SongRepository
) : ViewModel() {
    val tags: StateFlow<List<Tag>> get() = tagRepository.tags
    val songs: StateFlow<List<Song>> get() = songRepository.songList

    private val _clickedTag = MutableStateFlow<Tag?>(null)
    val clickedTag: StateFlow<Tag?> get() = _clickedTag

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> get() = _showDialog

    fun deleteTag(id: Long) {
        viewModelScope.launch {
            val deleteIndex = tags.value.indexOfFirst { it.id == id }
            tagRepository.deleteTag(tags.value[deleteIndex].id)
        }
    }

    fun openDialog() {
        _showDialog.value = true
    }

    fun closeDialog() {
        _showDialog.value = false
    }

    fun setClickedTag(tag: Tag?) {
        _clickedTag.value = tag
    }
}