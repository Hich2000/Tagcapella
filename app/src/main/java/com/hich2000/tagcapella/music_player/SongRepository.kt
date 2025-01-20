package com.hich2000.tagcapella.music_player

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.hich2000.tagcapella.utils.Database
import com.hich2000.tagcapella.tags.TagDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.io.path.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension


@Singleton
class SongRepository @Inject constructor(
    private val database: Database
) {

    private val db = database.db

    // Define a CoroutineScope for the repository
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // List of all songs in the scanned directory
    private var _songList = mutableStateListOf<SongDTO>()
    val songList: SnapshotStateList<SongDTO> get() = _songList

    // State to indicate if initializing has completed
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> get() = _isInitialized

    init {
        repositoryScope.launch {
            val scannedSongs: MutableList<SongDTO> = scanMusicFolder()
            saveSongList(scannedSongs)
            setSongList(getSongList())
            _isInitialized.value = true
        }
    }

    fun setSongList(songList: List<SongDTO>) {
        _songList.clear()
        _songList.addAll(songList)
    }

    // Suspend function to fetch the list of songs from the directory asynchronously
    suspend fun scanMusicFolder(): MutableList<SongDTO> {
        // Perform file IO operations on a background thread using withContext(Dispatchers.IO)
        return withContext(Dispatchers.IO) {
            val songList = mutableListOf<SongDTO>()
            val path = Path("/storage/emulated/0/Music")

            // List directory entries asynchronously and add valid music files
            try {
                path.listDirectoryEntries().forEach {
                    if (it.isRegularFile() && !it.isDirectory()) {
                        // Add the song to the list
                        songList.add(
                            SongDTO(
                                null,
                                it.toString(),
                                it.nameWithoutExtension,
                                database
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                // Handle any potential exceptions here (e.g., permission issues, invalid paths)
                e.printStackTrace()
            }

            //return the song list after they have been saved in the database
            return@withContext songList
        }
    }

    suspend fun saveSongList(songList: MutableList<SongDTO>): MutableList<SongDTO> {
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

    fun getSongList(): List<SongDTO> {
        return db.songQueries.selectAll { id, path, title ->
            SongDTO(
                id = id,
                path = path,
                title = title,
                database = database
            )
        }.executeAsList()
    }

    private fun songRecordExists(song: SongDTO): Boolean {
        return db.songQueries.selectSong(song.path).executeAsOneOrNull() !== null
    }

    fun filterSongList(
        includeTags: List<TagDTO> = listOf(),
        excludeTags: List<TagDTO> = listOf()
    ): List<SongDTO> {
        val filteredSongList = mutableListOf<SongDTO>()
        val includeIds: List<Long> = includeTags.map { it.id }

        //if the include tag list is empty we add the entire songlist otherwise we query only the included tags
        if (includeTags.isEmpty()) {
            filteredSongList.addAll(_songList)
        } else {
            filteredSongList.addAll(db.songQueries.filterSongList(includeIds) { id, path, title ->
                SongDTO(
                    id = id,
                    path = path,
                    title = title,
                    database = database
                )
            }.executeAsList())
        }

        //now we remove the excluded tags
        filteredSongList.removeAll { song ->
            song.songTagList.any { it in excludeTags }
        }

        return filteredSongList
    }
}