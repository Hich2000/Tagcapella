package com.hich2000.tagcapella.main

import android.widget.Toast
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.hich2000.tagcapella.music.playerScreen.PlayerScreen
import com.hich2000.tagcapella.music.songScreen.SongScreen
import com.hich2000.tagcapella.settings.SettingsScreen
import com.hich2000.tagcapella.settings.folderScreen.FolderScreen
import com.hich2000.tagcapella.tagsAndCategories.TagCategoryScreen
import com.hich2000.tagcapella.utils.ToastEventBus
import com.hich2000.tagcapella.utils.navigation.LocalNavController
import com.hich2000.tagcapella.utils.navigation.NavItem

@Composable
fun TagcapellaApp() {
    val navController = rememberNavController()
    var showNavBar by remember{ mutableStateOf(true) }
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
        val navItems = NavItem.navItems
        val targetIndex =
            navItems.indexOfFirst { it.title == targetState.destination.route }
        val initialIndex =
            navItems.indexOfFirst { it.title == initialState.destination.route }
        return if (targetIndex > initialIndex) AnimatedContentTransitionScope.SlideDirection.Start
        else AnimatedContentTransitionScope.SlideDirection.End
    }

    NavScaffold(navController, showNavBar) {
        CompositionLocalProvider(LocalNavController provides navController) {
            NavHost(
                navController = navController,
                startDestination = NavItem.Player.title,
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
                composable(NavItem.Player.title)
                {
                    //todo make this setup dynamic according to NavItem list.
                    showNavBar = true
                    PlayerScreen()
                }
                composable(NavItem.SongLibrary.title) {
                    showNavBar = true
                    SongScreen()
                }
                composable(NavItem.Tags.title) {
                    showNavBar = true
                    TagCategoryScreen()
                }
                navigation(
                    startDestination = NavItem.Settings.Main.title,
                    route = NavItem.Settings.title,
                ) {
                    composable(NavItem.Settings.Main.title) {
                        showNavBar = true
                        SettingsScreen()
                    }
                    composable(NavItem.Settings.Folders.title) {
                        showNavBar = false
                        FolderScreen()
                    }
                }
            }
        }
    }
}