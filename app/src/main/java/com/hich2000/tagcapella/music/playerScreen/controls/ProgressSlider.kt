package com.hich2000.tagcapella.music.playerScreen.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
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

    val sliderColors = SliderColors(
        thumbColor = MaterialTheme.colorScheme.secondary,
        activeTrackColor = MaterialTheme.colorScheme.secondary,
        activeTickColor = Color.Red,
        inactiveTrackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
        inactiveTickColor = Color.Red,
        disabledThumbColor = Color.Red,
        disabledActiveTrackColor = Color.Red,
        disabledActiveTickColor = Color.Red,
        disabledInactiveTrackColor = Color.Red,
        disabledInactiveTickColor = Color.Red,
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formattedPosition,
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(0.15f)
            )
            Slider(
                value = playbackPosition.toFloat(),
                valueRange = if (playbackDuration > 0) {
                    (0f..playbackDuration.toFloat())
                } else {
                    (0f..1f)
                },
                onValueChange = onValueChange,
                onValueChangeFinished = onValueChangeFinished,
                colors = sliderColors,
                track = { sliderState ->
                    SliderDefaults.Track(
                        sliderState = sliderState,
                        colors = sliderColors,
                        drawStopIndicator = {},
                        thumbTrackGapSize = 0.dp,
                        modifier = Modifier
                            .height(4.dp)
                    )
                },
                thumb = {
                    Box(
                        modifier = Modifier
                            .offset(y = 2.dp)
                            .clip(CircleShape)
                            .size(12.dp)
                            .background(MaterialTheme.colorScheme.secondary, CircleShape)
                    )
                },
                modifier = Modifier.weight(1f)
            )
            Text(
                formattedDuration,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(0.15f)
            )
        }
    }
}