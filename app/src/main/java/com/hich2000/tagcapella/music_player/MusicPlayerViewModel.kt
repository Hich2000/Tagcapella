package com.hich2000.tagcapella.music_player

import android.app.Application
import android.content.ComponentName
import androidx.concurrent.futures.await
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
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
    private val application: Application,
    val songRepository: SongRepository
) : ViewModel() {

    private lateinit var _mediaController: MediaController
    val mediaController: MediaController get() = _mediaController

    // State management
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> get() = _isPlaying.asStateFlow()

    private val _shuffleModeEnabled = MutableStateFlow(false)
    val shuffleModeEnabled: StateFlow<Boolean> get() = _shuffleModeEnabled.asStateFlow()

    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_ALL)
    val repeatMode: StateFlow<Int> get() = _repeatMode.asStateFlow()

    private val _isMediaControllerInitialized = MutableStateFlow(false)
    val isMediaControllerInitialized: StateFlow<Boolean> get() = _isMediaControllerInitialized.asStateFlow()

    private var _includedTags = MutableStateFlow<List<TagDTO>>(emptyList())
    val includedTags: StateFlow<List<TagDTO>> get() = _includedTags.asStateFlow()

    private val _excludedTags = MutableStateFlow<List<TagDTO>>(emptyList())
    val excludedTags: StateFlow<List<TagDTO>> get() = _excludedTags.asStateFlow()

    private val _currentPlaylist = MutableStateFlow<List<SongDTO>>(emptyList())
    val currentPlaylist: StateFlow<List<SongDTO>> get() = _currentPlaylist.asStateFlow()

    fun initializeMediaController() {
        viewModelScope.launch {
            try {
                val sessionToken = SessionToken(application, ComponentName(application, PlaybackService::class.java))
                val controller = MediaController.Builder(application, sessionToken).buildAsync().await()
                setupMediaController(controller)
                _mediaController = controller

                val playlist = getFilteredPlaylist()
                preparePlaylist(playlist)

                _isMediaControllerInitialized.value = true
            } catch (e: Exception) {
                handleMediaControllerError(e)
            }
        }
    }

    private fun setupMediaController(controller: MediaController) {
        controller.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                _shuffleModeEnabled.value = shuffleModeEnabled
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                _repeatMode.value = repeatMode
            }
        })
        controller.repeatMode = Player.REPEAT_MODE_ALL
    }

    fun preparePlaylist(playlist: List<SongDTO>) {
        val mediaItems = mutableListOf<MediaItem>()

        playlist.listIterator().forEach {
            val mediaItem = MediaItem.Builder()
                .setMediaId(it.path)
                .setUri(it.path)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(it.title)
                        .setDisplayTitle(it.title)
                        .build()
                )
                .build()

            mediaItems.add(mediaItem)
        }

        _mediaController.clearMediaItems()
        _mediaController.addMediaItems(mediaItems)
        _mediaController.prepare()

        _currentPlaylist.value = playlist
    }

    suspend fun getFilteredPlaylist(
        includeTags: List<TagDTO> = listOf(),
        excludeTags: List<TagDTO> = listOf()
    ): List<SongDTO> {
        songRepository.isInitialized.first { it }
        return songRepository.filterSongList(includeTags, excludeTags)
    }

    private fun handleMediaControllerError(exception: Exception) {
        // Log or emit error state
        exception.printStackTrace()
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

