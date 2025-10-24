package com.hich2000.tagcapella.music.playerScreen.queue

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.hich2000.tagcapella.music.songScreen.SongCard
import com.hich2000.tagcapella.music.songScreen.SongList

@Composable
fun Queue(
    queueViewModel: QueueViewModel = hiltViewModel()
) {
    val queue by queueViewModel.currentQueue.collectAsState()

    SongList(
        songList = queue
    ) { song ->
        SongCard(
            song = song,
            onClick = { queueViewModel.seek(song) }
        )
    }
}