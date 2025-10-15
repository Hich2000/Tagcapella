package com.hich2000.tagcapella.tags

import com.hich2000.tagcapella.songs.Song
import kotlinx.coroutines.flow.MutableStateFlow

data class TagDTO(
    val id: Long,
    val tag: String,
    val categoryId: Long?,
    val taggedSongs: MutableStateFlow<List<Song>> = MutableStateFlow(emptyList())
)