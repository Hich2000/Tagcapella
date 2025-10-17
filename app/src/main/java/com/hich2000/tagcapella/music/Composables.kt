package com.hich2000.tagcapella.music

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.hich2000.tagcapella.songs.Song
import com.hich2000.tagcapella.songs.SongViewModel
import com.hich2000.tagcapella.tags.tagList.TagCard
import com.hich2000.tagcapella.tags.TagDTO
import com.hich2000.tagcapella.tags.dialogs.TagDialog
import com.hich2000.tagcapella.tags.TagViewModel
import kotlinx.coroutines.launch
import kotlin.io.path.Path
import kotlin.io.path.nameWithoutExtension

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
