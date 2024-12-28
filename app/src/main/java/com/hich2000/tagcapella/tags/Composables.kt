package com.hich2000.tagcapella.tags

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hich2000.tagcapella.music_player.SongCard
import com.hich2000.tagcapella.music_player.SongList
import com.hich2000.tagcapella.music_player.SongRepository


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TagList(
    tagCard: @Composable (tag: TagDTO) -> Unit,
    floatingActionButton: @Composable () -> Unit,
    tagViewModel: TagViewModel = hiltViewModel()
) {
    val tagList = remember { tagViewModel.tags }
    val columnScroll = rememberScrollState()

    Scaffold(
        floatingActionButton = floatingActionButton
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                .verticalScroll(columnScroll)
        ) {
            tagList.forEach { tag ->
                tagCard(tag)
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
            .height(75.dp),
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
        Column(modifier = Modifier.padding(16.dp)) {
            TextField(
                value = textState,
                onValueChange = { textState = it },
                label = { Text("Tag") },
            )

            if (tag === null) {
                Button(
                    onClick = {
                        tagViewModel.insertTag(textState)
                    },
                ) {
                    Text("add")
                }
            } else {
                Button(
                    onClick = {
                        tagViewModel.updateTag(
                            id = tag.id,
                            tag = textState
                        )
                    },
                ) {
                    Text("update")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagScreen(
    songRepository: SongRepository,
    tagViewModel: TagViewModel = hiltViewModel()
) {
    val showEditDialog = remember { mutableStateOf(false) }
    val clickedTag = remember { mutableStateOf<TagDTO?>(null) }

    val showSongDialog = remember { mutableStateOf(false) }

    if (showEditDialog.value) {
        BasicAlertDialog(
            onDismissRequest = {
                showEditDialog.value = false
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
                songList = songRepository.songList,
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
                                song.id?.let {
                                    tagViewModel.deleteSongTag(clickedTag.value!!, song)
                                }
                            } else {
                                song.id?.let {
                                    tagViewModel.addSongTag(clickedTag.value!!, song)
                                }
                            }
                        }
                    )
                }
            )
        }
    }

    TagList(
        tagCard = { tag ->
            var editCallback: (() -> Unit)? = null
            var songCallback: (() -> Unit)? = null
            var deleteCallback: (() -> Unit)? = null

            if (tag.tag != "All") {
                editCallback = {
                    clickedTag.value = tag
                    showEditDialog.value = true
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
        },
        floatingActionButton = {
            SmallFloatingActionButton(onClick = {
                showEditDialog.value = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add label")
            }
        }
    )
}
