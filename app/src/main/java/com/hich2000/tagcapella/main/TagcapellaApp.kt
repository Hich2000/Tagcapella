package com.hich2000.tagcapella.main

import android.widget.Toast
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hich2000.tagcapella.main.navigation.BottomNavBar
import com.hich2000.tagcapella.main.navigation.LocalNavController
import com.hich2000.tagcapella.main.navigation.NavBarItem
import com.hich2000.tagcapella.main.navigation.Route
import com.hich2000.tagcapella.music.playerScreen.PlayerScreen
import com.hich2000.tagcapella.music.playerScreen.queue.QueueBuilder
import com.hich2000.tagcapella.music.songScreen.SongScreen
import com.hich2000.tagcapella.music.songScreen.songTagScreen.SongTagScreen
import com.hich2000.tagcapella.settings.SettingsScreen
import com.hich2000.tagcapella.settings.folderScreen.FolderScreen
import com.hich2000.tagcapella.tagsAndCategories.TagCategoryScreen
import com.hich2000.tagcapella.tagsAndCategories.tags.tagScreen.TagSongScreen
import com.hich2000.tagcapella.utils.ToastEventBus

@Composable
fun TagcapellaApp() {
    val mainNavController = rememberNavController()
    val currentNavBackStackEntry by mainNavController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route
    val showNavBar =
        currentRoute != null && NavBarItem.bottomNavItems.any { it.route.route == currentRoute }
    val context = LocalContext.current
    val slideSpeed = 250

    LaunchedEffect(Unit) {
        ToastEventBus.toastFlow.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            if (showNavBar) {
                BottomNavBar(mainNavController)
            }
        },
        topBar = {
            Column {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    IconButton(
                        onClick = {
                            mainNavController.popBackStack()
                        }
                    ) {
                        //should not show up on starting route,
                        // but I want to keep divider height so we keep the button, just no icon
                        if (currentRoute != Route.Player.route) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                tint = MaterialTheme.colorScheme.secondary,
                                contentDescription = "Back to settings"
                            )
                        }
                    }
                }
                HorizontalDivider(
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = if (showNavBar) innerPadding.calculateStartPadding(LocalLayoutDirection.current) else 0.dp,
                    bottom = if (showNavBar) innerPadding.calculateBottomPadding() else 0.dp
                )
                .fillMaxSize()
        ) {
            CompositionLocalProvider(LocalNavController provides mainNavController) {
                NavHost(
                    navController = mainNavController,
                    startDestination = Route.Player.route,
                    enterTransition = {
                        slideIntoContainer(
                            getSlideDirection(initialState, targetState),
                            tween(slideSpeed)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            getSlideDirection(initialState, targetState),
                            tween(slideSpeed)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            getSlideDirection(initialState, targetState),
                            tween(slideSpeed)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            getSlideDirection(initialState, targetState),
                            tween(slideSpeed)
                        )
                    }
                ) {
                    //todo maybe I can make a base route composable that contains everything
                    // and then I can have some of the routes wrapped in the scaffold with the bottom bar and the others not?
                    // Maybe then I don't need to do the check for the bottom bar anymore and can have cleaner transitions?
                    composable(
                        route = Route.Player.route
                    ) {
                        PlayerScreen()
                    }

                    composable(
                        route = Route.Player.QueueBuilder.route
                    ) {
                        QueueBuilder()
                    }

                    composable(
                        route = Route.Songs.route
                    ) {
                        SongScreen()
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
                        route = Route.Tags.route
                    ) {
                        TagCategoryScreen()
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
                        route = Route.Settings.route
                    ) {
                        SettingsScreen()
                    }

                    composable(
                        route = Route.Settings.Folders.route
                    ) {
                        FolderScreen()
                    }
                }
            }
        }
    }
}

fun getSlideDirection(
    initialState: NavBackStackEntry,
    targetState: NavBackStackEntry
): AnimatedContentTransitionScope.SlideDirection {
    val navItems = NavBarItem.bottomNavItems
    val targetIndex =
        navItems.indexOfFirst { it.route.route == targetState.destination.route }
    val initialIndex =
        navItems.indexOfFirst { it.route.route == initialState.destination.route }

    return if (targetIndex > initialIndex && initialIndex >= 0) {
        AnimatedContentTransitionScope.SlideDirection.Start
    } else if (targetIndex == -1) {
        AnimatedContentTransitionScope.SlideDirection.Start
    } else {
        AnimatedContentTransitionScope.SlideDirection.End
    }
}