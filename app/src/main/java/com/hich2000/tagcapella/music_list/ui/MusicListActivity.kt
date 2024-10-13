package com.hich2000.tagcapella.music_list.ui

import android.content.ComponentName
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.hich2000.tagcapella.theme.TagcapellaTheme
import com.hich2000.tagcapella.music_player.PlaybackService
import java.util.concurrent.ExecutionException
import kotlin.io.path.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension

class MusicListActivity : ComponentActivity() {

    lateinit var mediaController: MediaController
    var mediaControllerReady: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        val sessionToken = SessionToken(this, ComponentName(this, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture.addListener({
            try {
                mediaController = controllerFuture.get()
                mediaControllerReady = true
            } catch (e: ExecutionException) {
                finish()
            }
        }, MoreExecutors.directExecutor())


        val playlist = mutableListOf<MediaItem>()
        val path = Path("/storage/emulated/0/Music").listDirectoryEntries()
        path.listIterator().forEach {
            if (!it.isDirectory() && it.isRegularFile()) {

                val mediaItem = MediaItem.Builder()
                    .setMediaId(it.toString())
                    .setUri(it.toString())
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(it.nameWithoutExtension)
                            .setDisplayTitle(it.nameWithoutExtension)
                            .build()
                    )
                    .build()

                playlist.add(mediaItem)
            }
        }

        setContent {
            TagcapellaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState(0))
                    ) {
                        for ((index, song) in playlist.withIndex()) {
                            SongCard(song, index)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun SongCard(mediaItem: MediaItem, mediaItemIndex: Int) {
        val scroll = rememberScrollState(0)
        Card(
            modifier = Modifier
                .border(2.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .background(Color.Gray)
                .height(75.dp),
            onClick = {
                if (mediaControllerReady) {
                    mediaController.seekTo(mediaItemIndex, C.TIME_UNSET)
                }
            }
        ) {
            Row {
                Icon(Icons.Rounded.PlayArrow, contentDescription = null)
                Text(
                    mediaItem.mediaMetadata.title.toString(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scroll)
                        .fillMaxHeight()
                )
            }
        }
    }


}

//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    TagcapellaTheme {
//        Greeting("Android")
//    }
//}