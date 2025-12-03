package com.hich2000.tagcapella.main

import android.widget.Toast
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
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
    val context = LocalContext.current
    val slideSpeed = 250

    LaunchedEffect(Unit) {
        ToastEventBus.toastFlow.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    CompositionLocalProvider(LocalNavController provides rootNavController) {
        NavHost(
            navController = rootNavController,
            route = Route.Root.route,
            startDestination = Route.Main.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    tween(slideSpeed)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    tween(slideSpeed)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    tween(slideSpeed)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    tween(slideSpeed)
                )
            }
        ) {
            //main screen with bottom nav bar
            composable(
                route = Route.Main.route
            ) {
                MainNavScaffold()
            }

            //sub screens without bottom nav bar
            composable(
                route = Route.Player.QueueBuilder.route
            ) {
                SubScaffold {
                    QueueBuilder()
                }
            }
            composable(
                route = Route.Songs.Tags.route,
                arguments = listOf(
                    navArgument("songPath") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                SubScaffold {
                    val songPath = backStackEntry.arguments?.getString("songPath") ?: ""
                    SongTagScreen(songPath)
                }
            }
            composable(
                route = Route.Tags.Songs.route,
                arguments = listOf(
                    navArgument("tagId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                SubScaffold {
                    val tagId = backStackEntry.arguments?.getLong("tagId") ?: 0L
                    TagSongScreen(tagId)
                }
            }
            composable(
                route = Route.Settings.Folders.route
            ) {
                SubScaffold {
                    FolderScreen()
                }
            }
        }
    }
}