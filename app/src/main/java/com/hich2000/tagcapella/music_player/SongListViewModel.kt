package com.hich2000.tagcapella.music_player

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.io.path.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension

class SongListViewModel(application: Application) : AndroidViewModel(application) {

    private var _songList = mutableStateListOf<MediaItem>()
    val songList: SnapshotStateList<MediaItem> get() = _songList

    // State to indicate if initializing has completed
    private val _isInitialized = mutableStateOf(false)
    val isInitialized: State<Boolean> get() = _isInitialized

    suspend fun initializeSongList() {
        viewModelScope.launch {
            val initialPlaylist = getInitialPlaylist()

            _songList.clear()
            _songList.addAll(initialPlaylist)
            _isInitialized.value = true
        }
    }

    // Suspend function to fetch the playlist from the directory asynchronously
    private suspend fun getInitialPlaylist(): List<MediaItem> {
        // Perform file IO operations on a background thread using withContext(Dispatchers.IO)
        return withContext(Dispatchers.IO) {
            val songList = mutableListOf<MediaItem>()
            val path = Path("/storage/emulated/0/Music")

            // List directory entries asynchronously and add valid music files
            try {
                path.listDirectoryEntries().forEach {
                    if (it.isRegularFile() && !it.isDirectory()) {
                        // Create a MediaItem for each file
                        val mediaItem = MediaItem.Builder()
                            .setMediaId(it.toString())
                            .setUri(it.toString())
                            .setMediaMetadata(
                                MediaMetadata.Builder()
                                    .setTitle(it.nameWithoutExtension)
                                    .setDisplayTitle(it.nameWithoutExtension)
                                    .build()
                            )
                            .build()

                        // Add the media item to the list
                        songList.add(mediaItem)
                    }
                }
            } catch (e: Exception) {
                // Handle any potential exceptions here (e.g., permission issues, invalid paths)
                e.printStackTrace()
            }
            return@withContext songList  // Return the list of songs
        }
    }
}