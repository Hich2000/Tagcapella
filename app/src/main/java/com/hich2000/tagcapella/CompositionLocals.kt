package com.hich2000.tagcapella

import androidx.compose.runtime.compositionLocalOf
import com.hich2000.tagcapella.music_player.MusicPlayerViewModel

val LocalMusicPlayerViewModel = compositionLocalOf<MusicPlayerViewModel> { error("MusicPlayerViewModel not provided") }
