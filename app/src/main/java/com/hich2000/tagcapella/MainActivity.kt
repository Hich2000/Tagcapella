package com.hich2000.tagcapella

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.hich2000.tagcapella.categories.CategoryForm
import com.hich2000.tagcapella.categories.CategoryScreen
import com.hich2000.tagcapella.music.songScreen.SongScreen
import com.hich2000.tagcapella.music.controls.MusicControls
import com.hich2000.tagcapella.settings.SettingsScreen
import com.hich2000.tagcapella.settings.folderScreen.FolderScreen
import com.hich2000.tagcapella.songs.FolderScanManager
import com.hich2000.tagcapella.tags.forms.TagForm
import com.hich2000.tagcapella.tags.tagScreen.TagScreen
import com.hich2000.tagcapella.theme.TagcapellaTheme
import com.hich2000.tagcapella.utils.LifeCycleManager
import com.hich2000.tagcapella.utils.ToastEventBus
import com.hich2000.tagcapella.utils.composables.ExpandableFab
import com.hich2000.tagcapella.utils.composables.TagCapellaButton
import com.hich2000.tagcapella.utils.navigation.LocalNavController
import com.hich2000.tagcapella.utils.navigation.NavItem
import com.hich2000.tagcapella.utils.sharedPreferences.SharedPreferenceKey
import com.hich2000.tagcapella.utils.sharedPreferences.SharedPreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
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

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    @Inject
    lateinit var folderScanManager: FolderScanManager

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
        folderScanManager.addScanFolder("Music/")
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

    @Composable
    fun TagcapellaApp() {
        val navController = rememberNavController()
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            ToastEventBus.toastFlow.collect { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }


        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            bottomBar = { BottomNavBar(navController) }
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
                CompositionLocalProvider(LocalNavController provides navController) {
                    NavHost(
                        navController = navController,
                        startDestination = NavItem.Player.title,
                    ) {
                        composable(NavItem.Player.title) {
                            MusicControls()
                        }
                        composable(NavItem.SongLibrary.title) {
                            SongScreen()
                        }
                        composable(NavItem.Tags.title) {
                            TagCategoryScreen()
                        }
                        navigation(
                            startDestination = NavItem.Settings.Main.title,
                            route = NavItem.Settings.title
                        ) {
                            composable(NavItem.Settings.Main.title) {
                                SettingsScreen()
                            }
                            composable(NavItem.Settings.Folders.title) {
                                FolderScreen()
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun BottomNavBar(navController: NavController) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(MaterialTheme.colorScheme.primary)
                .border(2.dp, MaterialTheme.colorScheme.tertiary)
                .clickable(
                    //clickable modifier to block passthrough clicks to the bottom sheet below.
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                ),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem.navItems.forEach {
                IconButton(
                    onClick = {
                        if (currentRoute != it.title) {
                            navController.navigate(it.title) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                ) {
                    it.icon?.let { imageVector ->
                        Icon(
                            imageVector = imageVector,
                            contentDescription = it.title,
                            tint = if (currentRoute == it.title) {
                                MaterialTheme.colorScheme.secondary
                            } else {
                                MaterialTheme.colorScheme.secondary.copy(
                                    alpha = 0.4f
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun TagCategoryScreen() {
        val selectedScreen = rememberSaveable { mutableIntStateOf(0) }
        val showTagDialog = remember { mutableStateOf(false) }
        val showCategoryDialog = remember { mutableStateOf(false) }
        var fabExpanded by remember { mutableStateOf(false) }

        if (showTagDialog.value) {
            BasicAlertDialog(
                onDismissRequest = {
                    showTagDialog.value = false
                },
            ) {
                TagForm()
            }
        }

        if (showCategoryDialog.value) {
            BasicAlertDialog(
                onDismissRequest = {
                    showCategoryDialog.value = false
                },
            ) {
                CategoryForm()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        if (fabExpanded) {
                            fabExpanded = false
                        }
                    }
                ),
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TagCapellaButton(
                        onClick = {
                            selectedScreen.intValue = 0
                        },
                        modifier = Modifier
                            .weight(1f)
                            .border(2.dp, MaterialTheme.colorScheme.tertiary, RectangleShape),
                        shape = RectangleShape,
                    ) {
                        Text(
                            "Tags"
                        )
                    }
                    TagCapellaButton(
                        onClick = {
                            selectedScreen.intValue = 1
                        },
                        modifier = Modifier
                            .weight(1f)
                            .border(2.dp, MaterialTheme.colorScheme.tertiary, RectangleShape),
                        shape = RectangleShape,
                    ) {
                        Text(
                            "Categories"
                        )
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        ExpandableFab(
                            buttons = listOf {
                                TagCapellaButton(
                                    onClick = {
                                        showTagDialog.value = true
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(2.dp, MaterialTheme.colorScheme.tertiary)
                                ) {
                                    Text("New Tag")
                                }
                                TagCapellaButton(
                                    onClick = {
                                        showCategoryDialog.value = true
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(2.dp, MaterialTheme.colorScheme.tertiary)
                                ) {
                                    Text("New Category")
                                }
                            },
                            expanded = fabExpanded,
                            onclick = { fabExpanded = true }
                        )
                    }
                ) {
                    if (selectedScreen.intValue == 0) {
                        TagScreen()
                    } else if (selectedScreen.intValue == 1) {
                        CategoryScreen()
                    }
                }
            }
        }
    }
}