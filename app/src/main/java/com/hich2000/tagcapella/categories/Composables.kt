package com.hich2000.tagcapella.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hich2000.tagcapella.utils.TagCapellaButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    categoryViewModel: CategoryViewModel = hiltViewModel()
) {
    val showCategoryDialog = remember { mutableStateOf(false) }
    val clickedCategory = remember { mutableStateOf<CategoryDTO?>(null) }

    if (showCategoryDialog.value) {
        BasicAlertDialog(
            onDismissRequest = {
                showCategoryDialog.value = false
                clickedCategory.value = null
            },
        ) {
            CategoryForm(
                category = clickedCategory.value
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CategoryList(
            categoryCard = { category ->
                val editCallback: (() -> Unit) = {
                    clickedCategory.value = category
                    showCategoryDialog.value = true
                }
                val deleteCallback: (() -> Unit) = {
                    categoryViewModel.deleteCategory(category.id)
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

@Composable
fun CategoryList(
    categoryCard: @Composable (category: CategoryDTO) -> Unit,
    categoryViewModel: CategoryViewModel = hiltViewModel()
) {
    val categoryList by categoryViewModel.categories.collectAsState()
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

@Composable
fun CategoryCard(
    category: CategoryDTO,
    editCallback: (() -> Unit)? = null,
    deleteCallback: (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.background
) {

    Card(
        modifier = Modifier
            .border(2.dp, MaterialTheme.colorScheme.tertiary, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .height(50.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, Color.Red, shape = RoundedCornerShape(8.dp))
                .background(backgroundColor)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Label,
                contentDescription = "Label",
                modifier = Modifier.weight(0.15f)
            )
            Text(
                category.category,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
            )

            if (deleteCallback != null) {
                IconButton(
                    onClick = deleteCallback,
                    modifier = Modifier.weight(0.2f)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
            }
            if (editCallback != null) {
                IconButton(
                    onClick = editCallback,
                    modifier = Modifier.weight(0.2f)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit"
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryForm(
    category: CategoryDTO? = null,
    categoryViewModel: CategoryViewModel = hiltViewModel()
) {
    var textState by remember { mutableStateOf(if (category is CategoryDTO) category.category else "") }

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
                    .padding(all = 8.dp)
            )

            Box (
                modifier = Modifier.padding(0.dp)
            ) {
                if (category === null) {
                    TagCapellaButton(
                        onClick = {
                            categoryViewModel.insertCategory(textState)
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
                            categoryViewModel.updateCategory(
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