package com.hich2000.tagcapella.main.navigation

import android.net.Uri

sealed class Route(
    val route: String
) {

    data object Root : Route(route = "root")
    data object Main : Route(route = "main")

    data object Player : Route(route = "player") {
        data object QueueBuilder : Route(route = "${route}/queue_builder")
    }

    data object Songs : Route(route = "library") {
        data object Tags : Route(route = "${route}/song_tags/{songPath}") {
            fun createRoute(songPath: String): String =
                route.replace("{songPath}", Uri.encode(songPath))
        }
    }

    data object Tags : Route(route = "tags_categories") {
        data object Songs : Route(route = "${route}/tag_songs/{tagId}") {
            fun createRoute(tagId: Long): String = route.replace("{tagId}", tagId.toString())
        }
    }

    data object Settings : Route(route = "settings") {
        data object Folders : Route(route = "${route}/folders")
    }
}