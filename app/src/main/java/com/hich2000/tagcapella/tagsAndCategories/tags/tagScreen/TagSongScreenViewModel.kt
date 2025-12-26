package com.hich2000.tagcapella.tagsAndCategories.tags.tagScreen

import androidx.lifecycle.ViewModel
import com.hich2000.tagcapella.music.queueManager.Song
import com.hich2000.tagcapella.music.queueManager.SongRepository
import com.hich2000.tagcapella.tagsAndCategories.tags.Tag
import com.hich2000.tagcapella.tagsAndCategories.tags.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class TagSongScreenViewModel @Inject constructor(
    private val tagRepository: TagRepository,
    private val songRepository: SongRepository
) : ViewModel() {
    val tags: StateFlow<List<Tag>> get() = tagRepository.tags
    val songs: StateFlow<List<Song>> get() = songRepository.songList

    fun getTag(tagId: Long) : Tag {
        return tags.value.first {
            it.id == tagId
        }
    }

    fun addSongTag(tag: Tag, song: Song) {
        songRepository.addSongTag(song = song, tag = tag)
        tagRepository.initTagList()
    }

    fun deleteSongTag(tag: Tag, song: Song) {
        songRepository.deleteSongTag(song = song, tag = tag)
        tagRepository.initTagList()
    }
}