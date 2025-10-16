package com.hich2000.tagcapella.utils

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

@Composable
fun ExpandableFab(
    buttons: List<@Composable () -> Unit>,
    expanded: Boolean = false,
    onclick: (() -> Unit)
) {
    Box(
        contentAlignment = Alignment.Companion.BottomEnd
    ) {
        AnimatedContent(
            targetState = expanded,
            transitionSpec = {
                fadeIn(tween(200)) + scaleIn(tween(200)) togetherWith
                        fadeOut(tween(200)) + scaleOut(tween(200))
            },
            label = "FAB Expansion",
        ) { expanded ->
            if (expanded) {
                Column(
                    modifier = Modifier.Companion
                        .border(2.dp, MaterialTheme.colorScheme.tertiary)
                        .width(200.dp)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    buttons.forEach { button ->
                        button()
                    }
                }
            } else {
                FloatingActionButton(
                    onClick = onclick,
                    containerColor = MaterialTheme.colorScheme.background,
                    modifier = Modifier.Companion
                        .padding(16.dp)
                        .border(2.dp, MaterialTheme.colorScheme.tertiary),
                    shape = RectangleShape
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Expand",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}