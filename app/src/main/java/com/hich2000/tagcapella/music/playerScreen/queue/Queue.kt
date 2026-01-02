package com.hich2000.tagcapella.music.playerScreen.queue

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Queue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hich2000.tagcapella.R
import com.hich2000.tagcapella.main.navigation.LocalNavController
import com.hich2000.tagcapella.main.navigation.Route
import com.hich2000.tagcapella.music.songScreen.SongCard
import com.hich2000.tagcapella.music.songScreen.SongList
import kotlinx.coroutines.launch
import kotlin.math.sin

@Composable
fun Queue(
    queueViewModel: QueueViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val queue by queueViewModel.currentQueue.collectAsState()
    val playerState by queueViewModel.playerState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Route.Player.QueueBuilder.route)
                },
                shape = RectangleShape,
                containerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier.border(2.dp, MaterialTheme.colorScheme.tertiary)
            ) {
                Icon(
                    imageVector = Icons.Default.Queue,
                    contentDescription = "filter queue",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        SongList(
            songList = queue,
            modifier = Modifier.padding(innerPadding)
        ) { song ->
            if (playerState.currentSong == song.path) {
                Box(
                    contentAlignment = Alignment.Center,
                ) {
                    SongCard(
                        song = song,
                        onClick = { queueViewModel.seek(song) }
                    )
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.soundwave),
                            contentDescription = "Sound wave",
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }

                    val waveColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f)
                    val phase = remember { Animatable(0f) }
                    val scope = rememberCoroutineScope()

                    LaunchedEffect(playerState.isPlaying,) {
                        if (playerState.isPlaying) {
                            scope.launch {
                                while (true) {
                                    phase.animateTo(
                                        targetValue = (2 * Math.PI).toFloat(),
                                        animationSpec = tween(
                                            durationMillis = 2000,
                                            easing = LinearEasing
                                        )
                                    )
                                    phase.snapTo(0f)
                                }
                            }
                        } else {
                            phase.stop()
                        }
                    }

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        val wavePath = Path()
                        val amplitude = 25f
                        val baseline = size.height+150f
                        val waveCenter = size.height+50f

                        wavePath.moveTo(0f, baseline)
                        for (x in 0..size.width.toInt()) {
                            val radians = (x / size.width) * (2 * Math.PI) + phase.value
                            val y = waveCenter + amplitude * sin(radians).toFloat()
                            wavePath.lineTo(x.toFloat(), y)
                        }

                        wavePath.lineTo(size.width, baseline)
                        wavePath.close()
                        drawPath(
                            path = wavePath,
                            color = waveColor
                        )

                        drawPath(
                            path = wavePath,
                            color = waveColor,
                            style = Stroke(4f)
                        )
                    }
                }
            } else {
                SongCard(
                    song = song,
                    onClick = { queueViewModel.seek(song) }
                )
            }
        }
    }
}