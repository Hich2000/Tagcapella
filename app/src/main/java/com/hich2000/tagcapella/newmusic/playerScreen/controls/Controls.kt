package com.hich2000.tagcapella.newmusic.playerScreen.controls

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.ShuffleOn
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import com.hich2000.tagcapella.newmusic.mediaController.PlayerState

@Composable
fun Controls(
    playerState: PlayerState,
    pausePlay: () -> Unit,
    seekToNext: () -> Unit,
    seekToPrevious: () -> Unit,
    shuffleModeEnabled: () -> Unit,
    repeatMode: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Companion.CenterVertically,
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(2.dp, MaterialTheme.colorScheme.tertiary)
    ) {
        //shuffle mode
        IconButton(
            onClick = { shuffleModeEnabled() }
        ) {
            val icon =
                if (playerState.shuffleModeEnabled) Icons.Default.ShuffleOn else Icons.Default.Shuffle
            Icon(
                icon,
                contentDescription = "Shuffle button",
                tint = MaterialTheme.colorScheme.secondary
            )
        }
        //skip previous
        IconButton(
            onClick = { seekToPrevious() }
        ) {
            Icon(
                Icons.Default.SkipPrevious,
                contentDescription = "Skip to previous button",
                tint = MaterialTheme.colorScheme.secondary
            )
        }
        //play/pause
        IconButton(
            onClick = { pausePlay() }
        ) {
            val icon = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow
            val contentDescription = if (playerState.isPlaying) "Pause" else "Play"
            Icon(
                icon,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.secondary
            )
        }
        //skip next
        IconButton(
            onClick = { seekToNext() }
        ) {
            Icon(
                Icons.Default.SkipNext,
                contentDescription = "Skip to next button",
                tint = MaterialTheme.colorScheme.secondary
            )
        }
        //loop mode
        IconButton(
            onClick = { repeatMode() }
        ) {
            var icon = Icons.Default.Repeat

            when (playerState.repeatMode) {
                Player.REPEAT_MODE_OFF -> {
                    icon = Icons.AutoMirrored.Filled.ArrowRightAlt
                }

                Player.REPEAT_MODE_ALL -> {
                    icon = Icons.Default.Repeat
                }

                Player.REPEAT_MODE_ONE -> {
                    icon = Icons.Default.RepeatOne
                }
            }

            Icon(
                icon,
                contentDescription = "Shuffle button",
                tint = MaterialTheme.colorScheme.secondary
            )
        }

    }
}