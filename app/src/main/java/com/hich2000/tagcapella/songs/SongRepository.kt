package com.hich2000.tagcapella.songs

import android.content.Context
import android.provider.MediaStore
import com.hich2000.tagcapella.tags.TagDTO
import com.hich2000.tagcapella.utils.Database
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SongRepository @Inject constructor(
    private val folderScanManager: FolderScanManager,
    private val database: Database,
    @ApplicationContext val context: Context
) {

    private val db = database.db

    // Define a CoroutineScope for the repository
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // List of all songs in the scanned directory
    private val _songList = MutableStateFlow<List<Song>>(emptyList())
    val songList: StateFlow<List<Song>> get() = _songList

    // State to indicate if initializing has completed
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> get() = _isInitialized

    init {
        repositoryScope.launch {
            triggerScan()
        }
    }

    fun setSongList(songList: List<Song>) {
        _songList.value = songList
    }

    suspend fun triggerScan() {
        folderScanManager.isInitialized.first { it }
        val scannedSongs: MutableList<Song> =
            scanMusicFolder(folderScanManager.foldersToScan.value)
        setSongList(scannedSongs)
        _isInitialized.value = true
    }

    // Suspend function to fetch the list of songs from the directory asynchronously
    suspend fun scanMusicFolder(foldersToScan: List<String>): MutableList<Song> {
        // Perform file IO operations on a background thread using withContext(Dispatchers.IO)
        return withContext(Dispatchers.IO) {
            val songList = mutableListOf<Song>()
            val placeholders = foldersToScan.joinToString(", ") { "?" }


            val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.RELATIVE_PATH,
                MediaStore.Audio.Media.DATA,
            )
            val selection =
                "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.RELATIVE_PATH} IN ($placeholders)"
            val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
            val cursor = context.contentResolver.query(
                collection,
                projection,
                selection,
                foldersToScan.toTypedArray(),
                sortOrder
            )


            cursor?.use {
                val pathColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                while (it.moveToNext()) {
                    //todo refactor SongDTO to use content uri instead of this DATA column. Better in the long term
                    val path = it.getString(pathColumn)
                    songList.add(
                        Song(
                            path,
                            database
                        )
                    )
                }
            }

            //return the song list after they have been saved in the database
            return@withContext songList
        }
    }

    fun filterSongList(
        includeTags: List<TagDTO> = listOf(),
        excludeTags: List<TagDTO> = listOf()
    ): List<Song> {
        val filteredSongList = mutableListOf<Song>()
        val includeIds: List<Long> = includeTags.map { it.id }

        //if the include tag list is empty we add the entire songlist otherwise we query only the included tags
        if (includeTags.isEmpty()) {
            filteredSongList.addAll(_songList.value)
        } else {
            filteredSongList.addAll(db.songQueries.filterSongList(includeIds) { _, path ->
                Song(
                    path = path,
                    database = database
                )
            }.executeAsList())
        }

        //now we remove the excluded tags and songs who's path does not exist anymore
        filteredSongList.removeAll { song ->
            song.songTagList.any { it in excludeTags } or !File(song.path).exists()
        }

        return filteredSongList
    }
}