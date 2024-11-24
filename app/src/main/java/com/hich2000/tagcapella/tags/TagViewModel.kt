package com.hich2000.tagcapella.tags

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.hich200.tagcapella.TagcapellaDb
import com.hich2000.tagcapella.Database
import com.hich2000.tagcapella.music_player.SongDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class TagDTO @Inject constructor(val id: Long, val tag: String, val database: Database) {

    private var db = database.db

    private var _taggedSongList = mutableStateListOf<SongDTO>()
    val taggedSongList: SnapshotStateList<SongDTO>
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

    private fun getTaggedSongs(): MutableList<SongDTO> {
        val songs = db.tagQueries.selectTaggedSongs(id) { id, title, path ->
            SongDTO(id, path, title, database)
        }.executeAsList()
        return songs.toMutableStateList()
    }

    private fun getSongCount(): Int {
        return _taggedSongList.size
    }
}

@HiltViewModel
class TagViewModel @Inject constructor(
    private val database: Database
) : ViewModel() {

    private var _tags = mutableStateListOf<TagDTO>()
    val tags: SnapshotStateList<TagDTO> get() = _tags

    private val db: TagcapellaDb = database.db

    init {
        _tags = selectAllTags()
    }

    private fun selectAllTags(): SnapshotStateList<TagDTO> {
        return db.tagQueries.selectAll { id, tag -> TagDTO(id, tag, database) }.executeAsList()
            .toMutableStateList()
    }

    fun insertTag(tag: String) {
        db.tagQueries.insertTag(null, tag)
        val newTag = db.tagQueries.lastInsertedTag().executeAsOne()
        _tags.add(TagDTO(newTag.id, newTag.tag, database))
    }

    fun updateTag(id: Long, tag: String) {
        db.tagQueries.updateTag(tag, id)
        val updatedIndex = _tags.indexOfFirst { it.id == id }
        if (updatedIndex >= 0) {
            _tags[updatedIndex] = _tags[updatedIndex].copy(tag = tag)
        }
    }

    fun deleteTag(id: Long) {
        val deleteIndex = _tags.indexOfFirst { it.id == id }
        db.tagQueries.deleteTag(_tags[deleteIndex].id)
        _tags.removeAt(deleteIndex)
    }

    fun addSongTag(tag: TagDTO, song: SongDTO) {
        if (!tag.taggedSongList.contains(song)) {
            song.id?.let { db.tagQueries.addSongTag(it, tag.id) }
            tag.reloadSongList()
            song.reloadTagList()
        }
    }

    fun deleteSongTag(tag: TagDTO, song: SongDTO) {
        song.id?.let {
            db.tagQueries.deleteSongTag(tag.id, it)
            tag.reloadSongList()
            song.reloadTagList()
        }
    }
}