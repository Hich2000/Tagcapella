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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.hich2000.tagcapella.theme.TagcapellaTheme
import com.hich2000.tagcapella.music_player.MusicPlayerViewModel

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
            Scaffold(
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                if (isMediaControllerInitialized) {
                    Text(text = "Media Controller Initialized!")
                    MusicControls(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                } else {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun MusicControls(
    modifier: Modifier = Modifier,
) {
    val MusicPlayer = LocalMusicPlayerViewModel.current
    Box(
        modifier = modifier
    ) {
        IconButton(
            onClick = { MusicPlayer.mediaController.play() }
        ) {
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = "Play button"
            )
        }
    }
}