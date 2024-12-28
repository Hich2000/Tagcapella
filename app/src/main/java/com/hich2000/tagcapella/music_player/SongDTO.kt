package com.hich2000.tagcapella.music_player

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.hich2000.tagcapella.Database
import com.hich2000.tagcapella.tags.TagDTO

data class SongDTO(val id: Long?, val path: String, val title: String, val database: Database) {

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
            if (_songTagList.isEmpty()) reloadTagList()
            return _songTagCount
        }


    fun reloadTagList() {
        _songTagList.clear()
        _songTagList.addAll(getSongTags())
        _songTagCount.intValue = getSongTagCount()
    }

    private fun getSongTags(): MutableList<TagDTO> {
        val tags = id?.let {
            db.songQueries.selectSongTags(it) { id, tag ->
                TagDTO(id, tag, database)
            }.executeAsList()
        }

        return tags?.toMutableStateList() ?: mutableListOf()
    }

    private fun getSongTagCount(): Int {
        return _songTagList.size
    }
}