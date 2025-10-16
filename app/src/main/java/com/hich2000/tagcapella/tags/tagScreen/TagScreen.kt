package com.hich2000.tagcapella.tags.tagScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.hich2000.tagcapella.music_player.SongCard
import com.hich2000.tagcapella.music_player.SongList
import com.hich2000.tagcapella.tags.tagList.TagCard
import com.hich2000.tagcapella.tags.tagList.TagForm
import com.hich2000.tagcapella.tags.tagList.TagList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagScreen(
    tagScreenViewModel: TagScreenViewModel = hiltViewModel()
) {
    val showTagDialog by tagScreenViewModel.showDialog.collectAsState()
    val clickedTag by tagScreenViewModel.clickedTag.collectAsState()
    val showSongDialog = remember { mutableStateOf(false) }
    val songList by tagScreenViewModel.songs.collectAsState()
    val tagList by tagScreenViewModel.tags.collectAsState()

    if (showTagDialog) {
        BasicAlertDialog(
            onDismissRequest = {
                tagScreenViewModel.closeDialog()
                tagScreenViewModel.setClickedTag(null)
            },
        ) {
            TagForm(
                tag = clickedTag
            )
        }
    }

    if (showSongDialog.value) {
        BasicAlertDialog(
            onDismissRequest = {
                showSongDialog.value = false
                tagScreenViewModel.setClickedTag(null)
            },
        ) {
            SongList(
                songList = songList,
                songCard = { song ->
                    val songTags by song.songTagList.collectAsState()
                    val isTagged: Boolean = songTags.any { songTag ->
                        songTag.id == clickedTag?.id
                    }

                    SongCard(
                        song = song,
                        backgroundColor = if (isTagged) {
                            Color.Companion.hsl(112f, 0.5f, 0.3f)
                        } else {
                            MaterialTheme.colorScheme.background
                        },
                        onClick = {
                            if (isTagged) {
                                tagScreenViewModel.deleteSongTag(clickedTag!!, song)
                            } else {
                                tagScreenViewModel.addSongTag(clickedTag!!, song)
                            }
                        }
                    )
                }
            )
        }
    }

    Box(
        modifier = Modifier.Companion.fillMaxSize()
    ) {
        TagList(
            tagList = tagList,
            tagCard = { tag ->
                val editCallback = {
                    tagScreenViewModel.setClickedTag(tag)
                    tagScreenViewModel.openDialog()
                }
                val songCallback = {
                    tagScreenViewModel.setClickedTag(tag)
                    showSongDialog.value = true
                }
                val deleteCallback = {
                    tagScreenViewModel.deleteTag(tag.id)
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