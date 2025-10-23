package com.hich2000.tagcapella.tagsAndCategories.tags.tagScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hich2000.tagcapella.music.Song
import com.hich2000.tagcapella.music.SongRepository
import com.hich2000.tagcapella.tagsAndCategories.tags.TagDTO
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
    val tags: StateFlow<List<TagDTO>> get() = tagRepository.tags
    val songs: StateFlow<List<Song>> get() = songRepository.songList

    private val _clickedTag = MutableStateFlow<TagDTO?>(null)
    val clickedTag: StateFlow<TagDTO?> get() = _clickedTag

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> get() = _showDialog

    fun deleteTag(id: Long) {
        viewModelScope.launch {
            val deleteIndex = tags.value.indexOfFirst { it.id == id }
            tagRepository.deleteTag(tags.value[deleteIndex].id)
        }
    }

    fun addSongTag(tag: TagDTO, song: Song) {
        songRepository.addSongTag(song = song, tag = tag)
        tagRepository.initTagList()
    }

    fun deleteSongTag(tag: TagDTO, song: Song) {
        songRepository.deleteSongTag(song = song, tag = tag)
        tagRepository.initTagList()
    }

    fun openDialog() {
        _showDialog.value = true
    }

    fun closeDialog() {
        _showDialog.value = false
    }

    fun setClickedTag(tag: TagDTO?) {
        _clickedTag.value = tag
    }
}