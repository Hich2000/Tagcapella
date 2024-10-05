package com.hich2000.tagcapella

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.io.path.Path
import kotlin.io.path.listDirectoryEntries
import android.Manifest
import android.content.ComponentName
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import java.util.concurrent.ExecutionException
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile

class MainActivity : AppCompatActivity() {

    lateinit var mediaController: MediaController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Register the permissions callback, which handles the user's response to the
        // system permissions dialog. Save the return value, an instance of
        // ActivityResultLauncher. You can use either a val, as shown in this snippet,
        // or a lateinit var in your onAttach() or onCreate() method.
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) {
            }


        val audioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)

        if (audioPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(
                Manifest.permission.READ_MEDIA_AUDIO)
        }

    }

    override fun onStart() {
        super.onStart()

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
                            playlist.add(MediaItem.fromUri(it.toString()))
                        }
                    }

                    mediaController.addMediaItems(playlist)
                    mediaController.prepare()
                    mediaController.play()

                } catch (e: ExecutionException) {
                    finish()
                }
            },
            MoreExecutors.directExecutor()
        )
    }
}