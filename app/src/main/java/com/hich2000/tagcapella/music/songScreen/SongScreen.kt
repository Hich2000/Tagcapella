package com.hich2000.tagcapella.music.songScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.hich2000.tagcapella.music.MusicPlayerViewModel
import com.hich2000.tagcapella.tagsAndCategories.tags.TagDTO
import com.hich2000.tagcapella.tagsAndCategories.tags.dialogs.TagDialog
import com.hich2000.tagcapella.tagsAndCategories.tags.tagList.TagCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongScreen(
    mediaPlayerViewModel: MusicPlayerViewModel = hiltViewModel(),
    songScreenViewModel: SongScreenViewModel = hiltViewModel()
) {

    val coroutineScope = rememberCoroutineScope()


    val songListInitialized by songScreenViewModel.songRepoInitialized.collectAsState()
    val showTagDialog by songScreenViewModel.showTagDialog.collectAsState()
    val songList by songScreenViewModel.songs.collectAsState()
    val songTags by songScreenViewModel.songTags.collectAsState()
    val includedTags by songScreenViewModel.includedTags.collectAsState()
    val excludedTags by songScreenViewModel.excludedTags.collectAsState()


    val onTagClick = { tag: TagDTO ->
        //todo does not work, always goes to else
        if (songTags.find { songTag: TagDTO ->
                tag.id == songTag.id
            } !== null) {
            songScreenViewModel.deleteSongTag(tag)
        } else {
            songScreenViewModel.addSongTag(tag)
        }
    }

    val tagCardComposable = @Composable { tag: TagDTO ->
        val isTagged = songTags.find { songTag: TagDTO ->
            tag.id == songTag.id
        } !== null

        TagCard(
            tag = tag,
            onClick = onTagClick,
            backgroundColor =
                if (isTagged) {
                    Color.Companion.hsl(112f, 0.5f, 0.3f)
                } else {
                    MaterialTheme.colorScheme.background
                },
        )
    }

    if (showTagDialog) {
        TagDialog(
            onButtonPress = {
                coroutineScope.launch {
                    val filteredSongList = mediaPlayerViewModel.getFilteredPlaylist(
                        includedTags,
                        excludedTags
                    )
                    mediaPlayerViewModel.preparePlaylist(filteredSongList)
                    songScreenViewModel.closeDialog()
                }
            },
            tagCardComposable = tagCardComposable
        )
    }

    if (!songListInitialized) {
        CircularProgressIndicator(
            modifier = Modifier.Companion.fillMaxSize()
        )
        return
    }

    SongList(
        songList = songList,
        songCard = { song ->
            SongCard(
                song = song,
                tagCallBack = { songScreenViewModel.openDialog(song) },
                onClick = { songScreenViewModel.openDialog(song) }
            )
        },
        floatingActionButton = {}
    )
}