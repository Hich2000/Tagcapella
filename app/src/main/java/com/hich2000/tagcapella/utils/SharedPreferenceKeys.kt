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
}