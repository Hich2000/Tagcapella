package com.hich2000.tagcapella.tags

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.hich2000.tagcapella.utils.Database
import com.hich2000.tagcapella.songs.Song
import javax.inject.Inject

data class TagDTO @Inject constructor(val id: Long, val tag: String, val database: Database) {

    private var db = database.db

    private var _taggedSongList = mutableStateListOf<Song>()
    val taggedSongList: SnapshotStateList<Song>
        get() {
            if (_taggedSongList.isEmpty()) reloadSongList()
            return _taggedSongList
        }

    private var _taggedSongCount = mutableIntStateOf(getSongCount())
    val taggedSongCount: State<Int>
        get() {
            if (_taggedSongList.isEmpty()) reloadSongList()
            return _taggedSongCount
        }

    fun reloadSongList() {
        _taggedSongList.clear()
        _taggedSongList.addAll(getTaggedSongs())
        _taggedSongCount.intValue = getSongCount()
    }

    private fun getTaggedSongs(): MutableList<Song> {
        val songs = db.tagQueries.selectTaggedSongs(id) { _, path  ->
            Song(path, database)
        }.executeAsList()
        return songs.toMutableStateList()
    }

    private fun getSongCount(): Int {
        return _taggedSongList.size
    }
}

class TagDTOFactory @Inject constructor(private val database: Database) {

    fun getTagById(tagId: Long): TagDTO? {
        val db = database.db
        val tagResult = db.tagQueries.selectTagById(tagId) { id, tag ->
            TagDTO(id, tag, database)
        }.executeAsOneOrNull()

        return tagResult
    }
}