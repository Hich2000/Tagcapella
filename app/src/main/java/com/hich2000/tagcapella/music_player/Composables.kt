package com.hich2000.tagcapella.music_player

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Queue
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.ShuffleOn
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.Player
import com.hich2000.tagcapella.songs.Song
import com.hich2000.tagcapella.songs.SongViewModel
import com.hich2000.tagcapella.tags.TagCard
import com.hich2000.tagcapella.tags.TagDTO
import com.hich2000.tagcapella.tags.TagDialog
import com.hich2000.tagcapella.tags.TagViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.io.path.Path
import kotlin.io.path.nameWithoutExtension

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicControls(
    musicPlayerViewModel: MusicPlayerViewModel = hiltViewModel(),
) {
    // Use the state variable to determine if the MediaController and songlist are initialized
    val isMediaControllerInitialized by musicPlayerViewModel.isMediaControllerInitialized.collectAsState()

    if (isMediaControllerInitialized) {
        val coroutineScope = rememberCoroutineScope()

        //observe the isPlaying state for ui changes
        val isPlaying by musicPlayerViewModel.isPlaying.collectAsState()
        //observe the shuffleModeEnabled state for ui changes
        val shuffleModeEnabled by musicPlayerViewModel.shuffleModeEnabled.collectAsState()
        //observe the loopMode state for ui changes
        val repeatMode by musicPlayerViewModel.repeatMode.collectAsState()

        val playbackPosition by musicPlayerViewModel.playbackPosition.collectAsState()
        val playbackDuration by musicPlayerViewModel.playbackDuration.collectAsState()

        //get the mediaController itself
        val mediaController = musicPlayerViewModel.mediaController

        //bottomsheet stuff
        val sheetState = rememberBottomSheetScaffoldState()
        val songList by musicPlayerViewModel.currentPlaylist.collectAsState()
        val showDialog by musicPlayerViewModel.showDialog.collectAsState()

        val includedTags by musicPlayerViewModel.includedTags.collectAsState()
        val excludedTags by musicPlayerViewModel.excludedTags.collectAsState()

        if (showDialog) {
            TagDialog(
                onButtonPress = {
                    coroutineScope.launch {
                        val newPlaylist = musicPlayerViewModel.getFilteredPlaylist(includedTags, excludedTags)
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
                                Color.hsl(112f, 0.5f, 0.3f)
                            } else if (excludedTags.contains(tag)) {
                                Color.Red
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

                                mediaController.seekTo(
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
                            modifier = Modifier
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    // Temporary state for slider interaction
                    var sliderPosition by remember { mutableFloatStateOf(playbackPosition.toFloat()) }
                    var isUserInteracting by remember { mutableStateOf(false) }

                    // Sync sliderPosition with playbackPosition when not interacting
                    LaunchedEffect(playbackPosition) {
                        if (!isUserInteracting) {
                            sliderPosition = playbackPosition.toFloat()
                        }
                    }

                    PlaybackSlider(
                        playbackPosition = sliderPosition.toLong(),
                        playbackDuration = playbackDuration,
                        onValueChange = { newPosition: Float ->
                            isUserInteracting = true
                            sliderPosition = newPosition
                        },
                        onValueChangeFinished = {
                            isUserInteracting = false
                            musicPlayerViewModel.setPlaybackPosition(
                                sliderPosition.toLong(),
                                true
                            )
                        }
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .border(2.dp, MaterialTheme.colorScheme.tertiary)
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
                            contentDescription = "Shuffle button",
                            tint = MaterialTheme.colorScheme.secondary
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
                            contentDescription = "Skip to previous button",
                            tint = MaterialTheme.colorScheme.secondary
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
                            contentDescription = contentDescription,
                            tint = MaterialTheme.colorScheme.secondary
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
                            contentDescription = "Skip to next button",
                            tint = MaterialTheme.colorScheme.secondary
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
                            contentDescription = "Shuffle button",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }

                }
            }
        }
    }

}

@SuppressLint("DefaultLocale")
@Composable
fun PlaybackSlider(
    playbackPosition: Long,
    playbackDuration: Long,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit
) {

    val pHours = TimeUnit.MILLISECONDS.toHours(playbackPosition)
    val pMinutes = TimeUnit.MILLISECONDS.toMinutes(playbackPosition) % 60
    val pSeconds = TimeUnit.MILLISECONDS.toSeconds(playbackPosition) % 60
    val formattedPosition = if (pHours > 0) {
        String.format("%d:%02d:%02d", pHours, pMinutes, pSeconds)
    } else {
        String.format("%02d:%02d", pMinutes, pSeconds)
    }

    //formatted duration needs to only trigger when duration has actually propagated and is not negative
    var formattedDuration = "--:--"
    if (playbackDuration > 0) {
        val dHours = TimeUnit.MILLISECONDS.toHours(playbackDuration)
        val dMinutes = TimeUnit.MILLISECONDS.toMinutes(playbackDuration) % 60
        val dSeconds = TimeUnit.MILLISECONDS.toSeconds(playbackDuration) % 60
        formattedDuration = if (pHours > 0) {
            String.format("%d:%02d:%02d", dHours, dMinutes, dSeconds)
        } else {
            String.format("%02d:%02d", dMinutes, dSeconds)
        }
    }


    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                "$formattedPosition/$formattedDuration",
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Slider(
                value = playbackPosition.toFloat(),
                valueRange = if (playbackDuration > 0) {
                    (0f..playbackDuration.toFloat())
                } else {
                    (0f..1f)
                },
                onValueChange = onValueChange,
                onValueChangeFinished = onValueChangeFinished,
                colors = SliderColors(
                    thumbColor = MaterialTheme.colorScheme.secondary,
                    activeTrackColor = MaterialTheme.colorScheme.secondary,
                    activeTickColor = Color.Unspecified,
                    inactiveTrackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                    inactiveTickColor = Color.Unspecified,
                    disabledThumbColor = Color.Unspecified,
                    disabledActiveTrackColor = Color.Unspecified,
                    disabledActiveTickColor = Color.Unspecified,
                    disabledInactiveTrackColor = Color.Unspecified,
                    disabledInactiveTickColor = Color.Unspecified,
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongScreen(
    mediaPlayerViewModel: MusicPlayerViewModel = hiltViewModel(),
    tagViewModel: TagViewModel = hiltViewModel(),
    songViewModel: SongViewModel = hiltViewModel()
) {
    val showTagDialog by tagViewModel.showDialog.collectAsState()
    val songToTag = remember { mutableStateOf<Song?>(null) }
    var onTagClick by remember { mutableStateOf<(TagDTO) -> Unit>({}) }
    var tagCardComposable by remember { mutableStateOf<@Composable (tag: TagDTO) -> Unit>({}) }
    val coroutineScope = rememberCoroutineScope()

    val includedTags by mediaPlayerViewModel.includedTags.collectAsState()
    val excludedTags by mediaPlayerViewModel.excludedTags.collectAsState()
    val songListInitialized by songViewModel.isInitialized.collectAsState()
    val songList by songViewModel.songList.collectAsState()

    if (showTagDialog) {
        TagDialog(
            onButtonPress = {
                coroutineScope.launch {
                    val filteredSongList = mediaPlayerViewModel.getFilteredPlaylist(
                        includedTags,
                        excludedTags
                    )
                    mediaPlayerViewModel.preparePlaylist(filteredSongList)
                    tagViewModel.closeDialog()
                }
            },
            tagCardComposable = tagCardComposable
        )
    }

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
                tagCallBack = {
                    //todo extract this copy and pasted code into a variable or something
                    onTagClick = { tag ->
                        if (songToTag.value!!.songTagList.value.contains(tag)) {
                            songToTag.value!!.path.let {
                                tagViewModel.deleteSongTag(tag, songToTag.value!!)
                            }
                        } else {
                            songToTag.value!!.path.let {
                                tagViewModel.addSongTag(tag, songToTag.value!!)
                            }
                        }
                    }
                    tagCardComposable = { tag ->
                        val isTagged = songToTag.value?.songTagList?.value?.contains(tag) ?: false


                        TagCard(
                            tag = tag,
                            onClick = onTagClick,
                            backgroundColor =
                                if (isTagged) {
                                    Color.hsl(112f, 0.5f, 0.3f)
                                } else {
                                    MaterialTheme.colorScheme.background
                                },
                        )
                    }

                    tagViewModel.openDialog()
                    songToTag.value = song
                },
                onClick = {
                    tagCardComposable = { tag ->
                        val isTagged = songToTag.value?.songTagList?.value?.contains(tag) ?: false


                        TagCard(
                            tag = tag,
                            onClick = onTagClick,
                            backgroundColor =
                                if (isTagged) {
                                    Color.hsl(112f, 0.5f, 0.3f)
                                } else {
                                    MaterialTheme.colorScheme.background
                                },
                        )
                    }

                    //todo extract this copy and pasted code into a variable or something
                    onTagClick = { tag ->
                        if (songToTag.value!!.songTagList.value.contains(tag)) {
                            songToTag.value!!.path.let {
                                tagViewModel.deleteSongTag(tag, songToTag.value!!)
                            }
                        } else {
                            songToTag.value!!.path.let {
                                tagViewModel.addSongTag(tag, songToTag.value!!)
                            }
                        }
                    }

                    tagViewModel.openDialog()
                    songToTag.value = song

                }
            )
        },
        floatingActionButton = {}
    )
}

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

@Composable
fun SongCard(
    song: Song,
    tagCallBack: (() -> Unit)? = null,
    onClick: () -> Unit = {},
    backgroundColor: Color = MaterialTheme.colorScheme.primary
) {
    val scroll = rememberScrollState(0)
    val songTagCount by song.songTagCount
    val songPath = Path(song.path)

    Card(
        modifier = Modifier
            .border(2.dp, MaterialTheme.colorScheme.tertiary, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.tertiary)
            .height(50.dp),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(horizontal = 8.dp)
        ) {
            Icon(
                Icons.Rounded.PlayArrow,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .weight(0.1f)
                    .padding(0.dp)
            )
            Text(
                songPath.nameWithoutExtension,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .horizontalScroll(scroll)
                    .weight(1f),
                color = MaterialTheme.colorScheme.onBackground
            )

            if (tagCallBack != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(0.4f)
                        .padding(0.dp)
                ) {
                    IconButton(
                        onClick = tagCallBack,
                        modifier = Modifier.padding(horizontal = 0.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Label,
                            contentDescription = "Add tags",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(horizontal = 0.dp)
                        )
                    }
                    Text(
                        "($songTagCount)",
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 0.dp)
                    )
                }
            }
        }
    }
}
