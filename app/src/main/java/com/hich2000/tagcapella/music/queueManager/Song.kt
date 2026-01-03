package com.hich2000.tagcapella.music.queueManager

import com.hich2000.tagcapella.tagsAndCategories.tags.Tag

data class Song(val id: Long, val path: String, val tags: List<Tag>) {
    val tagCount: Int get() = tags.count()
}