package com.hich2000.tagcapella.music.mediaController

import androidx.media3.common.Player
import com.hich2000.tagcapella.utils.sharedPreferences.SharedPreferenceKey
import com.hich2000.tagcapella.utils.sharedPreferences.SharedPreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerStateManager @Inject constructor(
    private val sharedPreferenceManager: SharedPreferenceManager
) {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private var _playerState: MutableStateFlow<PlayerState> =
        MutableStateFlow(PlayerState.emptyPlayerState())
    val playerState: StateFlow<PlayerState> get() = _playerState

    private val _playbackPosition = MutableStateFlow(0L)
    val playbackPosition: StateFlow<Long> = _playbackPosition


    init {
        coroutineScope.launch {
            initPlayerState()
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
        onRepeatModeChangedCallback: ((Int) -> Unit)? = null,
        onShuffleModeChangedCallback: ((Boolean) -> Unit)? = null,
        onPlaybackStateChangeCallback: (() -> Unit)? = null
    ): Player.Listener {
        return object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playerState.value = _playerState.value.copy(
                    isPlaying = isPlaying
                )
                onPlaybackStateChangeCallback?.invoke()
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                _playerState.value = _playerState.value.copy(
                    repeatMode = repeatMode
                )
                onRepeatModeChangedCallback?.invoke(repeatMode)
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                _playerState.value = _playerState.value.copy(
                    shuffleModeEnabled = shuffleModeEnabled
                )
                onShuffleModeChangedCallback?.invoke(shuffleModeEnabled)
            }
        }
    }

    fun updatePosition(position: Long, duration: Long) {
        _playerState.value = _playerState.value.copy(position = position, duration = duration)
    }
}