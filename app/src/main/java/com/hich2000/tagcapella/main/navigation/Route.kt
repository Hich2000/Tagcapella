package com.hich2000.tagcapella.main.navigation

sealed class Route(
    val route: String
) {

    companion object {
        val navBar = listOf<Route>(
            Player,
            SongLibrary,
            Tags,
            Settings
        )
    }

    data object Player : Route(route = "player")
    data object SongLibrary : Route(route = "library")
    data object Tags : Route(route = "tags_categories")
    data object Settings : Route(route = "settings") {
        data object Folders : Route(route = "${route}/folders")
    }
}