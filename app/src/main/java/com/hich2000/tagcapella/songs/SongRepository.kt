package com.hich2000.tagcapella.songs

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.hich2000.tagcapella.tags.TagDTO
import com.hich2000.tagcapella.utils.Database
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.io.path.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.listDirectoryEntries


@Singleton
class SongRepository @Inject constructor(
    private val database: Database,
    @ApplicationContext val context: Context
) {

    private val db = database.db

    // Define a CoroutineScope for the repository
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // List of all songs in the scanned directory
    private var _songList = mutableStateListOf<Song>()
    val songList: SnapshotStateList<Song> get() = _songList

    // State to indicate if initializing has completed
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> get() = _isInitialized

    init {
        repositoryScope.launch {
            val scannedSongs: MutableList<Song> = scanMusicFolder()
            setSongList(scannedSongs)
            _isInitialized.value = true
        }
    }

    fun setSongList(songList: List<Song>) {
        _songList.clear()
        _songList.addAll(songList)
    }

    // Suspend function to fetch the list of songs from the directory asynchronously
    suspend fun scanMusicFolder(): MutableList<Song> {
        // Perform file IO operations on a background thread using withContext(Dispatchers.IO)
        return withContext(Dispatchers.IO) {
            val songList = mutableListOf<Song>()
            val path = Path("/storage/emulated/0/Music")

            // List directory entries asynchronously and add valid music files
            try {
                path.listDirectoryEntries().forEach {
                    if (it.isRegularFile() && !it.isDirectory()) {
                        // Add the song to the list
                        songList.add(
                            Song(
                                it.toString(),
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
        //maybe something for the future?
        //Look into Storage Access Framework and DocumentFile api from androidx dependency.
        //Does not seem possible to get a real path, android requires you to use SAF for this type of shit.
//        registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { test ->
//            if (test != null) {
//                application.contentResolver.takePersistableUriPermission(test, Intent.FLAG_GRANT_READ_URI_PERMISSION)
//
//                val doc = DocumentFile.fromTreeUri(applicationContext, test)
//                if (doc != null) {
//                    println("here2:")
//                    println(application.contentResolver.persistedUriPermissions)
////                    println("here:")
////                    println(application.contentResolver.releasePersistableUriPermission(test, Intent.FLAG_GRANT_READ_URI_PERMISSION))
//                }
//            }
//        }.launch(null)


        //this is how stuff works with mediastore
//        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//        val projection = arrayOf(
//            MediaStore.Audio.Media._ID,
//            MediaStore.Audio.Media.TITLE,
//            MediaStore.Audio.Media.ARTIST,
//            MediaStore.Audio.Media.DATA,
//        )
//        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
//        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
//        val cursor = context.contentResolver.query(
//            collection,
//            projection,
//            selection,
//            null,
//            sortOrder
//        )
//
//        cursor?.use {
////                val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
////                val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
////                val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
//            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
//
//            println("start:")
//            while (it.moveToNext()) {
//                val path = it.getString(dataColumn)
//
//                songList.add(
//                    Song(
//                        path,
//                        database
//                    )
//                )
//            }
//        }
    }

    fun filterSongList(
        includeTags: List<TagDTO> = listOf(),
        excludeTags: List<TagDTO> = listOf()
    ): List<Song> {
        val filteredSongList = mutableListOf<Song>()
        val includeIds: List<Long> = includeTags.map { it.id }

        //if the include tag list is empty we add the entire songlist otherwise we query only the included tags
        if (includeTags.isEmpty()) {
            filteredSongList.addAll(_songList)
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