package com.hich2000.tagcapella.tagsAndCategories.tags

import androidx.compose.runtime.toMutableStateList
import com.hich200.tagcapella.TagcapellaDb
import com.hich2000.tagcapella.songs.Song
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

    private var _tags = MutableStateFlow<List<TagDTO>>(emptyList())
    val tags: StateFlow<List<TagDTO>> get() = _tags

    init {
        repositoryScope.launch {
            initTagList()
        }
    }

    fun initTagList() {
        val tagList = db.tagQueries.selectAll { id, tag, category -> TagDTO(id, tag, category) }
            .executeAsList()
        tagList.forEach { tag ->
            tag.taggedSongs = getTaggedSongs(tag)
        }
        _tags.value =  tagList
    }

    fun insertTag(newTag: String, category: Long?): TagDTO {
        db.tagQueries.insertTag(id = null, tag = newTag, category = category)
        val newTag =  db.tagQueries.lastInsertedTag { id, tag, category ->
            TagDTO(
                id,
                tag,
                category
            )
        }.executeAsOne()
        initTagList()
        return newTag
    }

    fun updateTag(id: Long, tag: String, category: Long?) {
        db.tagQueries.updateTag(id = id, tag = tag, category = category)
        initTagList()
    }

    fun deleteTag(id: Long) {
        db.tagQueries.deleteTag(id)
        initTagList()
    }

    fun addSongTag(tag: TagDTO, song: Song) {
        if (!getTaggedSongs(tag).contains(song)) {
            song.path.let { db.tagQueries.addSongTag(it, tag.id) }
            song.reloadTagList()
            initTagList()
        }
    }

    fun deleteSongTag(tag: TagDTO, song: Song) {
        song.path.let { db.tagQueries.deleteSongTag(tag.id, it) }
        song.reloadTagList()
        initTagList()
    }

    fun getTaggedSongs(tag: TagDTO): MutableList<Song>  {
        val songs = db.tagQueries.selectTaggedSongs(tag.id) { _, path  ->
            Song(path, database)
        }.executeAsList()
        return songs.toMutableStateList()
    }

    fun getTagById(tagId: Long): TagDTO? {
        val db = database.db
        val tagResult = db.tagQueries.selectTagById(tagId) { id, tag, category ->
            TagDTO(id, tag, category)
        }.executeAsOneOrNull()

        return tagResult
    }
}