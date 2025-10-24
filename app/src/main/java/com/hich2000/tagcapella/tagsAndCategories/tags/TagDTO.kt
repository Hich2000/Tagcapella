package com.hich2000.tagcapella.tagsAndCategories.tags

import com.hich2000.tagcapella.music.queueManager.Song

data class TagDTO(
    val id: Long,
    val tag: String,
    val categoryId: Long?,
    var taggedSongs: List<Song> = emptyList()
)