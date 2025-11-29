package com.hich2000.tagcapella.tagsAndCategories.tags.tagScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hich2000.tagcapella.music.songScreen.SongCard
import com.hich2000.tagcapella.music.songScreen.SongList

@Composable
fun TagSongScreen(
    tagId: Long,
    tagSongScreenViewModel: TagSongScreenViewModel = hiltViewModel()
) {
    val songList by tagSongScreenViewModel.songs.collectAsState()
    val selectedTag = tagSongScreenViewModel.getTag(tagId)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        SongList(
            songList = songList,
            songCard = { song ->
                val isTagged: Boolean = song.tags.any { songTag ->
                    songTag.id == selectedTag.id
                }

                SongCard(
                    song = song,
                    backgroundColor = if (isTagged) {
                        Color.hsl(112f, 0.5f, 0.3f)
                    } else {
                        MaterialTheme.colorScheme.background
                    },
                    onClick = {
                        if (isTagged) {
                            tagSongScreenViewModel.deleteSongTag(selectedTag, song)
                        } else {
                            tagSongScreenViewModel.addSongTag(selectedTag, song)
                        }
                    }
                )
            }
        )
    }
}