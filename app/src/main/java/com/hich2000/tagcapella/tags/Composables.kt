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
import com.hich200.tagcapella.Tag
import com.hich2000.tagcapella.music_player.SongList

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TagList(
    tagViewModel: TagViewModel = hiltViewModel()
) {

    val tags = remember { tagViewModel.tags }
    val columnScroll = rememberScrollState()
    val showEditDialog = remember { mutableStateOf(false) }
    val clickedTag = remember { mutableStateOf<Tag?>(null) }

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
                onSongClick = { song ->
                    clickedTag.value?.let {
                        song.id?.let { it1 ->
                            tagViewModel.addSongTag(it.id, it1)
                        }
                    }
                }
            )
        }
    }

    Scaffold(
        floatingActionButton = {
            SmallFloatingActionButton(onClick = {
                showEditDialog.value = true
            }) {
                Icon(
                    Icons.Default.Add, contentDescription = "Add label"
                )
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, Color.Blue, shape = RoundedCornerShape(8.dp))
                .verticalScroll(columnScroll)
        ) {
            tags.forEach {
                TagCard(
                    tag = it,
                    tagViewModel = tagViewModel,
                    editCallback = {
                        clickedTag.value = it
                        showEditDialog.value = true
                    },
                    songCallback = {
                        clickedTag.value = it
                        showSongDialog.value = true
                    }
                )
            }
        }
    }
}

@Composable
fun TagCard(
    tag: Tag,
    tagViewModel: TagViewModel,
    editCallback: () -> Unit = {},
    songCallback: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .border(2.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .background(Color.Gray)
            .height(75.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, Color.Red, shape = RoundedCornerShape(8.dp))
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

            if (tag.id.toInt() != 0) {
                IconButton(
                    onClick = {
                        tagViewModel.deleteTag(tag.id)
                    },
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
                IconButton(
                    onClick = editCallback
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit"
                    )
                }
                IconButton(
                    onClick = songCallback
                ) {
                    Icon(
                        Icons.Default.MusicNote,
                        contentDescription = "Tag songs"
                    )
                }
            }
        }
    }
}

@Composable
fun TagForm(tag: Tag? = null, tagViewModel: TagViewModel) {
    var textState by remember { mutableStateOf(if (tag is Tag) tag.tag else "") }

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