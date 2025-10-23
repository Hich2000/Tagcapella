package com.hich2000.tagcapella.newmusic.playerScreen.controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun ProgressSlider(
    playbackPosition: Long,
    playbackDuration: Long,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit
) {
    val pHours = TimeUnit.MILLISECONDS.toHours(playbackPosition)
    val pMinutes = TimeUnit.MILLISECONDS.toMinutes(playbackPosition) % 60
    val pSeconds = TimeUnit.MILLISECONDS.toSeconds(playbackPosition) % 60
    val formattedPosition = if (pHours > 0) {
        String.format(Locale.ROOT, "%d:%02d:%02d", pHours, pMinutes, pSeconds)
    } else {
        String.format(Locale.ROOT, "%02d:%02d", pMinutes, pSeconds)
    }

    //formatted duration needs to only trigger when duration has actually propagated and is not negative
    var formattedDuration = "--:--"
    if (playbackDuration > 0) {
        val dHours = TimeUnit.MILLISECONDS.toHours(playbackDuration)
        val dMinutes = TimeUnit.MILLISECONDS.toMinutes(playbackDuration) % 60
        val dSeconds = TimeUnit.MILLISECONDS.toSeconds(playbackDuration) % 60
        formattedDuration = if (pHours > 0) {
            String.format(Locale.ROOT, "%d:%02d:%02d", dHours, dMinutes, dSeconds)
        } else {
            String.format(Locale.ROOT, "%02d:%02d", dMinutes, dSeconds)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                "$formattedPosition/$formattedDuration",
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Row(
            modifier = Modifier.Companion.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Slider(
                value = playbackPosition.toFloat(),
                valueRange = if (playbackDuration > 0) {
                    (0f..playbackDuration.toFloat())
                } else {
                    (0f..1f)
                },
                onValueChange = onValueChange,
                onValueChangeFinished = onValueChangeFinished,
                colors = SliderColors(
                    thumbColor = MaterialTheme.colorScheme.secondary,
                    activeTrackColor = MaterialTheme.colorScheme.secondary,
                    activeTickColor = Color.Companion.Unspecified,
                    inactiveTrackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                    inactiveTickColor = Color.Companion.Unspecified,
                    disabledThumbColor = Color.Companion.Unspecified,
                    disabledActiveTrackColor = Color.Companion.Unspecified,
                    disabledActiveTickColor = Color.Companion.Unspecified,
                    disabledInactiveTrackColor = Color.Companion.Unspecified,
                    disabledInactiveTickColor = Color.Companion.Unspecified,
                )
            )
        }
    }
}