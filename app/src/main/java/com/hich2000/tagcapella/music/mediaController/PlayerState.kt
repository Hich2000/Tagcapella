package com.hich2000.tagcapella.music.mediaController

data class PlayerState(
    val isPlaying: Boolean,
    val shuffleModeEnabled: Boolean,
    val repeatMode: Int,
    val position: Long,
    val duration: Long,
) {

    companion object {
        fun emptyPlayerState(): PlayerState {
            return PlayerState(
                isPlaying = false,
                shuffleModeEnabled = false,
                repeatMode = 1,
                position = 0L,
                duration = 0L
            )
        }
    }
}
