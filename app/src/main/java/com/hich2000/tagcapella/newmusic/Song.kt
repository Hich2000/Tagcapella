package com.hich2000.tagcapella.newmusic

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.toMutableStateList
import com.hich2000.tagcapella.tagsAndCategories.tags.TagDTO
import com.hich2000.tagcapella.utils.Database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class Song(val path: String, val database: Database) {

    private var db = database.db

    private var _songTagList = MutableStateFlow<List<TagDTO>>(emptyList())
    val songTagList: StateFlow<List<TagDTO>>
        get() {
            if (_songTagList.value.isEmpty()) reloadTagList()
            return _songTagList
        }

    private var _songTagCount = mutableIntStateOf(getSongTagCount())
    val songTagCount: State<Int>
        get() {
            reloadTagList()
            return _songTagCount
        }

    fun reloadTagList() {
        _songTagList.value = emptyList()
        _songTagList.value = getSongTags()
        _songTagCount.intValue = getSongTagCount()

    }

    private fun getSongTags(): MutableList<TagDTO> {
        val tags = db.songQueries.selectSongTags(path) { id, tag, category ->
                TagDTO(id, tag, category)
            }.executeAsList()

        return tags.toMutableStateList()
    }

    private fun getSongTagCount(): Int {
        return _songTagList.value.size
    }
}