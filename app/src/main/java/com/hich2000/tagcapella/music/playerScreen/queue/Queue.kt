package com.hich2000.tagcapella.music.playerScreen.queue

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Queue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hich2000.tagcapella.music.songScreen.SongCard
import com.hich2000.tagcapella.music.songScreen.SongList
import com.hich2000.tagcapella.tagsAndCategories.tags.dialogs.TagDialog
import com.hich2000.tagcapella.tagsAndCategories.tags.tagList.TagCard

@Composable
fun Queue(
    queueViewModel: QueueViewModel = hiltViewModel()
) {
    val queue by queueViewModel.currentQueue.collectAsState()
    val showDialog by queueViewModel.showDialog.collectAsState()
    val includedTags by queueViewModel.includedTags.collectAsState()
    val excludedTags by queueViewModel.excludedTags.collectAsState()

    if (showDialog) {
        TagDialog(
            onButtonPress = {
                queueViewModel.updateQueue()
                queueViewModel.closeDialog()
            },
            onDismissRequest = { queueViewModel.closeDialog() },
            tagCardComposable = { tag ->
                TagCard(
                    tag = tag,
                    onClick = { queueViewModel.toggleTagFilter(tag) },
                    backgroundColor = if (includedTags.contains(tag)) {
                        Color.Green
                    } else if (excludedTags.contains(tag)) {
                        Color.Red
                    } else {
                        MaterialTheme.colorScheme.background
                    }
                )
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    queueViewModel.openDialog()
                },
                shape = RectangleShape,
                containerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier.border(2.dp, MaterialTheme.colorScheme.tertiary)
            ) {
                Icon(
                    imageVector = Icons.Default.Queue,
                    contentDescription = "filter queue",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    ) { innerPadding ->
        SongList(
            modifier = Modifier.padding(innerPadding),
            songList = queue
        ) { song ->
            SongCard(
                song = song,
                onClick = { queueViewModel.seek(song) }
            )
        }
    }
}