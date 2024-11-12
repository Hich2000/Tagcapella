package com.hich2000.tagcapella.music_player

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.hich2000.tagcapella.Database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.io.path.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension

data class Song(val path: String, val title: String)

@Singleton
class SongRepository @Inject constructor(
    database: Database
) {

    private val db = database.db

    // Define a CoroutineScope for the repository
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var _songList = mutableStateListOf<Song>()
    val songList: SnapshotStateList<Song> get() = _songList

    // State to indicate if initializing has completed
    private val _isInitialized = mutableStateOf(false)
    val isInitialized: State<Boolean> get() = _isInitialized

    init {
        repositoryScope.launch {
            setSongList(scanMusicFolder())
            _isInitialized.value = true
        }
    }

    fun setSongList(songList: List<Song>) {
        _songList.clear()
        _songList.addAll(songList)
    }

    // Suspend function to fetch the list of songs from the directory asynchronously
    suspend fun scanMusicFolder(): List<Song> {
        // Perform file IO operations on a background thread using withContext(Dispatchers.IO)
        return withContext(Dispatchers.IO) {
            val songList = mutableListOf<Song>()
            val path = Path("/storage/emulated/0/Music")

            // List directory entries asynchronously and add valid music files
            try {
                path.listDirectoryEntries().forEach {
                    if (it.isRegularFile() && !it.isDirectory()) {
                        // Add the song to the list
                        songList.add(Song(it.toString(), it.nameWithoutExtension))
                    }
                }
            } catch (e: Exception) {
                // Handle any potential exceptions here (e.g., permission issues, invalid paths)
                e.printStackTrace()
            }

            //return the song list after they have been saved in the database
            return@withContext saveSongList(songList)
        }
    }

    private suspend fun saveSongList(songList: MutableList<Song>): MutableList<Song> {
        return withContext(Dispatchers.IO) {
            val toRemove: MutableList<Int> = mutableListOf()

            songList.forEachIndexed { index, song ->
                try {
                    if (!songRecordExists(song)) {
                        db.songQueries.insertSong(song.path, song.title)
                    }
                } catch (e: Exception) {
                    toRemove.add(index)
                }
            }

            toRemove.forEach {
                songList.removeAt(it)
            }

            return@withContext songList
        }
    }

    private fun songRecordExists(song: Song): Boolean {
        return db.songQueries.selectSong(song.path).executeAsOneOrNull() !== null
    }
}