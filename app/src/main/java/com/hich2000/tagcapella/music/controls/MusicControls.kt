package com.hich2000.tagcapella.music.controls

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Queue
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.ShuffleOn
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.Player
import com.hich2000.tagcapella.music.MusicPlayerViewModel
import com.hich2000.tagcapella.music.songScreen.SongCard
import com.hich2000.tagcapella.music.songScreen.SongList
import com.hich2000.tagcapella.tagsAndCategories.tags.dialogs.TagDialog
import com.hich2000.tagcapella.tagsAndCategories.tags.tagList.TagCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicControls(
    musicPlayerViewModel: MusicPlayerViewModel = hiltViewModel(),
) {
    // Use the state variable to determine if the MediaController and songlist are initialized
    val isMediaControllerInitialized by musicPlayerViewModel.isMediaControllerInitialized.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val mediaController by musicPlayerViewModel.mediaController.collectAsState()
    val playerState by musicPlayerViewModel.playerState.collectAsState()

    //bottomsheet stuff
    val sheetState = rememberBottomSheetScaffoldState()
    val songList by musicPlayerViewModel.currentPlaylist.collectAsState()
    val showDialog by musicPlayerViewModel.showDialog.collectAsState()

    val includedTags by musicPlayerViewModel.includedTags.collectAsState()
    val excludedTags by musicPlayerViewModel.excludedTags.collectAsState()

    if (isMediaControllerInitialized) {
        if (showDialog) {
            TagDialog(
                onButtonPress = {
                    coroutineScope.launch {
                        val newPlaylist =
                            musicPlayerViewModel.getFilteredPlaylist(includedTags, excludedTags)
                        musicPlayerViewModel.preparePlaylist(newPlaylist)
                        musicPlayerViewModel.closeDialog()
                    }
                },
                tagCardComposable = { tag ->
                    TagCard(
                        tag = tag,
                        onClick = {
                            if (includedTags.contains(tag)) {
                                musicPlayerViewModel.removeIncludedTag(tag)
                                musicPlayerViewModel.addExcludedTag(tag)
                            } else if (excludedTags.contains(tag)) {
                                musicPlayerViewModel.removeExcludedTag(tag)
                            } else {
                                musicPlayerViewModel.addIncludedTag(tag)
                            }
                        },
                        backgroundColor =
                            if (includedTags.contains(tag)) {
                                Color.Companion.hsl(112f, 0.5f, 0.3f)
                            } else if (excludedTags.contains(tag)) {
                                Color.Companion.Red
                            } else {
                                MaterialTheme.colorScheme.background
                            },
                    )
                }
            )
        }

        BottomSheetScaffold(
            scaffoldState = sheetState,
            sheetPeekHeight = 40.dp,
            sheetShape = CutCornerShape(topStart = 16.dp, topEnd = 16.dp),
            sheetContent = {
                SongList(
                    songList = songList,
                    songCard = { song ->
                        SongCard(
                            song = song,
                            tagCallBack = {},
                            onClick = {
                                val index = songList.indexOfFirst { listItem ->
                                    listItem == song
                                }

                                mediaController?.seekTo(
                                    index,
                                    0
                                )
                            }
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { musicPlayerViewModel.openDialog() },
                            containerColor = MaterialTheme.colorScheme.background,
                            modifier = Modifier.Companion
                                .padding(16.dp)
                                .border(2.dp, MaterialTheme.colorScheme.tertiary),
                            shape = RectangleShape
                        ) {
                            Icon(
                                Icons.Default.Queue,
                                contentDescription = "Queue",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.Companion
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .padding(vertical = 20.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.Companion.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    // Temporary state for slider interaction
                    var sliderPosition by remember { mutableFloatStateOf(playerState.position.toFloat()) }
                    var isUserInteracting by remember { mutableStateOf(false) }

                    // Sync sliderPosition with playbackPosition when not interacting
                    LaunchedEffect(playerState.position) {
                        if (!isUserInteracting) {
                            sliderPosition = playerState.position.toFloat()
                        }
                    }

                    PlaybackSlider(
                        playbackPosition = sliderPosition.toLong(),
                        playbackDuration = playerState.duration,
                        onValueChange = { newPosition: Float ->
                            isUserInteracting = true
                            sliderPosition = newPosition
                        },
                        onValueChangeFinished = {
                            isUserInteracting = false
                            musicPlayerViewModel.setPlaybackPosition(sliderPosition.toLong())
                        }
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Companion.CenterVertically,
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .border(2.dp, MaterialTheme.colorScheme.tertiary)
                ) {
                    //shuffle mode
                    IconButton(
                        onClick = {
                            mediaController?.shuffleModeEnabled = !playerState.shuffleModeEnabled
                        }
                    ) {
                        val icon =
                            if (playerState.shuffleModeEnabled) Icons.Default.ShuffleOn else Icons.Default.Shuffle
                        Icon(
                            icon,
                            contentDescription = "Shuffle button",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                    //skip previous
                    IconButton(
                        onClick = {
                            mediaController?.seekToPrevious()
                        }
                    ) {
                        Icon(
                            Icons.Default.SkipPrevious,
                            contentDescription = "Skip to previous button",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                    //play/pause
                    IconButton(
                        onClick = {
                            musicPlayerViewModel.togglePlayback()
                        }
                    ) {
                        val icon = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow
                        val contentDescription = if (playerState.isPlaying) "Pause" else "Play"
                        Icon(
                            icon,
                            contentDescription = contentDescription,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                    //skip next
                    IconButton(
                        onClick = {
                            mediaController?.seekToNext()
                        }
                    ) {
                        Icon(
                            Icons.Default.SkipNext,
                            contentDescription = "Skip to next button",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                    //loop mode
                    IconButton(
                        onClick = {
                            when (playerState.repeatMode) {
                                Player.REPEAT_MODE_OFF -> {
                                    mediaController?.repeatMode = Player.REPEAT_MODE_ALL
                                }
                                Player.REPEAT_MODE_ALL -> {
                                    mediaController?.repeatMode = Player.REPEAT_MODE_ONE
                                }
                                Player.REPEAT_MODE_ONE -> {
                                    mediaController?.repeatMode = Player.REPEAT_MODE_OFF
                                }
                            }
                        }
                    ) {
                        var icon = Icons.Default.Repeat

                        when (playerState.repeatMode) {
                            Player.REPEAT_MODE_OFF -> {
                                icon = Icons.AutoMirrored.Filled.ArrowRightAlt
                            }
                            Player.REPEAT_MODE_ALL -> {
                                icon = Icons.Default.Repeat
                            }
                            Player.REPEAT_MODE_ONE -> {
                                icon = Icons.Default.RepeatOne
                            }
                        }

                        Icon(
                            icon,
                            contentDescription = "Shuffle button",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }

                }
            }
        }
    }

}

