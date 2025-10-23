package com.hich2000.tagcapella.newmusic.mediaController

import androidx.media3.common.Player
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

        _playerState.value = PlayerState(
            isPlaying = false,
            shuffleModeEnabled = shuffleMode,
            repeatMode = repeatMode,
            position = playbackPosition,
            duration = playbackDuration
        )
    }

    // Expose a listener you can plug into the MediaController
    fun createListener(
        onPlaybackStateReady: () -> Unit = {}
    ): Player.Listener {
        return object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playerState.value = _playerState.value.copy(
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
                    onPlaybackStateReady()
                }
                savePlayerState()
            }

        }
    }

    fun updateTimeline(position: Long, duration: Long) {
        _playerState.value = _playerState.value.copy(position = position, duration = duration)
    }

    private fun savePlayerState() {
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