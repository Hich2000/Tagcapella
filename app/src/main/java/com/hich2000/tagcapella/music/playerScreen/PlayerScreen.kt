package com.hich2000.tagcapella.music.playerScreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hich2000.tagcapella.main.navigation.LocalNavController
import com.hich2000.tagcapella.main.navigation.Route
import com.hich2000.tagcapella.music.playerScreen.controls.Controls
import com.hich2000.tagcapella.music.playerScreen.controls.ProgressSlider
import com.hich2000.tagcapella.music.playerScreen.queue.Queue
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    playerScreenViewModel: PlayerScreenViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val playerState by playerScreenViewModel.playerState.collectAsState()
    var isUserInteracting by remember { mutableStateOf(false) }
    var tempSliderPosition by remember { mutableFloatStateOf(0F) }
    val bottomSheetState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

    BackHandler(enabled = bottomSheetState.bottomSheetState.currentValue == SheetValue.Expanded) {
        scope.launch {
            bottomSheetState.bottomSheetState.partialExpand()
        }
    }

    BottomSheetScaffold(
        scaffoldState = bottomSheetState,
        sheetContent = { Queue() },
        sheetPeekHeight = 48.dp,
        sheetContainerColor = MaterialTheme.colorScheme.tertiary,
        sheetShape = CutCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        )
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding(),
                    start = 16.dp,
                    end = 16.dp
                ),
            verticalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = {},
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
                    .background(MaterialTheme.colorScheme.tertiary)
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "icon",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Text(
                text = playerState.currentSong.substringAfterLast('/'),
                textAlign = TextAlign.Center,
                softWrap = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .basicMarquee(
                        velocity = 64.dp
                    )
            )
            ProgressSlider(
                playbackPosition = if (!isUserInteracting) playerState.position else tempSliderPosition.toLong(),
                playbackDuration = playerState.duration,
                onValueChange = { newPosition: Float ->
                    isUserInteracting = true
                    tempSliderPosition = newPosition
                },
                onValueChangeFinished = {
                    isUserInteracting = false
                    playerState.position = tempSliderPosition.toLong()
                    playerScreenViewModel.seek(tempSliderPosition.toLong())
                }
            )
            Controls(
                playerState = playerState,
                pausePlay = { playerScreenViewModel.pausePlay() },
                seekToNext = { playerScreenViewModel.next() },
                seekToPrevious = { playerScreenViewModel.previous() },
                shuffleModeEnabled = { playerScreenViewModel.shuffleMode() },
                repeatMode = { playerScreenViewModel.loopMode() }
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = {
                        navController.navigate(Route.Songs.Tags.createRoute(playerState.currentSong))
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Label,
                        contentDescription = "Tags",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}