package com.hich2000.tagcapella.utils.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?.substringBefore('/')

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
            if (!it.navBar) {
                return@forEach
            }

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