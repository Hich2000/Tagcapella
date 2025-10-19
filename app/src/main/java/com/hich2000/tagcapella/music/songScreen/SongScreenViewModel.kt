package com.hich2000.tagcapella.music.songScreen

import androidx.lifecycle.ViewModel
import com.hich2000.tagcapella.music.mediaController.MediaControllerManager
import com.hich2000.tagcapella.newmusic.Song
import com.hich2000.tagcapella.newmusic.SongRepository
import com.hich2000.tagcapella.tagsAndCategories.tags.TagDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SongScreenViewModel @Inject constructor(
    private val songRepository: SongRepository,
    private val mediaControllerManager: MediaControllerManager
) : ViewModel() {
    val songRepoInitialized: StateFlow<Boolean> get() = songRepository.isInitialized
    val songs: StateFlow<List<Song>> get() = songRepository.songList

    private val _showTagDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showTagDialog: StateFlow<Boolean> get() = _showTagDialog

    private val _songToTag: MutableStateFlow<Song?> = MutableStateFlow(null)
    val songToTag: StateFlow<Song?> get() = _songToTag

    val includedTags: StateFlow<List<TagDTO>> get() = mediaControllerManager.includedTags
    val excludedTags: StateFlow<List<TagDTO>> get() = mediaControllerManager.excludedTags

    fun addSongTag(tag: TagDTO) {
        songToTag.value?.let {
            songRepository.addSongTag(it, tag)
        }
    }

    fun deleteSongTag(tag: TagDTO) {
        songToTag.value?.let {
            songRepository.deleteSongTag(it, tag)
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
}