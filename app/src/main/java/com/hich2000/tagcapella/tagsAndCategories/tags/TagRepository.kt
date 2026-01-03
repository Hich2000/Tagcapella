package com.hich2000.tagcapella.tagsAndCategories.tags

import androidx.compose.runtime.toMutableStateList
import com.hich200.tagcapella.TagcapellaDb
import com.hich2000.tagcapella.music.queueManager.Song
import com.hich2000.tagcapella.utils.Database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagRepository @Inject constructor(
    private val database: Database
) {

    private val db: TagcapellaDb = database.db

    // Define a CoroutineScope for the repository
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var _tags = MutableStateFlow<List<Tag>>(emptyList())
    val tags: StateFlow<List<Tag>> get() = _tags

    init {
        repositoryScope.launch {
            initTagList()
        }
    }

    fun initTagList() {
        val tagList = db.tagQueries.selectAll { id, tag, category -> Tag(id, tag, category) }
            .executeAsList()
        tagList.forEach { tag ->
            tag.taggedSongs = getTaggedSongs(tag)
        }
        _tags.value = tagList
    }

    fun insertTag(newTag: String, category: Long?) {
        db.tagQueries.insertTag(id = null, tag = newTag, category = category)
        db.tagQueries.lastInsertedTag { id, tag, category ->
            Tag(
                id,
                tag,
                category
            )
        }.executeAsOne()
        initTagList()
    }

    fun updateTag(id: Long, tag: String, category: Long?) {
        db.tagQueries.updateTag(id = id, tag = tag, category = category)
        initTagList()
    }

    fun deleteTag(id: Long) {
        db.tagQueries.deleteTag(id)
        initTagList()
    }

    fun getTaggedSongs(tag: Tag): MutableList<Song>  {
        val songs = db.tagQueries.selectTaggedSongs(tag.id) { id, path  ->
            Song(id, path, emptyList())
        }.executeAsList()
        return songs.toMutableStateList()
    }

    fun getTagById(tagId: Long): Tag? {
        val db = database.db
        val tagResult = db.tagQueries.selectTagById(tagId) { id, tag, category ->
            Tag(id, tag, category)
        }.executeAsOneOrNull()

        return tagResult
    }
}