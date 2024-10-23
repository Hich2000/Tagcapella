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
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.hich2000.tagcapella.LocalMusicPlayerViewModel
import com.hich2000.tagcapella.LocalSongListViewModel


@Composable
fun MusicControls() {
    //get the mediaController for controls
    val mediaController = LocalMusicPlayerViewModel.current.mediaController
    //observe the isPlaying state for ui changes
    val isPlaying by LocalMusicPlayerViewModel.current.isPlaying
    //observe the shuffleModeEnabled state for ui changes
    val shuffleModeEnabled by LocalMusicPlayerViewModel.current.shuffleModeEnabled
    //observe the loopMode state for ui changes
    val repeatMode by LocalMusicPlayerViewModel.current.repeatMode


    BottomAppBar(
        modifier = Modifier
            .border(2.dp, Color.Gray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, Color.Gray),
            horizontalArrangement = Arrangement.Center
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

@Composable
fun SongList(modifier: Modifier = Modifier) {

    val mediaController = LocalMusicPlayerViewModel.current
    val songListViewModel = LocalSongListViewModel.current


    // Use the state variable to determine if the MediaController and songlist are initialized
    val isMediaControllerInitialized by mediaController.isMediaControllerInitialized
    val isSongListInitialized by songListViewModel.isInitialized

    val songList = remember { songListViewModel.songList }

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
            itemsIndexed(songList) { index, song ->
                SongCard(song, index)
            }
        }
    }
}

@Composable
fun SongCard(mediaItem: MediaItem, mediaItemIndex: Int) {
    val scroll = rememberScrollState(0)
    //get the mediaController for controls
    val mediaController = LocalMusicPlayerViewModel.current.mediaController
    Card(
        modifier = Modifier
            .border(2.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .background(Color.Gray)
            .height(75.dp),
        onClick = {
            mediaController.seekTo(mediaItemIndex, C.TIME_UNSET)
        }
    ) {
        Row {
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
