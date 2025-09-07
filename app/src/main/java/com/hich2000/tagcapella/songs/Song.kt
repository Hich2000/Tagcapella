package com.hich2000.tagcapella.songs

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.hich2000.tagcapella.utils.Database
import com.hich2000.tagcapella.tags.TagDTO
import java.io.File

data class Song(val path: String, val database: Database) {

    private var db = database.db

    private var _songTagList = mutableStateListOf<TagDTO>()
    val songTagList: SnapshotStateList<TagDTO>
        get() {
            if (_songTagList.isEmpty()) reloadTagList()
            return _songTagList
        }

    private var _songTagCount = mutableIntStateOf(getSongTagCount())
    val songTagCount: State<Int>
        get() {
            reloadTagList()
            return _songTagCount
        }

    val title: String
        get() = File(path).nameWithoutExtension


    fun reloadTagList() {
        _songTagList.clear()
        _songTagList.addAll(getSongTags())
        _songTagCount.intValue = getSongTagCount()
    }

    private fun getSongTags(): MutableList<TagDTO> {
        val tags = db.songQueries.selectSongTags(path) { id, tag, category ->
                TagDTO(id, tag, category, database)
            }.executeAsList()

        return tags.toMutableStateList()
    }

    private fun getSongTagCount(): Int {
        return _songTagList.size
    }
}