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
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.hich2000.tagcapella.categories.CategoryScreen
import com.hich2000.tagcapella.music_player.MusicControls
import com.hich2000.tagcapella.music_player.SongScreen
import com.hich2000.tagcapella.songs.SongViewModel
import com.hich2000.tagcapella.tags.TagScreen
import com.hich2000.tagcapella.theme.TagcapellaTheme
import com.hich2000.tagcapella.utils.NavItems
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltAndroidApp
class MyApp : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private val _mediaPermissionGranted: MutableStateFlow<Int> =
        MutableStateFlow(PackageManager.PERMISSION_DENIED)
    private val mediaPermissionGranted: StateFlow<Int> get() = _mediaPermissionGranted

    private val songViewModel: SongViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        _mediaPermissionGranted.value = ContextCompat.checkSelfPermission(
            this,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_AUDIO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
        )
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
            } else {
                _mediaPermissionGranted.value = PackageManager.PERMISSION_GRANTED
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
        var selectedScreen by remember { mutableStateOf(NavItems.Player) }
        val mediaPermissionGranted by mediaPermissionGranted.collectAsState()

        if (mediaPermissionGranted == PackageManager.PERMISSION_GRANTED) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(Color.Black)
                            .border(2.dp, Color.Gray),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NavItems.entries.forEach {
                            IconButton(
                                onClick = { selectedScreen = it },
                            ) {
                                Icon(
                                    imageVector = it.icon,
                                    contentDescription = it.title,
                                    tint = if (selectedScreen == it) {
                                        Color.White
                                    } else {
                                        Color.Gray
                                    }
                                )
                            }
                        }
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
                Box(
                    modifier = Modifier
                        .padding(
                            top = innerPadding.calculateTopPadding(),
                            start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                            bottom = innerPadding.calculateBottomPadding()
                        )
                        .fillMaxSize()
                ) {
                    val songList by songViewModel.songList.collectAsState()

                    if (selectedScreen == NavItems.SongLibrary) {
                        SongScreen(songList = songList)
                    } else if (selectedScreen == NavItems.Tags) {
                        TagScreen()
                    } else if (selectedScreen == NavItems.Player) {
                        MusicControls()
                    } else if (selectedScreen == NavItems.Categories) {
                        CategoryScreen()
                    }
                }
            }
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("Media permissions are necessary to use this application")
                        }
                    }
                }
            }
        }
    }
}