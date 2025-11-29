package com.hich2000.tagcapella.main

import android.widget.Toast
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hich2000.tagcapella.main.navigation.BottomNavBar
import com.hich2000.tagcapella.main.navigation.LocalNavController
import com.hich2000.tagcapella.main.navigation.NavBarItem
import com.hich2000.tagcapella.main.navigation.Route
import com.hich2000.tagcapella.music.playerScreen.PlayerScreen
import com.hich2000.tagcapella.music.songScreen.SongScreen
import com.hich2000.tagcapella.settings.SettingsScreen
import com.hich2000.tagcapella.settings.folderScreen.FolderScreen
import com.hich2000.tagcapella.tagsAndCategories.TagCategoryScreen
import com.hich2000.tagcapella.utils.ToastEventBus

@Composable
fun TagcapellaApp() {
    val mainNavController = rememberNavController()
    val context = LocalContext.current
    val slideSpeed = 250

    LaunchedEffect(Unit) {
        ToastEventBus.toastFlow.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            BottomNavBar(mainNavController)
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

                    composable(
                        route = Route.Player.route
                    ) {
                        PlayerScreen()
                    }

                    composable(
                        route = Route.SongLibrary.route
                    ) {
                        SongScreen()
                    }

                    composable(
                        route = Route.Tags.route
                    ) {
                        TagCategoryScreen()
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