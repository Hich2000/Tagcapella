package com.hich2000.tagcapella.music.mediaController

import android.app.Application
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import androidx.concurrent.futures.await
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.hich2000.tagcapella.newmusic.mediaController.PlaybackService
import com.hich2000.tagcapella.newmusic.Song
import com.hich2000.tagcapella.newmusic.SongRepository
import com.hich2000.tagcapella.newmusic.mediaController.PlayerState
import com.hich2000.tagcapella.newmusic.mediaController.PlayerStateManager
import com.hich2000.tagcapella.tagsAndCategories.tags.TagDTO
import com.hich2000.tagcapella.tagsAndCategories.tags.TagRepository
import com.hich2000.tagcapella.utils.sharedPreferences.SharedPreferenceKey
import com.hich2000.tagcapella.utils.sharedPreferences.SharedPreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaControllerManager @Inject constructor(
    private val application: Application,
    private val sharedPreferenceManager: SharedPreferenceManager,
    private val tagRepository: TagRepository,
    private val songRepository: SongRepository,
    private val playerStateManager: PlayerStateManager
) {

    //todo figure out how scopes work and rework this away from main thread
    private val repositoryScope = CoroutineScope(Dispatchers.Main)

    private val _mediaController: MutableStateFlow<MediaController?> =
        MutableStateFlow(null)
    val mediaController: StateFlow<MediaController?> get() = _mediaController

    // State management
    val playerState: StateFlow<PlayerState> get() = playerStateManager.playerState

    private val _isMediaControllerInitialized = MutableStateFlow(false)
    val isMediaControllerInitialized: StateFlow<Boolean> get() = _isMediaControllerInitialized

    private val _currentPlaylist = MutableStateFlow<List<Song>>(emptyList())
    val currentPlaylist: StateFlow<List<Song>> get() = _currentPlaylist

    private var _includedTags = MutableStateFlow<List<TagDTO>>(emptyList())
    val includedTags: StateFlow<List<TagDTO>> get() = _includedTags

    private val _excludedTags = MutableStateFlow<List<TagDTO>>(emptyList())
    val excludedTags: StateFlow<List<TagDTO>> get() = _excludedTags

    private lateinit var audioOutputChangeReceiver: BroadcastReceiver

    init {
        repositoryScope.launch {
            initializeMediaController()
            initPlayerState()

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
            _includedTags.value = includedTagIds.map { tagRepository.getTagById(it)!! }
            _excludedTags.value = excludedTagIds.map { tagRepository.getTagById(it)!! }
            val playlist = getFilteredPlaylist(_includedTags.value, _excludedTags.value)
            _currentPlaylist.value = playlist


            _mediaController.value?.isPlaying?.let {
                if (!it) {
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

                        _mediaController.value?.seekTo(seekToIndex, playbackPosition)
                    }
                }
            }

            // Register the receiver to detect changes in audio output
            audioOutputChangeReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
                        mediaController.value?.pause()
                    }
                }
            }
            // Register the receiver
            val filter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
            application.registerReceiver(audioOutputChangeReceiver, filter)
        }
    }

    suspend fun initializeMediaController() {

        // Reuse existing MediaController if it's already connected
        if (mediaController.value != null) {
            _isMediaControllerInitialized.value = true
            return
        }

        _isMediaControllerInitialized.value = try {
            val sessionToken =
                SessionToken(application, ComponentName(application, PlaybackService::class.java))
            val controllerFuture = MediaController.Builder(application, sessionToken).buildAsync()
            _mediaController.value = controllerFuture.await()


            val audioAttributes: AudioAttributes = AudioAttributes.Builder()
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build()

            _mediaController.value?.setAudioAttributes(audioAttributes, true)

            _mediaController.value?.let { observeMediaControllerState() }

            true
        } catch (_: Throwable) {
            false
        }
    }

    private fun initPlayerState() {
        val repeatMode: Int = sharedPreferenceManager.getPreference(
            SharedPreferenceKey.PlayerRepeatMode,
            Player.REPEAT_MODE_ALL
        )

        val shuffleMode: Boolean = sharedPreferenceManager.getPreference(
            SharedPreferenceKey.PlayerShuffleMode,
            false
        )

        _mediaController.value?.repeatMode = repeatMode
        _mediaController.value?.shuffleModeEnabled = shuffleMode
    }

    private fun observeMediaControllerState() {
        _mediaController.value?.addListener(
            playerStateManager.createListener(
                onRepeatModeChangedCallback = { mode ->
                    sharedPreferenceManager.savePreference(
                        SharedPreferenceKey.PlayerRepeatMode,
                        mode
                    )
                },
                onShuffleModeChangedCallback = { enabled ->
                    sharedPreferenceManager.savePreference(
                        SharedPreferenceKey.PlayerShuffleMode,
                        enabled
                    )
                },
                onPlaybackStateChangeCallback = {
                    // Save current song + position
                    _mediaController.value?.currentMediaItem?.let {
                        sharedPreferenceManager.savePreference(
                            SharedPreferenceKey.LastSongPlayed,
                            it.mediaId
                        )
                        sharedPreferenceManager.savePreference(
                            SharedPreferenceKey.LastSongPosition,
                            playerStateManager.playbackPosition.value
                        )
                        sharedPreferenceManager.savePreference(
                            SharedPreferenceKey.LastSongDuration,
                            playerState.value.duration
                        )
                    }
                }
            )
        )

        //get the duration and position of the current song every second
        repositoryScope.launch {
            while (true) {
                val controller = _mediaController.value ?: continue
                playerStateManager.updatePosition(
                    controller.currentPosition,
                    controller.duration
                )
                delay(1000)
            }
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

    fun preparePlaylist(playlist: List<Song>) {
        val mediaItems = playlist.map {
            MediaItem.Builder()
                .setMediaId(it.path)
                .setUri(it.path)
                .setMediaMetadata(
                    MediaMetadata.Builder().setTitle(File(it.path).nameWithoutExtension).build()
                )
                .build()
        }
        _mediaController.value?.clearMediaItems()
        _mediaController.value?.addMediaItems(mediaItems)
        _mediaController.value?.prepare()
        _currentPlaylist.value = playlist
    }

    fun togglePlayback() {
        if (playerState.value.isPlaying) {
            _mediaController.value?.pause()
        } else {
            _mediaController.value?.play()
        }
    }

    fun cleanUpResources() {
        // Unregister the receiver when singleton is destroyed to avoid memory leaks
        try {
            application.unregisterReceiver(audioOutputChangeReceiver)
        } catch (_: IllegalArgumentException) {
            // Receiver might have already been unregistered
        }
    }

    fun setPlaybackPosition(position: Number) {
        _mediaController.value?.seekTo(position.toLong())
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