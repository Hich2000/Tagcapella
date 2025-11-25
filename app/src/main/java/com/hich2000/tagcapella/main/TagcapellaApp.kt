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
import com.hich2000.tagcapella.utils.ToastEventBus
import com.hich2000.tagcapella.utils.navigation.LocalNavController
import com.hich2000.tagcapella.utils.navigation.NavItem

@Composable
fun TagcapellaApp() {
    val mainNavController = rememberNavController()
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

    NavScaffold(mainNavController, showNavBar) {
        CompositionLocalProvider(LocalNavController provides mainNavController) {
            NavHost(
                navController = mainNavController,
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

                NavItem.navItems.forEach { navItem ->
                    composable(navItem.title) {
                        showNavBar = navItem.navBar
                        navItem.screen()
                    }
                }

            }
        }
    }
}