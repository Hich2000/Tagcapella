package com.hich2000.tagcapella.tags

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.hich200.tagcapella.Tag
import com.hich200.tagcapella.TagcapellaDb
import com.hich2000.tagcapella.Database
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TagViewModel @Inject constructor(
    database: Database
) : ViewModel() {

    private var _tags = mutableStateListOf<Tag>()
    val tags: SnapshotStateList<Tag> get() = _tags

    private var db: TagcapellaDb = database.db

    init {
        _tags = selectAllTags()
    }

    fun selectAllTags(): SnapshotStateList<Tag> {
        return db.tagQueries.selectAll().executeAsList().toMutableStateList()
    }

    fun insertTag(tag: String) {
        db.tagQueries.insertTag(null, tag)
        val newTag = db.tagQueries.lastInsertedTag().executeAsOne()
        _tags.add(newTag)
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
}