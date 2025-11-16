package com.hich2000.tagcapella.tagsAndCategories.categories.categoryScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.hich2000.tagcapella.tagsAndCategories.categories.forms.CategoryForm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    categoryScreenViewModel: CategoryScreenViewModel = hiltViewModel()
) {
    val categoryList by categoryScreenViewModel.categories.collectAsState()
    val showCategoryDialog by categoryScreenViewModel.showDialog.collectAsState()
    val clickedCategory by categoryScreenViewModel.selectedCategory.collectAsState()

    if (showCategoryDialog) {
        BasicAlertDialog(
            onDismissRequest = {
                categoryScreenViewModel.closeDialog()
            },
        ) {
            CategoryForm(
                category = clickedCategory
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CategoryList(
            categoryList = categoryList,
            categoryCard = { category ->
                val editCallback: (() -> Unit) = {
                    categoryScreenViewModel.openDialog(category)
                }
                val deleteCallback: (() -> Unit) = {
                    categoryScreenViewModel.deleteCategory(category.id)
                }

                CategoryCard(
                    category = category,
                    editCallback = editCallback,
                    deleteCallback = deleteCallback
                )
            }
        )
    }
}