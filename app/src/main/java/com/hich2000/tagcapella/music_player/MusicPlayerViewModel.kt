package com.hich2000.tagcapella.music_player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.hich2000.tagcapella.tags.TagDTO
import com.hich2000.tagcapella.tags.TagDTOFactory
import com.hich2000.tagcapella.utils.SharedPreferenceKeys
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
    private val sharedPreferenceManager: SharedPreferenceManager
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

    private val _playbackPosition = MutableStateFlow(0L)
    val playbackPosition: StateFlow<Long> get() = _playbackPosition

    private val _playbackDuration = MutableStateFlow(0L)
    val playbackDuration: StateFlow<Long> get() = _playbackDuration

    @Inject
    lateinit var tagDTOFactory: TagDTOFactory

    init {
        viewModelScope.launch {
            _isMediaControllerInitialized.value = try {
                val controller = mediaControllerManager.initializeMediaController()
                observeMediaControllerState(controller)
                _mediaController = controller


                val repeatMode: Int = sharedPreferenceManager.getPreference(
                    SharedPreferenceKeys.PLAYER_REPEAT_MODE,
                    Player.REPEAT_MODE_ALL
                )
                _repeatMode.value = repeatMode
                _mediaController.repeatMode = repeatMode

                val shuffleMode: Boolean = sharedPreferenceManager.getPreference(
                    SharedPreferenceKeys.PLAYER_SHUFFLE_MODE,
                    false
                )
                _shuffleModeEnabled.value = shuffleMode
                _mediaController.shuffleModeEnabled = shuffleMode


                _isPlaying.value = _mediaController.isPlaying


                //todo use shared preferences to remember settings, which song was playing and how far it was.
                //  I think on pausing it should save like the currently playing song and progress for when the service has ended.
                //  also use shared preferences to save the current tags and playlist.
                if (!_mediaController.isPlaying) {

                    //get included and excluded tag ids
                    val includedTagIds: List<String> = sharedPreferenceManager.getPreference(SharedPreferenceKeys.INCLUDED_TAGS, listOf())
                    val excludedTagIds: List<String> = sharedPreferenceManager.getPreference(SharedPreferenceKeys.EXCLUDED_TAGS, listOf())

                    //use the list of ids to make a list of DTOs
                    _includedTags.value = includedTagIds.map { tagDTOFactory.getTagById(it.toLong())!! }
                    _excludedTags.value = excludedTagIds.map { tagDTOFactory.getTagById(it.toLong())!! }

                    val playlist = getFilteredPlaylist(_includedTags.value, _excludedTags.value)
                    preparePlaylist(playlist)
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
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                _repeatMode.value = repeatMode
                sharedPreferenceManager.savePreference(
                    SharedPreferenceKeys.PLAYER_REPEAT_MODE,
                    repeatMode
                )
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                _shuffleModeEnabled.value = shuffleModeEnabled
                sharedPreferenceManager.savePreference(
                    SharedPreferenceKeys.PLAYER_SHUFFLE_MODE,
                    shuffleModeEnabled
                )
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

        val jsonIncluded: List<Long> = includeTags.map {it.id}
        val jsonExcluded: List<Long> = excludeTags.map {it.id}
        sharedPreferenceManager.savePreference(SharedPreferenceKeys.INCLUDED_TAGS, jsonIncluded)
        sharedPreferenceManager.savePreference(SharedPreferenceKeys.EXCLUDED_TAGS, jsonExcluded)

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

}

