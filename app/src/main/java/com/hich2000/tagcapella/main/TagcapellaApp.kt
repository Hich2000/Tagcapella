package com.hich2000.tagcapella.main

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hich2000.tagcapella.main.navigation.LocalNavController
import com.hich2000.tagcapella.main.navigation.Route
import com.hich2000.tagcapella.music.playerScreen.queue.QueueBuilder
import com.hich2000.tagcapella.music.songScreen.songTagScreen.SongTagScreen
import com.hich2000.tagcapella.settings.folderScreen.FolderScreen
import com.hich2000.tagcapella.tagsAndCategories.tags.tagScreen.TagSongScreen
import com.hich2000.tagcapella.utils.ToastEventBus

@Composable
fun TagcapellaApp() {
    val rootNavController = rememberNavController()
    val mainNavController = rememberNavController()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        ToastEventBus.toastFlow.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    CompositionLocalProvider(LocalNavController provides mainNavController) {
        NavHost(
            navController = rootNavController,
            route = Route.Root.route,
            startDestination = Route.Main.route
        ) {
            composable(
                route = Route.Main.route
            ) {
                MainScaffold(mainNavController)
            }

            composable(
                route = Route.Player.QueueBuilder.route
            ) {
                QueueBuilder()
            }
            composable(
                route = Route.Songs.Tags.route,
                arguments = listOf(
                    navArgument("songPath") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val songPath = backStackEntry.arguments?.getString("songPath") ?: ""
                SongTagScreen(songPath)
            }
            composable(
                route = Route.Tags.Songs.route,
                arguments = listOf(
                    navArgument("tagId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val tagId = backStackEntry.arguments?.getLong("tagId") ?: 0L
                TagSongScreen(tagId)
            }
            composable(
                route = Route.Settings.Folders.route
            ) {
                FolderScreen()
            }
        }
    }
}