package com.hich2000.tagcapella.main.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.hich2000.tagcapella.music.playerScreen.PlayerScreen
import com.hich2000.tagcapella.music.songScreen.SongScreen
import com.hich2000.tagcapella.settings.SettingsScreen
import com.hich2000.tagcapella.settings.folderScreen.FolderScreen
import com.hich2000.tagcapella.tagsAndCategories.TagCategoryScreen

// Bottom Navigation Screen Enum (To track selected screen)
sealed class NavItem(
    val route: String,
    val icon: ImageVector? = null,
    val navBar: Boolean = false,
    val content: @Composable () -> Unit = {}
) {

    companion object {
        val navItems = listOf(
            Player,
            SongLibrary,
            Tags,
            Settings,
            Settings.Folders
        )
    }

    object Player : NavItem(
        route = Route.Player.route,
        icon = Icons.Default.MusicNote,
        navBar = true,
        content = { PlayerScreen() }
    )

    object SongLibrary : NavItem(
        route = Route.SongLibrary.route,
        icon = Icons.Default.Folder,
        navBar = true,
        content = { SongScreen() }
    )

    object Tags : NavItem(
        route = Route.Tags.route,
        icon = Icons.AutoMirrored.Filled.Label,
        navBar = true,
        content = { TagCategoryScreen() }
    )

    object Settings : NavItem(
        route = Route.Settings.route,
        icon = Icons.Default.MoreVert,
        navBar = true,
        content = { SettingsScreen() }
    ) {
        object Folders : NavItem(
            route = Route.Settings.Folders.route,
            content = { FolderScreen() }
        )
    }
}