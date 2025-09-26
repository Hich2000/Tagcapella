package com.hich2000.tagcapella.music_player

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.hich2000.tagcapella.songs.Song
import com.hich2000.tagcapella.songs.SongRepository
import com.hich2000.tagcapella.tags.TagDTO
import com.hich2000.tagcapella.tags.TagDTOFactory
import com.hich2000.tagcapella.utils.SharedPreferenceKey
import com.hich2000.tagcapella.utils.SharedPreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicPlayerViewModel @Inject constructor(
    private val mediaControllerManager: MediaControllerManager,
    private val songRepository: SongRepository,
    private val sharedPreferenceManager: SharedPreferenceManager,
    private val tagDTOFactory: TagDTOFactory,
    private val application: Application
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

    private val _currentPlaylist = MutableStateFlow<List<Song>>(emptyList())
    val currentPlaylist: StateFlow<List<Song>> get() = _currentPlaylist.asStateFlow()

    private val _playbackPosition = MutableStateFlow(0L)
    val playbackPosition: StateFlow<Long> get() = _playbackPosition

    private val _playbackDuration = MutableStateFlow(0L)
    val playbackDuration: StateFlow<Long> get() = _playbackDuration

    private lateinit var audioOutputChangeReceiver: BroadcastReceiver

    init {
        var test = sharedPreferenceManager.getPreference(
            SharedPreferenceKey.PlayerRepeatMode,
            Player.REPEAT_MODE_ALL
        )
        println(test)

        viewModelScope.launch {
            // Register the receiver to detect changes in audio output
            audioOutputChangeReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
                        stopPlayback()
                    }
                }
            }
            // Register the receiver
            val filter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
            application.registerReceiver(audioOutputChangeReceiver, filter)

            _isMediaControllerInitialized.value = try {
                val controller = mediaControllerManager.initializeMediaController()
                observeMediaControllerState(controller)
                _mediaController = controller

                val repeatMode: Int = sharedPreferenceManager.getPreference(
                    SharedPreferenceKey.PlayerRepeatMode,
                    Player.REPEAT_MODE_ALL
                )
                _repeatMode.value = repeatMode
                _mediaController.repeatMode = repeatMode

                val shuffleMode: Boolean = sharedPreferenceManager.getPreference(
                    SharedPreferenceKey.PlayerShuffleMode,
                    false
                )
                _shuffleModeEnabled.value = shuffleMode
                _mediaController.shuffleModeEnabled = shuffleMode

                _isPlaying.value = _mediaController.isPlaying

                //get included and excluded tag ids
                val includedTagIds: List<Long> = sharedPreferenceManager.getPreference(
                    SharedPreferenceKey.IncludedTags,
                    listOf()
                )
                val excludedTagIds: List<Long> = sharedPreferenceManager.getPreference(
                    SharedPreferenceKey.ExcludedTags,
                    listOf()
                )

                //use the list of ids to make a list of DTOs
                _includedTags.value = includedTagIds.map { tagDTOFactory.getTagById(it)!! }
                _excludedTags.value = excludedTagIds.map { tagDTOFactory.getTagById(it)!! }
                val playlist = getFilteredPlaylist(_includedTags.value, _excludedTags.value)
                _currentPlaylist.value = playlist

                if (!_mediaController.isPlaying) {
                    preparePlaylist(playlist)

                    val lastSongPlayed: String = sharedPreferenceManager.getPreference(
                        SharedPreferenceKey.LastSongPlayed,
                        ""
                    )
                    val playbackPosition: Long = sharedPreferenceManager.getPreference(
                        SharedPreferenceKey.LastSongPosition,
                        0L
                    )

                    if (lastSongPlayed.isNotEmpty()) {
                        val seekToIndex = _currentPlaylist.value.indexOfFirst { song ->
                            song.path == lastSongPlayed
                        }

                        _mediaController.seekTo(seekToIndex, playbackPosition)
                    }
                }

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

                _mediaController.currentMediaItem?.let {
                    sharedPreferenceManager.savePreference(
                        SharedPreferenceKey.LastSongPlayed,
                        it.mediaId
                    )
                    sharedPreferenceManager.savePreference(
                        SharedPreferenceKey.LastSongPosition,
                        _playbackPosition.value
                    )
                }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                _repeatMode.value = repeatMode
                sharedPreferenceManager.savePreference(
                    SharedPreferenceKey.PlayerRepeatMode,
                    repeatMode
                )
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                _shuffleModeEnabled.value = shuffleModeEnabled
                sharedPreferenceManager.savePreference(
                    SharedPreferenceKey.PlayerShuffleMode,
                    shuffleModeEnabled
                )
            }


            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                _mediaController.currentMediaItem?.let {
                    sharedPreferenceManager.savePreference(
                        SharedPreferenceKey.LastSongPlayed,
                        it.mediaId
                    )
                    sharedPreferenceManager.savePreference(
                        SharedPreferenceKey.LastSongPosition,
                        _playbackPosition.value
                    )
                }
            }
        })

        //get the duration and position of the current song every second
        viewModelScope.launch {
            while (true) {
                _playbackPosition.value = _mediaController.currentPosition
                _playbackDuration.value = _mediaController.duration
                delay(1000)
            }
        }
    }

    fun preparePlaylist(playlist: List<Song>) {
        viewModelScope.launch {
            mediaControllerManager.preparePlaylist(_mediaController, playlist)
            _currentPlaylist.value = playlist
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

    fun setPlaybackPosition(position: Number, finished: Boolean = false) {
        _playbackPosition.value = position.toLong()
        if (finished) {
            _mediaController.seekTo(_playbackPosition.value)
        }
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

    private fun stopPlayback() {
        viewModelScope.launch {
            mediaControllerManager.stopPlayback()
            _isPlaying.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Unregister the receiver when the ViewModel is cleared to avoid memory leaks
        try {
            application.unregisterReceiver(audioOutputChangeReceiver)
        } catch (_: IllegalArgumentException) {
            // Receiver might have already been unregistered
        }
    }

}

