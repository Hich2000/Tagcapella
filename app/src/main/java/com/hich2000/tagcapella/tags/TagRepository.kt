package com.hich2000.tagcapella.tags

import androidx.compose.runtime.toMutableStateList
import com.hich200.tagcapella.TagcapellaDb
import com.hich2000.tagcapella.utils.Database
import com.hich2000.tagcapella.songs.Song
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagRepository @Inject constructor(
    private val database: Database
) {

    private val db: TagcapellaDb = database.db

    fun selectAllTags(): List<TagDTO> {
        return db.tagQueries.selectAll { id, tag, category -> TagDTO(id, tag, category) }
            .executeAsList()
            .map { it.copy() }
    }

    fun insertTag(newTag: String, category: Long?): TagDTO {
        db.tagQueries.insertTag(id = null, tag = newTag, category = category)
        return db.tagQueries.lastInsertedTag { id, tag, category ->
            TagDTO(
                id,
                tag,
                category
            )
        }
            .executeAsOne()
    }

    fun updateTag(id: Long, tag: String, category: Long?) {
        db.tagQueries.updateTag(id = id, tag = tag, category = category)
    }

    fun deleteTag(id: Long) {
        db.tagQueries.deleteTag(id)
    }

    fun addSongTag(tag: TagDTO, song: Song) {
        if (!getTaggedSongs(tag).contains(song)) {
            song.path.let { db.tagQueries.addSongTag(it, tag.id) }
            song.reloadTagList()
        }
    }

    fun deleteSongTag(tag: TagDTO, song: Song) {
        song.path.let { db.tagQueries.deleteSongTag(tag.id, it) }
        song.reloadTagList()
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