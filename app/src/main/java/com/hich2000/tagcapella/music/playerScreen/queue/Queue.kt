package com.hich2000.tagcapella.music.playerScreen.queue

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Queue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hich2000.tagcapella.R
import com.hich2000.tagcapella.main.navigation.LocalNavController
import com.hich2000.tagcapella.main.navigation.Route
import com.hich2000.tagcapella.music.songScreen.SongCard
import com.hich2000.tagcapella.music.songScreen.SongList

@Composable
fun Queue(
    queueViewModel: QueueViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val queue by queueViewModel.currentQueue.collectAsState()
    val playerState by queueViewModel.playerState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Route.Player.QueueBuilder.route)
                },
                shape = RectangleShape,
                containerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier.border(2.dp, MaterialTheme.colorScheme.tertiary)
            ) {
                Icon(
                    imageVector = Icons.Default.Queue,
                    contentDescription = "filter queue",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        SongList(
            songList = queue,
            modifier = Modifier.padding(innerPadding)
        ) { song ->
            if (playerState.currentSong == song.path) {
                Box(
                    contentAlignment = Alignment.Center,
                ) {
                    SongCard(
                        song = song,
                        onClick = { queueViewModel.seek(song) }
                    )
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.soundwave),
                            contentDescription = "Sound wave",
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            } else {
                SongCard(
                    song = song,
                    onClick = { queueViewModel.seek(song) }
                )
            }
        }
    }
}