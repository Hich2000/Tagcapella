package com.hich2000.tagcapella.music.playerScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.hich2000.tagcapella.music.playerScreen.controls.Controls
import com.hich2000.tagcapella.music.playerScreen.controls.ProgressSlider
import com.hich2000.tagcapella.music.playerScreen.queue.Queue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    playerScreenViewModel: PlayerScreenViewModel = hiltViewModel()
) {
    val playerState by playerScreenViewModel.playerState.collectAsState()
    var isUserInteracting by remember { mutableStateOf(false) }
    var tempSliderPosition by remember { mutableFloatStateOf(0F) }

    BottomSheetScaffold(
        sheetContent = { Queue() },
    ) { innerPadding ->
        Column(
            modifier = Modifier.Companion
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center
        ) {
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
        }
    }
}