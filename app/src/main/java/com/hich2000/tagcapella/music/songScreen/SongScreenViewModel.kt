package com.hich2000.tagcapella.music.songScreen

import androidx.lifecycle.ViewModel
import com.hich2000.tagcapella.music.mediaController.MediaControllerManager
import com.hich2000.tagcapella.songs.Song
import com.hich2000.tagcapella.songs.SongRepository
import com.hich2000.tagcapella.tags.TagDTO
import com.hich2000.tagcapella.tags.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SongScreenViewModel @Inject constructor(
    private val songRepository: SongRepository,
    private val tagRepository: TagRepository,
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
        songToTag.value?.let { tagRepository.addSongTag(tag, it) }
    }

    fun deleteSongTag(tag: TagDTO) {
        songToTag.value?.let { tagRepository.deleteSongTag(tag, it) }
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