package com.hich2000.tagcapella.music_player

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import com.hich2000.tagcapella.NavItems
import com.hich2000.tagcapella.tags.TagCard
import com.hich2000.tagcapella.tags.TagDTO
import com.hich2000.tagcapella.tags.TagList
import com.hich2000.tagcapella.tags.TagViewModel
import kotlinx.coroutines.launch

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
    songList: SnapshotStateList<SongDTO> = SnapshotStateList(),
    screenType: NavItems,
    mediaPlayerViewModel: MusicPlayerViewModel = hiltViewModel(),
    tagViewModel: TagViewModel = hiltViewModel()
) {
    val showTagDialog = remember { mutableStateOf(false) }
    val songToTag = remember { mutableStateOf<SongDTO?>(null) }
    val includedTags = remember { mediaPlayerViewModel.includedTags }
    val excludedTags = remember { mediaPlayerViewModel.excludedTags }
    var onTagClick by remember { mutableStateOf<(TagDTO) -> Unit>({}) }
    var tagCardComposable by remember { mutableStateOf<@Composable (tag: TagDTO) -> Unit>({}) }
    val coroutineScope = rememberCoroutineScope()

    if (showTagDialog.value) {
        BasicAlertDialog(
            onDismissRequest = {
                showTagDialog.value = false
            }
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                val fraction = if (screenType == NavItems.Queue) 0.9f else 1f

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(fraction = fraction)
                ) {
                    TagList(
                        tagCard = tagCardComposable,
                        floatingActionButton = {}
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                if (screenType == NavItems.Queue) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val filteredSongList = mediaPlayerViewModel.getFilteredPlayList(
                                    includedTags,
                                    excludedTags
                                )
                                mediaPlayerViewModel.preparePlaylist(filteredSongList)
                                showTagDialog.value = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }

    SongList(
        list = songList,
        songCard = { song ->
            SongCard(
                song = song,
                tagCallBack = {
                    //todo extract this copy and pasted code into a variable or something
                    onTagClick = { tag ->
                        if (songToTag.value!!.songTagList.contains(tag)) {
                            songToTag.value!!.id?.let {
                                tagViewModel.deleteSongTag(tag, songToTag.value!!)
                            }
                        } else {
                            songToTag.value!!.id?.let {
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
                    if (screenType == NavItems.Queue) {
                        val index = mediaPlayerViewModel.currentPlaylist.indexOf(song)
                        if (index >= 0) {
                            mediaPlayerViewModel.mediaController.seekTo(index, C.TIME_UNSET)
                        }
                    } else if (screenType == NavItems.SongList) {
                        //todo extract this copy and pasted code into a variable or something
                        onTagClick = { tag ->
                            if (songToTag.value!!.songTagList.contains(tag)) {
                                songToTag.value!!.id?.let {
                                    tagViewModel.deleteSongTag(tag, songToTag.value!!)
                                }
                            } else {
                                songToTag.value!!.id?.let {
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
            if (screenType == NavItems.Queue) {
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
                            includedTags.remove(tag)
                            excludedTags.add(tag)
                        } else if (excludedTags.contains(tag)) {
                            excludedTags.remove(tag)
                        } else {
                            includedTags.add(tag)
                        }
                    }
                }) {
                    Icon(Icons.Default.Queue, contentDescription = "New queue")
                }
            }
        }
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SongList(
    modifier: Modifier = Modifier,
    list: SnapshotStateList<SongDTO> = SnapshotStateList(),
    floatingActionButton: @Composable () -> Unit = {},
    songCard: @Composable (song: SongDTO) -> Unit,
    mediaController: MusicPlayerViewModel = hiltViewModel(),
) {
    val songRepository = mediaController.songRepository

    // Use the state variable to determine if the MediaController and song list are initialized
    val isMediaControllerInitialized by mediaController.isMediaControllerInitialized
    val isSongListInitialized by songRepository.isInitialized.collectAsState()

    val songList = remember { list }

    Scaffold(
        floatingActionButton = floatingActionButton
    ) {
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
}

@Composable
fun SongCard(
    song: SongDTO,
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
            .height(75.dp),
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
