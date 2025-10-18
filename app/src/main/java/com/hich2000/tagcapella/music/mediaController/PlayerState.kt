package com.hich2000.tagcapella.music.mediaController

data class PlayerState(
    val isPlaying: Boolean,
    val shuffleModeEnabled: Boolean,
    val repeatMode: Int,
    val position: Long,
    val duration: Long,
)
