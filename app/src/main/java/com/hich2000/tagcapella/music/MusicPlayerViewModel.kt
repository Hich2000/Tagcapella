package com.hich2000.tagcapella.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.session.MediaController
import com.hich2000.tagcapella.music.mediaController.MediaControllerManager
import com.hich2000.tagcapella.music.mediaController.PlayerState
import com.hich2000.tagcapella.newmusic.Song
import com.hich2000.tagcapella.newmusic.SongRepository
import com.hich2000.tagcapella.tagsAndCategories.tags.TagDTO
import com.hich2000.tagcapella.utils.sharedPreferences.SharedPreferenceKey
import com.hich2000.tagcapella.utils.sharedPreferences.SharedPreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicPlayerViewModel @Inject constructor(
    private val mediaControllerManager: MediaControllerManager,
    private val songRepository: SongRepository,
    private val sharedPreferenceManager: SharedPreferenceManager,
) : ViewModel() {

    val mediaController: StateFlow<MediaController?> get() = mediaControllerManager.mediaController

    // State management
    val playerState: StateFlow<PlayerState> get() = mediaControllerManager.playerState

    val isMediaControllerInitialized: StateFlow<Boolean> get() = mediaControllerManager.isMediaControllerInitialized
    val currentPlaylist: StateFlow<List<Song>> get() = mediaControllerManager.currentPlaylist
    val includedTags: StateFlow<List<TagDTO>> get() = mediaControllerManager.includedTags
    val excludedTags: StateFlow<List<TagDTO>> get() = mediaControllerManager.excludedTags

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> get() = _showDialog

    fun preparePlaylist(playlist: List<Song>) {
        viewModelScope.launch {
            mediaControllerManager.preparePlaylist(playlist)
        }
    }

    suspend fun getFilteredPlaylist(
        includeTags: List<TagDTO> = listOf(),
        excludeTags: List<TagDTO> = listOf()
    ): List<Song> {

        val jsonIncluded: List<Long> = includeTags.map { it.id }
        val jsonExcluded: List<Long> = excludeTags.map { it.id }
        sharedPreferenceManager.savePreference(SharedPreferenceKey.IncludedTags, jsonIncluded)
        sharedPreferenceManager.savePreference(SharedPreferenceKey.ExcludedTags, jsonExcluded)

        songRepository.isInitialized.first { it }
        return songRepository.filterSongList(includeTags, excludeTags)
    }

    fun setPlaybackPosition(position: Number) {
        mediaControllerManager.setPlaybackPosition(position)
    }

    fun addIncludedTag(tag: TagDTO) {
        mediaControllerManager.addIncludedTag(tag)
    }

    fun togglePlayback()
    {
        mediaControllerManager.togglePlayback()
    }

    fun removeIncludedTag(tag: TagDTO) {
        mediaControllerManager.removeIncludedTag(tag)
    }

    fun addExcludedTag(tag: TagDTO) {
        mediaControllerManager.addExcludedTag(tag)
    }

    fun removeExcludedTag(tag: TagDTO) {
        mediaControllerManager.removeExcludedTag(tag)
    }

    fun openDialog() {
        _showDialog.value = true
    }

    fun closeDialog() {
        _showDialog.value = false
    }
}

