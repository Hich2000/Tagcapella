package com.hich2000.tagcapella

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.media3.common.Player
import com.hich2000.tagcapella.music_player.MusicPlayerViewModel
import com.hich2000.tagcapella.theme.TagcapellaTheme

class MainActivity : ComponentActivity() {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        requestPermissions()

        setContent {
            TagcapellaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        TagcapellaApp()
                    }
                }
            }
        }
    }


    private fun requestPermissions() {
        // Initialize the permission launcher
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(this, "App required media permissions.", Toast.LENGTH_SHORT).show()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if the permission is already granted
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_MEDIA_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }


    @Composable
    fun TagcapellaApp() {

        val context = LocalContext.current
        val mediaViewModel = MusicPlayerViewModel(context.applicationContext as Application)

        // Use the state variable to determine if the MediaController is initialized
        val isMediaControllerInitialized by mediaViewModel.isMediaControllerInitialized

        CompositionLocalProvider(LocalMusicPlayerViewModel provides mediaViewModel) {
            if (isMediaControllerInitialized) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        MusicControls()
                    }
                ) { innerPadding ->
                    Text(
                        text = "Media Controller Initialized!",
                        modifier = Modifier
                            .padding(innerPadding)
                    )
                }
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun MusicControls() {
    //get the mediaController for controls
    val mediaController = LocalMusicPlayerViewModel.current.mediaController
    //observe the isPlaying state for ui changes
    val isPlaying by LocalMusicPlayerViewModel.current.isPlaying
    //observe the shuffleModeEnabled state for ui changes
    val shuffleModeEnabled by LocalMusicPlayerViewModel.current.shuffleModeEnabled
    //observe the loopMode state for ui changes
    val repeatMode by LocalMusicPlayerViewModel.current.repeatMode


    BottomAppBar(
        modifier = Modifier
            .border(2.dp, Color.Gray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, Color.Gray),
            horizontalArrangement = Arrangement.Center
        ) {
            //shuffle mode
            IconButton(
                onClick = {
                    if (shuffleModeEnabled) {
                        mediaController.shuffleModeEnabled = false
                    } else {
                        mediaController.shuffleModeEnabled = true
                    }
                }
            ) {
                val icon = if (shuffleModeEnabled) Icons.Default.ShuffleOn else Icons.Default.Shuffle
                Icon(
                    icon,
                    contentDescription = "Shuffle button"
                )
            }
            //skip previous
            IconButton(
                onClick = {
                    mediaController.seekToPrevious()
                }
            ) {
                Icon(
                    Icons.Default.SkipPrevious,
                    contentDescription = "Skip to previous button"
                )
            }
            //play/pause
            IconButton(
                onClick = {
                    if (isPlaying) mediaController.pause() else mediaController.play()
                }
            ) {
                val icon = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow
                val contentDescription = if (isPlaying) "Pause" else "Play"
                Icon(
                    icon,
                    contentDescription = contentDescription
                )
            }
            //skip next
            IconButton(
                onClick = {
                    mediaController.seekToNext()
                }
            ) {
                Icon(
                    Icons.Default.SkipNext,
                    contentDescription = "Skip to next button"
                )
            }
            //loop mode
            IconButton(
                onClick = {
                    if (repeatMode == Player.REPEAT_MODE_OFF) {
                        mediaController.repeatMode = Player.REPEAT_MODE_ALL
                    } else if (repeatMode == Player.REPEAT_MODE_ALL) {
                        mediaController.repeatMode = Player.REPEAT_MODE_ONE
                    } else if (repeatMode == Player.REPEAT_MODE_ONE) {
                        mediaController.repeatMode = Player.REPEAT_MODE_OFF
                    }
                }
            ) {
                var icon = Icons.Default.Repeat

                if (repeatMode == Player.REPEAT_MODE_OFF) {
                    icon = Icons.AutoMirrored.Filled.ArrowRightAlt
                } else if (repeatMode == Player.REPEAT_MODE_ALL) {
                    icon = Icons.Default.Repeat
                } else if (repeatMode == Player.REPEAT_MODE_ONE) {
                    icon = Icons.Default.RepeatOne
                }

                Icon(
                    icon,
                    contentDescription = "Shuffle button"
                )
            }

        }

    }
}