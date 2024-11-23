package com.hich2000.tagcapella.music_player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.ShuffleOn
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player

@Composable
fun MusicControls(
    mediaControllerViewModel: MusicPlayerViewModel = hiltViewModel()
) {
    // Use the state variable to determine if the MediaController and songlist are initialized
    val isMediaControllerInitialized by mediaControllerViewModel.isMediaControllerInitialized
    if (isMediaControllerInitialized) {
        //observe the isPlaying state for ui changes
        val isPlaying by mediaControllerViewModel.isPlaying
        //observe the shuffleModeEnabled state for ui changes
        val shuffleModeEnabled by mediaControllerViewModel.shuffleModeEnabled
        //observe the loopMode state for ui changes
        val repeatMode by mediaControllerViewModel.repeatMode

        //get the mediaController itself
        val mediaController = mediaControllerViewModel.mediaController
        Row(
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, Color.Gray),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            //shuffle mode
            IconButton(
                onClick = {
                    if (shuffleModeEnabled) {
                        mediaController.shuffleModeEnabled = false
                    } else {
                        mediaController.shuffleModeEnabled = true
                    }
                }
            ) {
                val icon =
                    if (shuffleModeEnabled) Icons.Default.ShuffleOn else Icons.Default.Shuffle
                Icon(
                    icon,
                    contentDescription = "Shuffle button"
                )
            }
            //skip previous
            IconButton(
                onClick = {
                    mediaController.seekToPrevious()
                }
            ) {
                Icon(
                    Icons.Default.SkipPrevious,
                    contentDescription = "Skip to previous button"
                )
            }
            //play/pause
            IconButton(
                onClick = {
                    if (isPlaying) mediaController.pause() else mediaController.play()
                }
            ) {
                val icon = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow
                val contentDescription = if (isPlaying) "Pause" else "Play"
                Icon(
                    icon,
                    contentDescription = contentDescription
                )
            }
            //skip next
            IconButton(
                onClick = {
                    mediaController.seekToNext()
                }
            ) {
                Icon(
                    Icons.Default.SkipNext,
                    contentDescription = "Skip to next button"
                )
            }
            //loop mode
            IconButton(
                onClick = {
                    if (repeatMode == Player.REPEAT_MODE_OFF) {
                        mediaController.repeatMode = Player.REPEAT_MODE_ALL
                    } else if (repeatMode == Player.REPEAT_MODE_ALL) {
                        mediaController.repeatMode = Player.REPEAT_MODE_ONE
                    } else if (repeatMode == Player.REPEAT_MODE_ONE) {
                        mediaController.repeatMode = Player.REPEAT_MODE_OFF
                    }
                }
            ) {
                var icon = Icons.Default.Repeat

                if (repeatMode == Player.REPEAT_MODE_OFF) {
                    icon = Icons.AutoMirrored.Filled.ArrowRightAlt
                } else if (repeatMode == Player.REPEAT_MODE_ALL) {
                    icon = Icons.Default.Repeat
                } else if (repeatMode == Player.REPEAT_MODE_ONE) {
                    icon = Icons.Default.RepeatOne
                }

                Icon(
                    icon,
                    contentDescription = "Shuffle button"
                )
            }

        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongScreen(
    mediaPlayerViewModel: MusicPlayerViewModel = hiltViewModel()
) {

    val showTagDialog = remember { mutableStateOf(false) }

    if (showTagDialog.value) {
        BasicAlertDialog(
            onDismissRequest = {
                showTagDialog.value = false
            }
        ) {

        }
    }

    SongList(songCard = { song ->
        SongCard(
            song = song,
            onClick = {
                val index = mediaPlayerViewModel.songRepository.songList.indexOf(song)
                if (index >= 0) {
                    mediaPlayerViewModel.mediaController.seekTo(index, C.TIME_UNSET)
                }
            }
        )
    })
}

@Composable
fun SongList(
    modifier: Modifier = Modifier,
    songCard: @Composable (song: SongDTO) -> Unit,
    mediaController: MusicPlayerViewModel = hiltViewModel(),
) {
    val songRepository = mediaController.songRepository

    // Use the state variable to determine if the MediaController and song list are initialized
    val isMediaControllerInitialized by mediaController.isMediaControllerInitialized
    val isSongListInitialized by songRepository.isInitialized

    val songList = remember { songRepository.songList }

    LazyColumn(
        modifier = modifier
    ) {
        if (!isMediaControllerInitialized || !isSongListInitialized) {
            item {
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            items(songList) { song ->
                songCard(song)
            }
        }
    }
}

@Composable
fun SongCard(
    song: SongDTO,
    onClick: () -> Unit = {},
    backgroundColor: Color = Color.Black
) {
    val scroll = rememberScrollState(0)

    val mediaItem = MediaItem.Builder()
        .setMediaId(song.path)
        .setUri(song.path)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(song.title)
                .setDisplayTitle(song.title)
                .build()
        )
        .build()

    Card(
        modifier = Modifier
            .border(2.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .background(Color.Gray)
            .height(75.dp),
        onClick = onClick
    ) {
        Row (
            modifier = Modifier.background(backgroundColor)
        ) {
            Icon(Icons.Rounded.PlayArrow, contentDescription = null)
            Text(
                mediaItem.mediaMetadata.title.toString(),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scroll)
                    .fillMaxHeight()
            )
        }
    }
}
