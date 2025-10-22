package com.hich2000.tagcapella.tagsAndCategories.categories.categoryScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import com.hich2000.tagcapella.tagsAndCategories.categories.CategoryDTO

@Composable
fun CategoryList(
    categoryList: List<CategoryDTO>,
    categoryCard: @Composable (category: CategoryDTO) -> Unit,
) {
    val columnScroll = rememberScrollState()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.Companion
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current)
                )
                .fillMaxSize()
                .verticalScroll(columnScroll)
        ) {
            categoryList.forEach { category ->
                categoryCard(category)
            }
        }
    }
}