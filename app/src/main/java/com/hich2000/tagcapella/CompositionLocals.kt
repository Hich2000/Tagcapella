package com.hich2000.tagcapella

import androidx.compose.runtime.compositionLocalOf
import com.hich2000.tagcapella.music_player.MusicPlayerViewModel
import com.hich2000.tagcapella.music_player.SongListViewModel
import com.hich2000.tagcapella.tags.TagViewModel

val LocalMusicPlayerViewModel = compositionLocalOf<MusicPlayerViewModel> { error("MusicPlayerViewModel not provided") }
val LocalSongListViewModel = compositionLocalOf<SongListViewModel> { error("SongListViewModel not provided") }
val localTagViewModel = compositionLocalOf<TagViewModel> { error("TagViewMdoel not provided") }