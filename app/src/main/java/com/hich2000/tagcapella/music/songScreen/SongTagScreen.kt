package com.hich2000.tagcapella.music.songScreen

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
import com.hich2000.tagcapella.tagsAndCategories.tags.Tag
import com.hich2000.tagcapella.tagsAndCategories.tags.tagList.TagCard
import com.hich2000.tagcapella.tagsAndCategories.tags.tagList.TagList

@Composable
fun SongTagScreen(
    songPath: String,
    songTagScreenViewModel: SongTagScreenViewModel = hiltViewModel()
) {
    val tagList by songTagScreenViewModel.tags.collectAsState()
    val songToTag = songTagScreenViewModel.getSong(songPath)

    val onTagClick = { tag: Tag ->
        if (songToTag.tags.find { songTag: Tag ->
                tag.id == songTag.id
            } !== null) {
            songTagScreenViewModel.deleteSongTag(songToTag, tag)
        } else {
            songTagScreenViewModel.addSongTag(songToTag, tag)
        }
    }

    val tagCardComposable = @Composable { tag: Tag ->
        val isTagged = songToTag.tags.find { songTag: Tag ->
            tag.id == songTag.id
        } !== null

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

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        TagList(
            tagList = tagList,
            tagCard = tagCardComposable,
        )
    }
}