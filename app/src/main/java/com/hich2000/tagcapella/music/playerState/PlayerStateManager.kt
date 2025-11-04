package com.hich2000.tagcapella.music.playerState

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.hich2000.tagcapella.utils.sharedPreferences.SharedPreferenceKey
import com.hich2000.tagcapella.utils.sharedPreferences.SharedPreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerStateManager @Inject constructor(
    private val sharedPreferenceManager: SharedPreferenceManager
) {
    private var _playerState: MutableStateFlow<PlayerState> =
        MutableStateFlow(PlayerState.emptyPlayerState())
    val playerState: StateFlow<PlayerState> get() = _playerState
    var _initialStartup: Boolean = true

    fun initPlayerState() {
        val repeatMode: Int = sharedPreferenceManager.getPreference(
            SharedPreferenceKey.PlayerRepeatMode,
            Player.REPEAT_MODE_ALL
        )

        val shuffleMode: Boolean = sharedPreferenceManager.getPreference(
            SharedPreferenceKey.PlayerShuffleMode,
            false
        )

        val playbackPosition: Long = sharedPreferenceManager.getPreference(
            SharedPreferenceKey.LastSongPosition,
            0L
        )

        val playbackDuration: Long = sharedPreferenceManager.getPreference(
            SharedPreferenceKey.LastSongDuration,
            0L
        )

        val lastSongPlayed: String = sharedPreferenceManager.getPreference(
            SharedPreferenceKey.LastSongPlayed,
            ""
        )

        _playerState.value = PlayerState(
            currentSong = lastSongPlayed,
            isPlaying = false,
            shuffleModeEnabled = shuffleMode,
            repeatMode = repeatMode,
            position = playbackPosition,
            duration = playbackDuration
        )
    }

    fun attachListener(
        mediaController: MediaController?
    ) {
        mediaController?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                val controller = mediaController
                if (controller.playbackState != Player.STATE_READY) return
                _playerState.value = _playerState.value.copy(
                    currentSong = mediaController.currentMediaItem?.mediaId ?: "",
                    position = mediaController.currentPosition,
                    duration = mediaController.duration,
                    isPlaying = isPlaying
                )
                savePlayerState()
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                _playerState.value = _playerState.value.copy(
                    repeatMode = repeatMode
                )
                savePlayerState()
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                _playerState.value = _playerState.value.copy(
                    shuffleModeEnabled = shuffleModeEnabled
                )
                savePlayerState()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    updateTimeline(
                        position = playerState.value.position,
                        duration = mediaController.duration
                    )
                }
                savePlayerState()
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                if (_initialStartup) {
                    //prevent the app from overwriting the playing song on first startup.
                    _initialStartup = false
                    return
                }
                if (mediaItem != null) {
                    _playerState.value = _playerState.value.copy(
                        currentSong = mediaItem.mediaId
                    )
                    savePlayerState()
                }
            }
        })
    }

    fun updateTimeline(position: Long, duration: Long) {
        _playerState.value = _playerState.value.copy(
            position = position,
            duration = duration
        )
    }

    fun savePlayerState() {
        sharedPreferenceManager.savePreference(
            SharedPreferenceKey.LastSongPlayed,
            _playerState.value.currentSong
        )
        sharedPreferenceManager.savePreference(
            SharedPreferenceKey.PlayerRepeatMode,
            _playerState.value.repeatMode
        )
        sharedPreferenceManager.savePreference(
            SharedPreferenceKey.PlayerShuffleMode,
            _playerState.value.shuffleModeEnabled
        )
        sharedPreferenceManager.savePreference(
            SharedPreferenceKey.LastSongPosition,
            _playerState.value.position
        )
        sharedPreferenceManager.savePreference(
            SharedPreferenceKey.LastSongDuration,
            _playerState.value.duration
        )
    }
}