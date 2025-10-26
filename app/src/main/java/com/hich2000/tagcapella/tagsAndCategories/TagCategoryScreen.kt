package com.hich2000.tagcapella.tagsAndCategories

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.hich2000.tagcapella.tagsAndCategories.categories.categoryScreen.CategoryScreen
import com.hich2000.tagcapella.tagsAndCategories.categories.forms.CategoryForm
import com.hich2000.tagcapella.tagsAndCategories.tags.forms.TagForm
import com.hich2000.tagcapella.tagsAndCategories.tags.tagScreen.TagScreen
import com.hich2000.tagcapella.utils.composables.ExpandableFab
import com.hich2000.tagcapella.utils.composables.TagCapellaButton

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TagCategoryScreen() {
    val selectedScreen = rememberSaveable { mutableIntStateOf(0) }
    val showTagDialog = remember { mutableStateOf(false) }
    val showCategoryDialog = remember { mutableStateOf(false) }
    val fabExpanded = remember { mutableStateOf(false) }

    if (showTagDialog.value) {
        BasicAlertDialog(
            onDismissRequest = {
                showTagDialog.value = false
            },
        ) {
            TagForm(
                onSaveAction = { showTagDialog.value = false }
            )
        }
    }

    if (showCategoryDialog.value) {
        BasicAlertDialog(
            onDismissRequest = {
                showCategoryDialog.value = false
            },
        ) {
            CategoryForm()
        }
    }

    Box(
        modifier = Modifier.Companion
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    if (fabExpanded.value) {
                        fabExpanded.value = false
                    }
                }
            ),
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                TagCapellaButton(
                    onClick = {
                        selectedScreen.intValue = 0
                    },
                    modifier = Modifier.Companion
                        .weight(1f)
                        .border(2.dp, MaterialTheme.colorScheme.tertiary, RectangleShape),
                    shape = RectangleShape,
                ) {
                    Text(
                        "Tags"
                    )
                }
                TagCapellaButton(
                    onClick = {
                        selectedScreen.intValue = 1
                    },
                    modifier = Modifier.Companion
                        .weight(1f)
                        .border(2.dp, MaterialTheme.colorScheme.tertiary, RectangleShape),
                    shape = RectangleShape,
                ) {
                    Text(
                        "Categories"
                    )
                }
            }

            Scaffold(
                modifier = Modifier.Companion.fillMaxSize(),
                floatingActionButton = {
                    ExpandableFab(
                        buttons = listOf {
                            TagCapellaButton(
                                onClick = {
                                    showTagDialog.value = true
                                },
                                modifier = Modifier.Companion
                                    .fillMaxWidth()
                                    .border(2.dp, MaterialTheme.colorScheme.tertiary)
                            ) {
                                Text("New Tag")
                            }
                            TagCapellaButton(
                                onClick = {
                                    showCategoryDialog.value = true
                                },
                                modifier = Modifier.Companion
                                    .fillMaxWidth()
                                    .border(2.dp, MaterialTheme.colorScheme.tertiary)
                            ) {
                                Text("New Category")
                            }
                        },
                        expanded = fabExpanded.value,
                        onclick = { fabExpanded.value = true }
                    )
                }
            ) {
                if (selectedScreen.intValue == 0) {
                    TagScreen()
                } else if (selectedScreen.intValue == 1) {
                    CategoryScreen()
                }
            }
        }
    }
}