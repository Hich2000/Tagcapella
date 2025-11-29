package com.hich2000.tagcapella.main.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.ui.graphics.vector.ImageVector

data class NavBarItem(
    val route: Route,
    val icon: ImageVector,
    val label: String
) {
    companion object {
        val bottomNavItems: List<NavBarItem> = listOf(
            NavBarItem(Route.Player, Icons.Default.MusicNote, "Player"),
            NavBarItem(Route.SongLibrary, Icons.Default.Folder, "Library"),
            NavBarItem(Route.Tags, Icons.AutoMirrored.Filled.Label, "Tags/Categories"),
            NavBarItem(Route.Settings, Icons.Default.MoreVert, "Settings"),
        )
    }
}