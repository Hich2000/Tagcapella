package com.hich2000.tagcapella.music

import com.hich2000.tagcapella.tagsAndCategories.tags.TagDTO

data class Song(val path: String, val tags: List<TagDTO>) {
    val tagCount: Int get() = tags.count()
}