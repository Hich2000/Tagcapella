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
}