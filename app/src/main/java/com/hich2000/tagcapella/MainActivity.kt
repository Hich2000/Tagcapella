package com.hich2000.tagcapella

import android.Manifest
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hich2000.tagcapella.music_player.MusicControls
import com.hich2000.tagcapella.music_player.MusicPlayerViewModel
import com.hich2000.tagcapella.music_player.SongCard
import com.hich2000.tagcapella.music_player.SongListViewModel
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
        // Use viewModel() to instantiate the ViewModels
        val playerViewModel: MusicPlayerViewModel = viewModel()
        val songListViewModel: SongListViewModel = viewModel()

        // LaunchedEffect to trigger async initialization logic
        LaunchedEffect(Unit) {
            playerViewModel.initializeMediaController()
            songListViewModel.initializeSongList()
        }

        // Use the state variable to determine if the MediaController and songlist are initialized
        val isMediaControllerInitialized by playerViewModel.isMediaControllerInitialized
        val isSongListInitialized by songListViewModel.isInitialized

        //get the songlist from the view model
        val songList = remember { songListViewModel.songList }

        CompositionLocalProvider(
            LocalSongListViewModel provides songListViewModel,
            LocalMusicPlayerViewModel provides playerViewModel
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    if (isMediaControllerInitialized) {
                        MusicControls()
                    }
                },
                topBar = {
                    Text(
                        "ayylmao",
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, Color.Gray),
                        textAlign = TextAlign.Center
                    )
                }
            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(innerPadding)
                ) {

                    if (!isMediaControllerInitialized || !isSongListInitialized) {
                        item {
                            CircularProgressIndicator(
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } else {
                        itemsIndexed(songList) { index, song ->
                            SongCard(song, index)
                        }
                    }
                }
            }
        }
    }
}

