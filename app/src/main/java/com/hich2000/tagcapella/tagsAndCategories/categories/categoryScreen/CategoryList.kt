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
import com.hich2000.tagcapella.tagsAndCategories.categories.Category

@Composable
fun CategoryList(
    categoryList: List<Category>,
    categoryCard: @Composable (category: Category) -> Unit,
) {
    val columnScroll = rememberScrollState()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
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