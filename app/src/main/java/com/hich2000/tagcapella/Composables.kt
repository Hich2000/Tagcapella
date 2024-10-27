package com.hich2000.tagcapella

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.ui.graphics.vector.ImageVector

// Bottom Navigation Screen Enum (To track selected screen)
enum class NavItems(
    val title: String,
    val icon: ImageVector
) {

    Player(
        title = "Player",
        icon = Icons.Default.MusicNote
    ),
    SongList (
        title = "Song List",
        icon = Icons.AutoMirrored.Filled.List
    ),
    Tags (
        title = "Tags",
        icon = Icons.AutoMirrored.Filled.Label
    )
}