package com.hich2000.tagcapella.tags.tagList

import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hich2000.tagcapella.tags.TagDTO
import com.hich2000.tagcapella.utils.TagCapellaButton

@Composable
fun TagList(
    tagList: List<TagDTO> = emptyList(),
    tagCard: @Composable (tag: TagDTO) -> Unit,
    tagListViewModel: TagListViewModel = hiltViewModel()
) {
    val columnScroll by tagListViewModel.columnScrollState.collectAsState()
    val scroll by tagListViewModel.scrollState.collectAsState()
    val categories by tagListViewModel.categories.collectAsState()
    val selectedCategory by tagListViewModel.selectedCategory.collectAsState()


    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.Companion
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current)
                )
                .fillMaxSize()
                .verticalScroll(columnScroll)
        ) {
            if (categories.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.Companion.CenterVertically,
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .horizontalScroll(scroll)
                ) {
                    TagCapellaButton(
                        onClick = {
                            tagListViewModel.setSelectedCategory(null)
                        },
                        modifier = Modifier.Companion
                            .border(2.dp, MaterialTheme.colorScheme.tertiary, RectangleShape)
                            .padding(0.dp)
                            .width(120.dp),
                        shape = RectangleShape,
                    ) {
                        Text(
                            text = "All",
                            textAlign = TextAlign.Companion.Center,
                            modifier = Modifier.Companion
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                        )
                    }

                    categories.forEach { category ->
                        val buttonModifier = Modifier.Companion
                            .border(2.dp, MaterialTheme.colorScheme.tertiary, RectangleShape)
                            .padding(0.dp)
                        val finalModifier = if (category.category.length < 20) {
                            buttonModifier.width(120.dp)
                        } else {
                            buttonModifier.wrapContentWidth()
                            buttonModifier.weight(1f)
                        }

                        TagCapellaButton(
                            onClick = {
                                tagListViewModel.setSelectedCategory(category.id)
                            },
                            modifier = finalModifier,
                            shape = RectangleShape,
                        ) {
                            Text(
                                text = category.category,
                                textAlign = TextAlign.Companion.Center,
                                modifier = Modifier.Companion
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                            )
                        }
                    }
                }
            }


            tagList.forEach { tag ->
                if (selectedCategory !== null && tag.categoryId != selectedCategory) {
                    return@forEach
                }
                tagCard(tag)
            }
        }

    }
}