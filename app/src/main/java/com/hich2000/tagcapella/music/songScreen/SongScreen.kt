package com.hich2000.tagcapella.music.songScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hich2000.tagcapella.main.navigation.LocalNavController
import com.hich2000.tagcapella.main.navigation.Route
import com.hich2000.tagcapella.tagsAndCategories.tags.Tag
import com.hich2000.tagcapella.tagsAndCategories.tags.dialogs.TagDialog
import com.hich2000.tagcapella.tagsAndCategories.tags.tagList.TagCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongScreen(
    songScreenViewModel: SongScreenViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val songListInitialized by songScreenViewModel.songRepoInitialized.collectAsState()
    val showTagDialog by songScreenViewModel.showTagDialog.collectAsState()
    val songList by songScreenViewModel.songs.collectAsState()
    val songToTag by songScreenViewModel.songToTag.collectAsState()

    val onTagClick = { tag: Tag ->
        if (songToTag?.tags?.find { songTag: Tag ->
                tag.id == songTag.id
            } !== null) {
            songScreenViewModel.deleteSongTag(tag)
        } else {
            songScreenViewModel.addSongTag(tag)
        }
    }

    val tagCardComposable = @Composable { tag: Tag ->
        val isTagged = songToTag?.tags?.find { songTag: Tag ->
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

    if (showTagDialog) {
        TagDialog(
            onButtonPress = {
                songScreenViewModel.closeDialog()
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
                showTagCount = true,
//                onClick = { songScreenViewModel.openDialog(song) }
                onClick = { navController.navigate(Route.Songs.Tags.createRoute(song.path)) }
            )
        }
    )
}