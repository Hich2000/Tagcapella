package com.hich2000.tagcapella.utils

sealed class SharedPreferenceKey<T>(
    val key: String
) {
    data object PlayerRepeatMode : SharedPreferenceKey<Int>("PLAYER_REPEAT_MODE")
    data object PlayerShuffleMode : SharedPreferenceKey<Boolean>("PLAYER_SHUFFLE_MODE")
    data object IncludedTags : SharedPreferenceKey<List<Long>>("INCLUDED_TAGS")
    data object ExcludedTags : SharedPreferenceKey<List<Long>>("EXCLUDED_TAGS")
    data object LastSongPlayed : SharedPreferenceKey<String>("LAST_SONG_PLAYED")
    data object LastSongPosition : SharedPreferenceKey<Long>("LAST_SONG_POSITION")
}