package com.hich2000.tagcapella.music_player

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.hich2000.tagcapella.R
import com.hich2000.tagcapella.music_list.ui.MusicListActivity
import java.util.concurrent.ExecutionException
import kotlin.io.path.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension

class MusicPlayerActivity : AppCompatActivity() {

    private lateinit var mediaController: MediaController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_music_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sessionToken = SessionToken(this, ComponentName(this, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture.addListener(
            {
                try {
                    mediaController = controllerFuture.get()

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

                    mediaController.repeatMode = Player.REPEAT_MODE_ALL
                    mediaController.addMediaItems(playlist)
                    mediaController.prepare()

                    val playButton = findViewById<ImageButton>(R.id.playButton)
                    val nextButton = findViewById<ImageButton>(R.id.nextButton)
                    val prevButton = findViewById<ImageButton>(R.id.prevButton)
                    val loopModeButton = findViewById<ImageButton>(R.id.loopModeButton)
                    val shuffleButton = findViewById<ImageButton>(R.id.shuffleButton)

                    playButton.setOnClickListener {
                        if (mediaController.isPlaying) {
                            mediaController.pause()
                            playButton.setImageResource(androidx.media3.session.R.drawable.media3_icon_play)
                        } else {
                            mediaController.play()
                            playButton.setImageResource(androidx.media3.session.R.drawable.media3_icon_pause)
                        }
                    }

                    nextButton.setOnClickListener {
                        mediaController.seekToNext()
                    }

                    prevButton.setOnClickListener {
                        mediaController.seekToPrevious()
                    }

                    loopModeButton.setOnClickListener {
                        if (mediaController.repeatMode == Player.REPEAT_MODE_ALL) {
                            mediaController.repeatMode = Player.REPEAT_MODE_ONE
                            loopModeButton.setImageResource(androidx.media3.session.R.drawable.media3_icon_repeat_one)
                        } else if (mediaController.repeatMode == Player.REPEAT_MODE_ONE) {
                            mediaController.repeatMode = Player.REPEAT_MODE_OFF
                            loopModeButton.setImageResource(androidx.media3.session.R.drawable.media3_icon_repeat_off)
                        } else if (mediaController.repeatMode == Player.REPEAT_MODE_OFF) {
                            mediaController.repeatMode = Player.REPEAT_MODE_ALL
                            loopModeButton.setImageResource(androidx.media3.session.R.drawable.media3_icon_repeat_all)
                        }
                    }

                    shuffleButton.setOnClickListener {
                        if (mediaController.shuffleModeEnabled) {
                            mediaController.shuffleModeEnabled = false
                            shuffleButton.setImageResource(androidx.media3.session.R.drawable.media3_icon_shuffle_off)
                        } else {
                            mediaController.shuffleModeEnabled = true
                            shuffleButton.setImageResource(androidx.media3.session.R.drawable.media3_icon_shuffle_on)
                        }
                    }


                    val button = findViewById<Button>(R.id.button)

                    button.setOnClickListener {
                        val intent = Intent(this, MusicListActivity::class.java)
                        startActivity(intent)
                    }


                } catch (e: ExecutionException) {
                    finish()
                }
            },
            MoreExecutors.directExecutor()
        )

    }
}