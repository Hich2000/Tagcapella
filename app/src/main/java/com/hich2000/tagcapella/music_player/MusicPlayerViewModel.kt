package com.hich2000.tagcapella.music_player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.hich2000.tagcapella.tags.TagDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicPlayerViewModel @Inject constructor(
    private val mediaControllerManager: MediaControllerManager,
    private val songRepository: SongRepository
) : ViewModel() {

    private lateinit var _mediaController: MediaController
    val mediaController: MediaController get() = _mediaController

    // State management
    private val _shuffleModeEnabled = MutableStateFlow(false)
    val shuffleModeEnabled: StateFlow<Boolean> get() = _shuffleModeEnabled

    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_ALL)
    val repeatMode: StateFlow<Int> get() = _repeatMode

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> get() = _isPlaying

    private val _isMediaControllerInitialized = MutableStateFlow(false)
    val isMediaControllerInitialized: StateFlow<Boolean> get() = _isMediaControllerInitialized.asStateFlow()

    private var _includedTags = MutableStateFlow<List<TagDTO>>(emptyList())
    val includedTags: StateFlow<List<TagDTO>> get() = _includedTags.asStateFlow()

    private val _excludedTags = MutableStateFlow<List<TagDTO>>(emptyList())
    val excludedTags: StateFlow<List<TagDTO>> get() = _excludedTags.asStateFlow()

    private val _currentPlaylist = MutableStateFlow<List<SongDTO>>(emptyList())
    val currentPlaylist: StateFlow<List<SongDTO>> get() = _currentPlaylist.asStateFlow()

    init {
        viewModelScope.launch {
            _isMediaControllerInitialized.value = try {
                val controller = mediaControllerManager.initializeMediaController()
                observeMediaControllerState(controller)
                _mediaController = controller
                _mediaController.repeatMode = Player.REPEAT_MODE_ALL

                val playlist = getFilteredPlaylist()
                preparePlaylist(playlist)

                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private fun observeMediaControllerState(controller: MediaController) {
        controller.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                _repeatMode.value = repeatMode
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                _shuffleModeEnabled.value = shuffleModeEnabled
            }
        })
    }

    fun preparePlaylist(playlist: List<SongDTO>) {
        viewModelScope.launch {
            mediaControllerManager.preparePlaylist(_mediaController, playlist)
            _currentPlaylist.value = playlist
        }
    }

    suspend fun getFilteredPlaylist(
        includeTags: List<TagDTO> = listOf(),
        excludeTags: List<TagDTO> = listOf()
    ): List<SongDTO> {
        songRepository.isInitialized.first { it }
        return songRepository.filterSongList(includeTags, excludeTags)
    }

    fun addIncludedTag(tag: TagDTO) {
        _includedTags.value += tag
    }

    fun removeIncludedTag(tag: TagDTO) {
        _includedTags.value -= tag
    }

    fun addExcludedTag(tag: TagDTO) {
        _excludedTags.value += tag
    }

    fun removeExcludedTag(tag: TagDTO) {
        _excludedTags.value -= tag
    }
}

