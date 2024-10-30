package com.hich2000.tagcapella.tags

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.hich200.tagcapella.TagcapellaDb
import com.hich200.tagcapella.Tags

class TagViewModel(application: Application) : AndroidViewModel(application) {

    private var _tags = mutableStateListOf<Tags>()
    val tags: SnapshotStateList<Tags> get() = _tags

    private var db: TagcapellaDb

    init {
        val driver: SqlDriver = AndroidSqliteDriver(TagcapellaDb.Schema, getApplication(), "tagcapella.db")
        db = TagcapellaDb(driver)

        _tags = selectAllTags()
    }

    fun selectAllTags(): SnapshotStateList<Tags> {
        return db.tagsQueries.selectAll().executeAsList().toMutableStateList()
    }

    fun insertTag(tag: String) {
        db.tagsQueries.insertTag(null, tag)
        val newTag = db.tagsQueries.lastInsertedTag().executeAsOne()
        _tags.add(newTag)
    }

    fun updateTag(id: Long, tag: String) {
        db.tagsQueries.updateTag(tag, id)
    }

    fun deleteTag(id: Long) {
        val deleteIndex = _tags.indexOfFirst { it.id == id }
        db.tagsQueries.deleteTag(_tags[deleteIndex].id)
        _tags.removeAt(deleteIndex)
    }
}