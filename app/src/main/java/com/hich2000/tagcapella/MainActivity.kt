package com.hich2000.tagcapella

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.hich2000.tagcapella.main.TagcapellaApp
import com.hich2000.tagcapella.music.mediaController.MediaPlayerCoordinator
import com.hich2000.tagcapella.music.queueManager.FolderScanManager
import com.hich2000.tagcapella.music.queueManager.QueueManager
import com.hich2000.tagcapella.music.queueManager.SongRepository
import com.hich2000.tagcapella.theme.TagcapellaTheme
import com.hich2000.tagcapella.utils.LifeCycleManager
import com.hich2000.tagcapella.utils.composables.TagCapellaButton
import com.hich2000.tagcapella.utils.sharedPreferences.SharedPreferenceKey
import com.hich2000.tagcapella.utils.sharedPreferences.SharedPreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MyApp : Application(), LifecycleObserver {

    @Inject
    lateinit var appLifeCycleManager: LifeCycleManager

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifeCycleManager)
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mediaPermissionGranted = mutableIntStateOf(PackageManager.PERMISSION_DENIED)
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    @Inject
    lateinit var folderScanManager: FolderScanManager

    @Inject
    lateinit var songRepository: SongRepository

    @Inject
    lateinit var mediaPlayerCoordinator: MediaPlayerCoordinator

    @Inject
    lateinit var queueManager: QueueManager

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        mediaPermissionGranted.intValue = ContextCompat.checkSelfPermission(
            this,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_AUDIO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
        )

        setContent {
            TagcapellaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        if (mediaPermissionGranted.intValue == PackageManager.PERMISSION_GRANTED) {
                            TagcapellaApp()
                        } else {
                            RequestPermissionScreen()
                        }
                    }
                }
            }

        }
    }

    @Composable
    fun ObservePermissionOnResume(
        permission: String,
        onPermissionChanged: (Boolean) -> Unit
    ) {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current

        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    val isGranted = ContextCompat.checkSelfPermission(
                        context,
                        permission
                    ) == PackageManager.PERMISSION_GRANTED
                    onPermissionChanged(isGranted)
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    }

    private fun updateMediaPermissionGranted(isGranted: Boolean) {
        mediaPermissionGranted.intValue = if (isGranted) {
            PackageManager.PERMISSION_GRANTED
        } else {
            PackageManager.PERMISSION_DENIED
        }
        if (mediaPermissionGranted.intValue == PackageManager.PERMISSION_GRANTED) {
            coroutineScope.launch {
                folderScanManager.addScanFolder("Music/")
                songRepository.ini()
                queueManager.updateQueue()
                mediaPlayerCoordinator.ini()
            }
        }
    }

    @Composable
    fun RequestPermissionScreen() {
        var permissionAlreadyRequested = remember {
            sharedPreferenceManager.getPreference(
                SharedPreferenceKey.PermissionsAlreadyRequested,
                false
            )
        }

        val context = LocalActivity.current as Activity
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        // Initialize the permission launcher
        val permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            sharedPreferenceManager.savePreference(
                SharedPreferenceKey.PermissionsAlreadyRequested,
                true
            )
            permissionAlreadyRequested = true
            updateMediaPermissionGranted(isGranted)
        }

        ObservePermissionOnResume(permission) { isGranted ->
            updateMediaPermissionGranted(isGranted)
        }

        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Text(
                    "This app requires audio permissions to function",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                TagCapellaButton(
                    onClick = {
                        if (!permissionAlreadyRequested) {
                            permissionLauncher.launch(permission)
                        } else {
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Grant audio permissions")
                }
            }
        }
    }

}