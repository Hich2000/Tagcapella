package com.hich2000.tagcapella.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hich2000.tagcapella.utils.navigation.BottomNavBar

@Composable
fun NavScaffold(
    navController: NavController,
    showNavBar: Boolean,
    content: @Composable () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            if (showNavBar) BottomNavBar(navController)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(
                    top = if (showNavBar) {
                        innerPadding.calculateTopPadding()
                    } else {
                        0.dp
                    },
                    start = if (showNavBar) {
                        innerPadding.calculateStartPadding(LocalLayoutDirection.current)
                    } else {
                        0.dp
                    },
                    bottom = if (showNavBar) {
                        innerPadding.calculateBottomPadding()
                    } else {
                        0.dp
                    }
                )
                .fillMaxSize()
        ) {
            content()
        }
    }
}