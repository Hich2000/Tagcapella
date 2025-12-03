package com.hich2000.tagcapella.main

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hich2000.tagcapella.main.navigation.BottomNavBar
import com.hich2000.tagcapella.main.navigation.NavBarItem
import com.hich2000.tagcapella.main.navigation.Route
import com.hich2000.tagcapella.music.playerScreen.PlayerScreen
import com.hich2000.tagcapella.music.songScreen.SongScreen
import com.hich2000.tagcapella.settings.SettingsScreen
import com.hich2000.tagcapella.tagsAndCategories.TagCategoryScreen

@Composable
fun MainScaffold() {
    val navController = rememberNavController()
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route
    val slideSpeed = 250

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            BottomNavBar(navController)
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
                            navController.popBackStack()
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
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            NavHost(
                navController = navController,
                route = Route.Main.route,
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
                    route = Route.Songs.route
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