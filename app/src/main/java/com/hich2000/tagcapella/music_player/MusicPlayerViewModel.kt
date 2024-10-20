package com.hich2000.tagcapella.music_player

import android.app.Application
import android.content.ComponentName
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutionException

class MusicPlayerViewModel(application: Application) : AndroidViewModel(application) {

    // Hold MediaController in a mutable state
    private lateinit var _mediaController: MediaController
    val mediaController: MediaController
        get() = _mediaController


    // State to indicate if the shuffle mode is enabled
    private val _shuffleModeEnabled = mutableStateOf(false)
    val shuffleModeEnabled: State<Boolean> get() = _shuffleModeEnabled

    // State to indicate which loop mode is enabled
    private val _repeatMode = mutableIntStateOf(Player.REPEAT_MODE_ALL)
    val repeatMode: State<Int> get() = _repeatMode

    // State to indicate if the music is playing
    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> get() = _isPlaying

    // State to indicate if the MediaController is initialized
    private val _isMediaControllerInitialized = mutableStateOf(false)
    val isMediaControllerInitialized: State<Boolean> get() = _isMediaControllerInitialized

    fun initializeMediaController() {
        viewModelScope.launch {
            val sessionToken =
                SessionToken(
                    getApplication(),
                    ComponentName(getApplication(), PlaybackService::class.java)
                )
            val controllerFuture =
                MediaController.Builder(getApplication(), sessionToken).buildAsync()
            controllerFuture.addListener(
                {
                    try {
                        _mediaController = controllerFuture.get()

                        // Listen for playback state changes
                        _mediaController.addListener(object : Player.Listener {
                            override fun onIsPlayingChanged(isPlaying: Boolean) {
                                _isPlaying.value = isPlaying
                            }

                            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                                _shuffleModeEnabled.value = shuffleModeEnabled
                            }

                            override fun onRepeatModeChanged(repeatMode: Int) {
                                _repeatMode.intValue = _mediaController.repeatMode
                            }
                        })

                        _mediaController.repeatMode = Player.REPEAT_MODE_ALL
                        _mediaController.addMediaItems(getInitialPlaylist())
                        _mediaController.prepare()
                        _isMediaControllerInitialized.value = true // Update the loading state
                    } catch (e: ExecutionException) {
                        e.printStackTrace()
                    }
                },
                MoreExecutors.directExecutor()
            )
        }
    }

    private fun getInitialPlaylist(): MutableList<MediaItem> {
        val playlist = mutableListOf<MediaItem>()

//        val path = Path("/storage/emulated/0/Music").listDirectoryEntries()
//        path.listIterator().forEach {
//            if (!it.isDirectory() && it.isRegularFile()) {
//
//                val mediaItem = MediaItem.Builder()
//                    .setMediaId(it.toString())
//                    .setUri(it.toString())
//                    .setMediaMetadata(
//                        MediaMetadata.Builder()
//                            .setTitle(it.nameWithoutExtension)
//                            .setDisplayTitle(it.nameWithoutExtension)
//                            .build()
//                    )
//                    .build()
//
//                playlist.add(mediaItem)
//            }
//        }
        return playlist
    }
}
