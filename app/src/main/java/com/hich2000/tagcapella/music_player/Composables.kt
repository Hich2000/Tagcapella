package com.hich2000.tagcapella.music_player

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SmallFloatingActionButton
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import com.hich2000.tagcapella.songs.Song
import com.hich2000.tagcapella.songs.SongViewModel
import com.hich2000.tagcapella.tags.TagCard
import com.hich2000.tagcapella.tags.TagDTO
import com.hich2000.tagcapella.tags.TagList
import com.hich2000.tagcapella.tags.TagViewModel
import com.hich2000.tagcapella.utils.TagCapellaButton
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicControls(
    mediaControllerViewModel: MusicPlayerViewModel = hiltViewModel(),
) {
    // Use the state variable to determine if the MediaController and songlist are initialized
    val isMediaControllerInitialized by mediaControllerViewModel.isMediaControllerInitialized.collectAsState()

    if (isMediaControllerInitialized) {
        //observe the isPlaying state for ui changes
        val isPlaying by mediaControllerViewModel.isPlaying.collectAsState()
        //observe the shuffleModeEnabled state for ui changes
        val shuffleModeEnabled by mediaControllerViewModel.shuffleModeEnabled.collectAsState()
        //observe the loopMode state for ui changes
        val repeatMode by mediaControllerViewModel.repeatMode.collectAsState()

        val playbackPosition by mediaControllerViewModel.playbackPosition.collectAsState()
        val playbackDuration by mediaControllerViewModel.playbackDuration.collectAsState()

        //get the mediaController itself
        val mediaController = mediaControllerViewModel.mediaController

        //bottomsheet stuff
        val sheetState = rememberBottomSheetScaffoldState()
        val songList by mediaControllerViewModel.currentPlaylist.collectAsState()

        BottomSheetScaffold(
            scaffoldState = sheetState,
            sheetPeekHeight = 40.dp,
            sheetShape = CutCornerShape(topStart = 16.dp, topEnd = 16.dp),
            sheetContent = {
                SongScreen(songList = songList, showQueue = true)
            }
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
                            mediaControllerViewModel.setPlaybackPosition(
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
                        .border(2.dp, Color.Gray)
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
            Text("$formattedPosition/$formattedDuration")
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
                onValueChangeFinished = onValueChangeFinished
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongScreen(
    songList: List<Song> = emptyList(),
    showQueue: Boolean = false,
    mediaPlayerViewModel: MusicPlayerViewModel = hiltViewModel(),
    tagViewModel: TagViewModel = hiltViewModel()
) {
    val showTagDialog = remember { mutableStateOf(false) }
    val songToTag = remember { mutableStateOf<Song?>(null) }
    var onTagClick by remember { mutableStateOf<(TagDTO) -> Unit>({}) }
    var tagCardComposable by remember { mutableStateOf<@Composable (tag: TagDTO) -> Unit>({}) }
    val coroutineScope = rememberCoroutineScope()

    val includedTags by mediaPlayerViewModel.includedTags.collectAsState()
    val excludedTags by mediaPlayerViewModel.excludedTags.collectAsState()

    if (showTagDialog.value) {
        Dialog(
            onDismissRequest = {
                showTagDialog.value = false
            },
        ) {
            Card(
                shape = CutCornerShape(0.dp),
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 16.dp)
                    .fillMaxSize()
                    .border(2.dp, Color.Gray)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    TagList(
                        tagCard = tagCardComposable,
                        floatingActionButton = {}
                    )


                    TagCapellaButton(
                        onClick = {
                            coroutineScope.launch {
                                val filteredSongList = mediaPlayerViewModel.getFilteredPlaylist(
                                    includedTags,
                                    excludedTags
                                )
                                mediaPlayerViewModel.preparePlaylist(filteredSongList)
                                showTagDialog.value = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .height(36.dp)
                    ) {
                        Text(text = "Save")
                    }
                }
            }
        }
    }

    SongList(
        songList = songList,
        songCard = { song ->
            SongCard(
                song = song,
                tagCallBack = {
                    //todo extract this copy and pasted code into a variable or something
                    onTagClick = { tag ->
                        if (songToTag.value!!.songTagList.contains(tag)) {
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
                        if (tag.tag != "All") {
                            TagCard(
                                tag = tag,
                                onClick = onTagClick,
                                backgroundColor =
                                try {
                                    //todo don't rely on try catching here, make this nicer later on
                                    if (songToTag.value!!.songTagList.contains(tag)) {
                                        Color.hsl(112f, 0.5f, 0.3f)
                                    } else {
                                        Color.Black
                                    }
                                } catch (e: Exception) {
                                    Color.Black
                                },
                            )
                        }
                    }

                    showTagDialog.value = true
                    songToTag.value = song
                },
                onClick = {

                    tagCardComposable = { tag ->
                        if (tag.tag != "All") {
                            TagCard(
                                tag = tag,
                                onClick = onTagClick,
                                backgroundColor =
                                try {
                                    //todo don't rely on try catching here, make this nicer later on
                                    if (songToTag.value!!.songTagList.contains(tag)) {
                                        Color.hsl(112f, 0.5f, 0.3f)
                                    } else {
                                        Color.Black
                                    }
                                } catch (e: Exception) {
                                    Color.Black
                                },
                            )
                        }
                    }

                    //todo maybe find a way to make this nicer I guess.
                    if (showQueue) {
                        val index = mediaPlayerViewModel.currentPlaylist.value.indexOf(song)
                        if (index >= 0) {
                            mediaPlayerViewModel.mediaController.seekTo(index, C.TIME_UNSET)
                        }
                    } else {
                        //todo extract this copy and pasted code into a variable or something
                        onTagClick = { tag ->
                            if (songToTag.value!!.songTagList.contains(tag)) {
                                songToTag.value!!.path.let {
                                    tagViewModel.deleteSongTag(tag, songToTag.value!!)
                                }
                            } else {
                                songToTag.value!!.path.let {
                                    tagViewModel.addSongTag(tag, songToTag.value!!)
                                }
                            }
                        }

                        showTagDialog.value = true
                        songToTag.value = song
                    }
                }
            )
        },
        floatingActionButton = {
            if (showQueue) {
                SmallFloatingActionButton(onClick = {
                    tagCardComposable = { tag ->
                        if (tag.tag != "All") {
                            TagCard(
                                tag = tag,
                                onClick = onTagClick,
                                backgroundColor =
                                if (includedTags.contains(tag)) {
                                    Color.hsl(112f, 0.5f, 0.3f)
                                } else if (excludedTags.contains(tag)) {
                                    Color.Red
                                } else {
                                    Color.Black
                                },
                            )
                        }
                    }
                    showTagDialog.value = true
                    onTagClick = { tag ->
                        if (includedTags.contains(tag)) {
                            mediaPlayerViewModel.removeIncludedTag(tag)
                            mediaPlayerViewModel.addExcludedTag(tag)
                        } else if (excludedTags.contains(tag)) {
                            mediaPlayerViewModel.removeExcludedTag(tag)
                        } else {
                            mediaPlayerViewModel.addIncludedTag(tag)
                        }
                    }
                }) {
                    Icon(Icons.Default.Queue, contentDescription = "New queue")
                }
            }
        }
    )
}

@Composable
fun SongList(
    modifier: Modifier = Modifier,
    songList: List<Song> = emptyList(),
    floatingActionButton: @Composable () -> Unit = {},
    songCard: @Composable (song: Song) -> Unit,
    mediaController: MusicPlayerViewModel = hiltViewModel(),
    songViewModel: SongViewModel = hiltViewModel()
) {

    // Use the state variable to determine if the MediaController and song list are initialized
    val isMediaControllerInitialized by mediaController.isMediaControllerInitialized.collectAsState()
    val isSongListInitialized by songViewModel.isInitialized.collectAsState()

    Scaffold(
        floatingActionButton = floatingActionButton
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current)
                )
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
}

@Composable
fun SongCard(
    song: Song,
    tagCallBack: (() -> Unit)? = null,
    onClick: () -> Unit = {},
    backgroundColor: Color = Color.Black
) {
    val scroll = rememberScrollState(0)
    val songTagCount by song.songTagCount

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
            .height(50.dp),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, Color.Red, shape = RoundedCornerShape(8.dp))
                .background(backgroundColor)
                .padding(horizontal = 8.dp)
        ) {
            Icon(
                Icons.Rounded.PlayArrow,
                contentDescription = null
            )

            if (tagCallBack != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = tagCallBack
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Label,
                            contentDescription = "Add tags",
                        )
                    }
                    Text(
                        "($songTagCount)",
                    )
                }
            }
            Text(
                mediaItem.mediaMetadata.title.toString(),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scroll)
            )
        }
    }
}
