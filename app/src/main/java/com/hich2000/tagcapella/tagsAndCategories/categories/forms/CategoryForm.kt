package com.hich2000.tagcapella.tagsAndCategories.categories.forms

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hich2000.tagcapella.tagsAndCategories.categories.Category
import com.hich2000.tagcapella.tagsAndCategories.categories.categoryScreen.CategoryScreenViewModel
import com.hich2000.tagcapella.utils.composables.TagCapellaButton

@Composable
fun CategoryForm(
    category: Category? = null,
    categoryScreenViewModel: CategoryScreenViewModel = hiltViewModel()
) {
    var textState by remember { mutableStateOf(if (category is Category) category.category else "") }

    Surface(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.large,
        tonalElevation = AlertDialogDefaults.TonalElevation
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .border(2.dp, MaterialTheme.colorScheme.tertiary)
        ) {
            TextField(
                value = textState,
                onValueChange = { textState = it },
                label = { Text("Category") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp),
            )

            Box(
                modifier = Modifier.padding(0.dp)
            ) {
                if (category === null) {
                    TagCapellaButton(
                        onClick = {
                            categoryScreenViewModel.insertCategory(textState)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .height(36.dp)
                            .border(2.dp, MaterialTheme.colorScheme.tertiary)
                            .padding(0.dp)
                    ) {
                        Text("add")
                    }
                } else {
                    TagCapellaButton(
                        onClick = {
                            categoryScreenViewModel.updateCategory(
                                id = category.id,
                                category = textState
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .height(36.dp)
                            .border(2.dp, MaterialTheme.colorScheme.tertiary)
                            .padding(0.dp)
                    ) {
                        Text("update")
                    }
                }
            }
        }
    }
}