package com.hich2000.tagcapella.music.songScreen.songTagScreen

import androidx.lifecycle.ViewModel
import com.hich2000.tagcapella.music.queueManager.Song
import com.hich2000.tagcapella.music.queueManager.SongRepository
import com.hich2000.tagcapella.tagsAndCategories.tags.Tag
import com.hich2000.tagcapella.tagsAndCategories.tags.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SongTagScreenViewModel @Inject constructor(
    private val songRepository: SongRepository,
    private val tagRepository: TagRepository
) : ViewModel() {
    val songs: StateFlow<List<Song>> get() = songRepository.songList
    val tags: StateFlow<List<Tag>> get() = tagRepository.tags

    fun getSong(songPath: String) : Song {
        return songs.value.first {
            it.path == songPath
        }
    }

    fun addSongTag(song: Song, tag: Tag) {
        songRepository.addSongTag(song, tag)
        updateSongToTag()
    }

    fun deleteSongTag(song: Song, tag: Tag) {
        songRepository.deleteSongTag(song, tag)
        updateSongToTag()
    }

    private fun updateSongToTag() {
        //todo maybe make this better by not re-initializing the entire tag list.
        tagRepository.initTagList()
    }
}