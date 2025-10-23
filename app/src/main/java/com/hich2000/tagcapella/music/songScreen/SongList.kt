package com.hich2000.tagcapella.music.songScreen

import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import com.hich2000.tagcapella.music.Song

@Composable
fun SongList(
    modifier: Modifier = Modifier,
    songList: List<Song> = emptyList(),
    floatingActionButton: @Composable () -> Unit = {},
    songCard: @Composable (song: Song) -> Unit,
) {
    Scaffold(
        floatingActionButton = floatingActionButton
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current)
                )
        ) {
            items(songList) { song ->
                songCard(song)
            }
        }
    }
}