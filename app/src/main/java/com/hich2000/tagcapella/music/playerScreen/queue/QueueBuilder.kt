package com.hich2000.tagcapella.music.playerScreen.queue

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hich2000.tagcapella.main.navigation.LocalNavController
import com.hich2000.tagcapella.main.navigation.TopBar
import com.hich2000.tagcapella.tagsAndCategories.tags.tagList.TagCard
import com.hich2000.tagcapella.tagsAndCategories.tags.tagList.TagList
import com.hich2000.tagcapella.utils.composables.TagCapellaButton

@Composable
fun QueueBuilder(
    queueBuilderViewModel: QueueBuilderViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val tagList by queueBuilderViewModel.tags.collectAsState()
    val includedTags by queueBuilderViewModel.includedTags.collectAsState()
    val excludedTags by queueBuilderViewModel.excludedTags.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = { TopBar(topText = "Queue Builder") }
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TagList(
                tagList = tagList,
                tagCard = { tag ->
                    TagCard(
                        tag = tag,
                        onClick = { queueBuilderViewModel.toggleTagFilter(tag) },
                        backgroundColor = if (includedTags.any { it.id == tag.id }) {
                            Color.Green
                        } else if (excludedTags.any { it.id == tag.id }) {
                            Color.Red
                        } else {
                            MaterialTheme.colorScheme.background
                        }
                    )
                },
            )

            TagCapellaButton(
                onClick = {
                    queueBuilderViewModel.updateQueue()
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(36.dp)
                    .border(2.dp, MaterialTheme.colorScheme.tertiary)
            ) {
                Text(text = "Save")
            }
        }
    }
}