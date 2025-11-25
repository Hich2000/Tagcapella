package com.hich2000.tagcapella.utils.navigation

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
    val title: String,
    val icon: ImageVector? = null,
    val navBar: Boolean = false,
    val screen: @Composable () -> Unit = {}
) {

    companion object {
        val navItems = listOf(
            Player,
            SongLibrary,
            Tags,
            Settings,
            Settings.Main,
            Settings.Folders
        )
    }

    object Player : NavItem(
        title = "Player",
        icon = Icons.Default.MusicNote,
        navBar = true,
        screen = { PlayerScreen() }
    )

    object SongLibrary : NavItem(
        title = "Song Library",
        icon = Icons.Default.Folder,
        navBar = true,
        screen = { SongScreen() }
    )

    object Tags : NavItem(
        title = "Tags",
        icon = Icons.AutoMirrored.Filled.Label,
        navBar = true,
        screen = { TagCategoryScreen() }
    )

    object Settings : NavItem(
        title = "Settings",
        icon = Icons.Default.MoreVert,
        navBar = true,
        screen = { SettingsScreen() }
    ) {
        object Main : NavItem(
            title = "${title}/Main",
            screen = { SettingsScreen() }
        )

        object Folders : NavItem(
            title = "${title}/Folders",
            screen = { FolderScreen() }
        )
    }
}