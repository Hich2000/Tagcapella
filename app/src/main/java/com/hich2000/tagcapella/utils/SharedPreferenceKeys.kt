package com.hich2000.tagcapella.utils

import kotlin.reflect.KClass

enum class SharedPreferenceKeys (
    val key: String,
    val type: KClass<*>
) {
    PLAYER_REPEAT_MODE(
        key = "PLAYER_REPEAT_MODE",
        type = Int::class
    ),
    PLAYER_SHUFFLE_MODE(
        key = "PLAYER_SHUFFLE_MODE",
        type = Boolean::class
    ),
    INCLUDED_TAGS(
        key = "INCLUDED_TAGS",
        type = List::class
    ),
    EXCLUDED_TAGS(
        key = "EXCLUDED_TAGS",
        type = List::class
    ),
    LAST_SONG_PLAYED(
        key = "LAST_SONG_PLAYED",
        type = String::class
    ),
    LAST_SONG_POSITION(
        key = "LAST_SONG_POSITION",
        type = Long::class
    ),
}