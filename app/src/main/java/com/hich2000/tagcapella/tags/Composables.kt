package com.hich2000.tagcapella.tags

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hich2000.tagcapella.music_player.SongCard
import com.hich2000.tagcapella.music_player.SongList
import com.hich2000.tagcapella.songs.SongViewModel
import com.hich2000.tagcapella.utils.TagCapellaButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagScreen(
    songViewModel: SongViewModel = hiltViewModel(),
    tagViewModel: TagViewModel = hiltViewModel(),
) {
    val showTagDialog = remember { mutableStateOf(false) }
    val clickedTag = remember { mutableStateOf<TagDTO?>(null) }
    val showSongDialog = remember { mutableStateOf(false) }
    val songList by songViewModel.songList.collectAsState()

    if (showTagDialog.value) {
        BasicAlertDialog(
            onDismissRequest = {
                showTagDialog.value = false
                clickedTag.value = null
            },
        ) {
            TagForm(
                tag = clickedTag.value,
                tagViewModel = tagViewModel
            )
        }
    }

    if (showSongDialog.value) {
        BasicAlertDialog(
            onDismissRequest = {
                showSongDialog.value = false
                clickedTag.value = null
            },
        ) {
            SongList(
                songList = songList,
                songCard = { song ->
                    SongCard(
                        song = song,
                        backgroundColor = if (clickedTag.value!!.taggedSongList.contains(song)) {
                            Color.hsl(112f, 0.5f, 0.3f)
                        } else {
                            Color.Black
                        },
                        onClick = {
                            if (clickedTag.value!!.taggedSongList.contains(song)) {
                                song.path.let {
                                    tagViewModel.deleteSongTag(clickedTag.value!!, song)
                                }
                            } else {
                                song.path.let {
                                    tagViewModel.addSongTag(clickedTag.value!!, song)
                                }
                            }
                        }
                    )
                }
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        TagList(
            tagCard = { tag ->
                var editCallback: (() -> Unit)? = null
                var songCallback: (() -> Unit)? = null
                var deleteCallback: (() -> Unit)? = null

                if (tag.tag != "All") {
                    editCallback = {
                        clickedTag.value = tag
                        showTagDialog.value = true
                    }
                    songCallback = {
                        clickedTag.value = tag
                        showSongDialog.value = true
                    }
                    deleteCallback = {
                        tagViewModel.deleteTag(tag.id)
                    }
                }

                TagCard(
                    tag = tag,
                    editCallback = editCallback,
                    songCallback = songCallback,
                    deleteCallback = deleteCallback
                )
            }
        )
    }
}

@Composable
fun TagList(
    tagCard: @Composable (tag: TagDTO) -> Unit,
    tagViewModel: TagViewModel = hiltViewModel()
) {
    val tagList by tagViewModel.tags.collectAsState()
    val columnScroll = rememberScrollState()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current)
                )
                .fillMaxSize()
//                .border(2.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                .verticalScroll(columnScroll)
        ) {
            tagList.forEach { tag ->
                tagCard(tag)
            }
        }
    }
}

@Composable
fun ExpandableFab(
    buttons: List<@Composable () -> Unit>,
    expanded: Boolean = false,
    onclick: (() -> Unit)
) {
    Box(
        modifier = Modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        AnimatedContent(
            targetState = expanded,
            transitionSpec = {
                fadeIn(tween(200)) + scaleIn(tween(200)) togetherWith
                        fadeOut(tween(200)) + scaleOut(tween(200))
            },
            label = "FAB Expansion"
        ) { expanded ->
            if (expanded) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .border(2.dp, Color.Gray)
                        .padding(8.dp)
                        .width(200.dp)
                ) {
                    buttons.forEach { button ->
                        button()
                    }
                }
            } else {
                FloatingActionButton(
                    onClick = onclick,
                    containerColor = Color.Black,
                    modifier = Modifier
                        .padding(16.dp)
                        .border(2.dp, Color.Gray)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Expand")
                }
            }
        }
    }
}

@Composable
fun TagCard(
    tag: TagDTO,
    editCallback: (() -> Unit)? = null,
    songCallback: (() -> Unit)? = null,
    deleteCallback: (() -> Unit)? = null,
    onClick: (tag: TagDTO) -> Unit = {},
    backgroundColor: Color = Color.Black
) {

    val taggedSongCount by tag.taggedSongCount

    Card(
        modifier = Modifier
            .border(2.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .background(Color.Gray)
            .height(50.dp),
        onClick = { onClick(tag) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, Color.Red, shape = RoundedCornerShape(8.dp))
                .background(backgroundColor)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Label, contentDescription = "Label"
            )
            Text(
                tag.tag,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )

            if (deleteCallback != null) {
                IconButton(
                    onClick = deleteCallback
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
            }
            if (editCallback != null) {
                IconButton(
                    onClick = editCallback
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit"
                    )
                }
            }
            if (songCallback != null) {
                IconButton(
                    onClick = songCallback
                ) {
                    Icon(
                        Icons.Default.MusicNote,
                        contentDescription = "Tag songs"
                    )
                }
                Text("($taggedSongCount)")
            }
        }
    }
}


@Composable
fun TagForm(tag: TagDTO? = null, tagViewModel: TagViewModel) {
    var textState by remember { mutableStateOf(if (tag is TagDTO) tag.tag else "") }

    Surface(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.large,
        tonalElevation = AlertDialogDefaults.TonalElevation
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .border(2.dp, Color.Gray)
        ) {
            TextField(
                value = textState,
                onValueChange = { textState = it },
                label = { Text("Tag") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp)
            )

            if (tag === null) {
                TagCapellaButton(
                    onClick = {
                        tagViewModel.insertTag(textState)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .height(36.dp)
                ) {
                    Text("add")
                }
            } else {
                TagCapellaButton(
                    onClick = {
                        tagViewModel.updateTag(
                            id = tag.id,
                            tag = textState
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .height(36.dp)
                ) {
                    Text("update")
                }
            }
        }
    }
}

