package com.hich2000.tagcapella.tags

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.hich200.tagcapella.TagcapellaDb
import com.hich200.tagcapella.Tag

class TagViewModel(application: Application) : AndroidViewModel(application) {

    private var _tags = mutableStateListOf<Tag>()
    val tags: SnapshotStateList<Tag> get() = _tags

    private var db: TagcapellaDb

    init {
        val driver: SqlDriver = AndroidSqliteDriver(TagcapellaDb.Schema, getApplication(), "tagcapella.db")
        db = TagcapellaDb(driver)

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