package com.hich2000.tagcapella.tags

import com.hich200.tagcapella.TagcapellaDb
import com.hich2000.tagcapella.utils.Database
import com.hich2000.tagcapella.songs.SongDTO
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagRepository @Inject constructor(
    private val database: Database
) {

    private val db: TagcapellaDb = database.db

    fun selectAllTags(): List<TagDTO> {
        return db.tagQueries.selectAll { id, tag -> TagDTO(id, tag, database) }.executeAsList()
            .toList()
    }

    fun insertTag(tag: String): TagDTO {
        db.tagQueries.insertTag(null, tag)
        return db.tagQueries.lastInsertedTag { _id, _tag -> TagDTO(_id, _tag, database) }
            .executeAsOne()
    }

    fun updateTag(id: Long, tag: String) {
        db.tagQueries.updateTag(tag, id)
        //todo add something here to check if the update was successful
    }

    fun deleteTag(id: Long) {
        db.tagQueries.deleteTag(id)
        //todo add something here to check if the delete was successful
    }

    fun addSongTag(tag: TagDTO, song: SongDTO) {
        if (!tag.taggedSongList.contains(song)) {
            song.id?.let { db.tagQueries.addSongTag(it, tag.id) }
            tag.reloadSongList()
            song.reloadTagList()
        }
    }

    fun deleteSongTag(tag: TagDTO, song: SongDTO) {
        song.id?.let { db.tagQueries.deleteSongTag(tag.id, it) }
        tag.reloadSongList()
        song.reloadTagList()
    }
}