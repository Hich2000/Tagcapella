package com.hich2000.tagcapella

import androidx.compose.runtime.compositionLocalOf
import com.hich2000.tagcapella.music_player.SongListViewModel
import com.hich2000.tagcapella.tags.TagViewModel

val LocalSongListViewModel = compositionLocalOf<SongListViewModel> { error("SongListViewModel not provided") }
val LocalTagViewModel = compositionLocalOf<TagViewModel> { error("TagViewMdoel not provided") }