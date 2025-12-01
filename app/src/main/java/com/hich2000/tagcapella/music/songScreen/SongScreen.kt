package com.hich2000.tagcapella.music.songScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hich2000.tagcapella.main.navigation.LocalNavController
import com.hich2000.tagcapella.main.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongScreen(
    songScreenViewModel: SongScreenViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val songListInitialized by songScreenViewModel.songRepoInitialized.collectAsState()
    val songList by songScreenViewModel.songs.collectAsState()

    if (!songListInitialized) {
        CircularProgressIndicator(
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    SongList(
        songList = songList,
        songCard = { song ->
            SongCard(
                song = song,
                showTagCount = true,
                onClick = { navController.navigate(Route.Songs.Tags.createRoute(song.path)) }
            )
        }
    )
}