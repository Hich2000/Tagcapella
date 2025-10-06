package com.hich2000.tagcapella.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.ui.graphics.vector.ImageVector

// Bottom Navigation Screen Enum (To track selected screen)
sealed class NavItems(
    val title: String,
    val icon: ImageVector? = null,
    val navBar: Boolean = false
) {

    init {
        if (navBar) {
            navItems += this
        }
    }

    companion object {
        var navItems = listOf<NavItems>()
            private set
    }

    object Player : NavItems(
        title = "Player",
        icon = Icons.Default.MusicNote,
        navBar = true
    )

    object SongLibrary : NavItems(
        title = "Song Library",
        icon = Icons.Default.Folder,
        navBar = true
    )

    object Tags : NavItems(
        title = "Tags",
        icon = Icons.AutoMirrored.Filled.Label,
        navBar = true
    )

    object Settings : NavItems(
        title = "Settings",
        icon = Icons.Default.MoreVert,
        navBar = true
    ) {
        //because these ones are nested they will not show up in the navbar
        object Main: NavItems(
            title = "${title}/Main"
        )
        object Folders : NavItems(
            title = "${title}/Folders"
        )
    }
}