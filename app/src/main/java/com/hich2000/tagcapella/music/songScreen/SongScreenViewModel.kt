package com.hich2000.tagcapella.music.songScreen

import androidx.lifecycle.ViewModel
import com.hich2000.tagcapella.music.queueManager.Song
import com.hich2000.tagcapella.music.queueManager.SongRepository
import com.hich2000.tagcapella.tagsAndCategories.tags.Tag
import com.hich2000.tagcapella.tagsAndCategories.tags.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SongScreenViewModel @Inject constructor(
    private val songRepository: SongRepository,
    private val tagRepository: TagRepository,
) : ViewModel() {
    val songRepoInitialized: StateFlow<Boolean> get() = songRepository.isInitialized
    val songs: StateFlow<List<Song>> get() = songRepository.songList

    private val _showTagDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showTagDialog: StateFlow<Boolean> get() = _showTagDialog

    private val _songToTag: MutableStateFlow<Song?> = MutableStateFlow(null)
    val songToTag: StateFlow<Song?> get() = _songToTag

    fun addSongTag(tag: Tag) {
        songToTag.value?.let {
            songRepository.addSongTag(it, tag)
            updateSongToTag()
        }
    }

    fun deleteSongTag(tag: Tag) {
        songToTag.value?.let {
            songRepository.deleteSongTag(it, tag)
            updateSongToTag()
        }
    }

    fun openDialog(song: Song?) {
        _songToTag.value = song
        _showTagDialog.value = true
    }

    fun closeDialog() {
        _songToTag.value = null
        _showTagDialog.value = false
    }

    private fun updateSongToTag() {
        _songToTag.value = songs.value.firstOrNull{ song: Song ->
            song.path == _songToTag.value!!.path
        }
        //todo maybe make this better by not re-initializing the entire tag list.
        tagRepository.initTagList()
    }
}