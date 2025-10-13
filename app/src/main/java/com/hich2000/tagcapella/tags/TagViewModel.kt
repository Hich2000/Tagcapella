package com.hich2000.tagcapella.tags

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hich2000.tagcapella.songs.Song
import com.hich2000.tagcapella.utils.ToastEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagViewModel @Inject constructor(
    private val tagRepository: TagRepository,
) : ViewModel() {

    private var _tags = MutableStateFlow<List<TagDTO>>(emptyList())
    val tags: StateFlow<List<TagDTO>> get() = _tags

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> get() = _showDialog

    init {
        _tags.value = selectAllTags()
    }

    private fun selectAllTags(): List<TagDTO> {
        return tagRepository.selectAllTags()
    }

    fun insertTag(tag: String, category: Long?) {
        viewModelScope.launch {
            try {
                tagRepository.insertTag(newTag = tag, category = category)
                _tags.value = selectAllTags()
            } catch (_: Throwable) {
                ToastEventBus.send("Tag already exists with name: $tag")
            }
        }
    }

    fun updateTag(id: Long, tag: String, category: Long?) {
        viewModelScope.launch {
            try {
                tagRepository.updateTag(id = id, tag = tag, category = category)
                _tags.value = selectAllTags()
            } catch (_: Throwable) {
                ToastEventBus.send("Tag already exists with name: $tag")
            }
        }
    }

    fun deleteTag(id: Long) {
        val deleteIndex = _tags.value.indexOfFirst { it.id == id }
        tagRepository.deleteTag(_tags.value[deleteIndex].id)
        _tags.value = selectAllTags()
    }

    fun addSongTag(tag: TagDTO, song: Song) {
        tagRepository.addSongTag(tag = tag, song = song)
    }

    fun deleteSongTag(tag: TagDTO, song: Song) {
        tagRepository.deleteSongTag(tag = tag, song = song)
    }

    fun openDialog() {
        _showDialog.value = true
    }

    fun closeDialog() {
        _showDialog.value = false
    }
}