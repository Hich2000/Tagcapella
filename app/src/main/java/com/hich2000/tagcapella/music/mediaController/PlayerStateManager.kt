package com.hich2000.tagcapella.music.mediaController

import androidx.media3.common.Player
import com.hich2000.tagcapella.utils.sharedPreferences.SharedPreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerStateManager @Inject constructor(
    private val sharedPreferenceManager: SharedPreferenceManager
) {

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_ALL)
    val repeatMode: StateFlow<Int> = _repeatMode

    private val _shuffleModeEnabled = MutableStateFlow(false)
    val shuffleModeEnabled: StateFlow<Boolean> = _shuffleModeEnabled

    private val _playbackPosition = MutableStateFlow(0L)
    val playbackPosition: StateFlow<Long> = _playbackPosition

    private val _playbackDuration = MutableStateFlow(0L)
    val playbackDuration: StateFlow<Long> = _playbackDuration

    // Expose a listener you can plug into the MediaController
    fun createListener(
        onRepeatModeChangedCallback: ((Int) -> Unit)? = null,
        onShuffleModeChangedCallback: ((Boolean) -> Unit)? = null,
        onPlaybackStateChangeCallback: (() -> Unit)? = null
    ): Player.Listener {
        return object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
                onPlaybackStateChangeCallback?.invoke()
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                _repeatMode.value = repeatMode
                onRepeatModeChangedCallback?.invoke(repeatMode)
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                _shuffleModeEnabled.value = shuffleModeEnabled
                onShuffleModeChangedCallback?.invoke(shuffleModeEnabled)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                onPlaybackStateChangeCallback?.invoke()
            }
        }
    }

    fun updatePosition(position: Long, duration: Long) {
        _playbackPosition.value = position
        _playbackDuration.value = duration
    }
}