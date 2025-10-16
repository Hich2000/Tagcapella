package com.hich2000.tagcapella.tags.tagList

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hich2000.tagcapella.categories.CategoryViewModel
import com.hich2000.tagcapella.tags.TagDTO
import com.hich2000.tagcapella.tags.TagViewModel
import com.hich2000.tagcapella.utils.TagCapellaButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagForm(
    tag: TagDTO? = null,
    tagViewModel: TagViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel()
) {
    var textState by remember { mutableStateOf(if (tag is TagDTO) tag.tag else "") }
    var dropdownState by remember { mutableStateOf(if (tag is TagDTO) tag.categoryId else null) }
    val categories by categoryViewModel.categories.collectAsState()
    var dropdownExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.Companion
            .wrapContentWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.large,
        tonalElevation = AlertDialogDefaults.TonalElevation
    ) {
        Column(
            modifier = Modifier.Companion
                .padding(16.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = RectangleShape
                )
        ) {

            BoxWithConstraints(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(all = 8.dp)
            ) {
                val dropdownWidth = this.maxWidth

                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = it },
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                ) {
                    TextField(
                        modifier = Modifier.Companion
                            .fillMaxWidth()
                            .menuAnchor(
                                type = MenuAnchorType.Companion.PrimaryEditable,
                                enabled = true
                            ),
                        label = { Text("category") },
                        onValueChange = {},
                        readOnly = true,
                        value = if (dropdownState == null) {
                            "(no category)"
                        } else {
                            categories.find { category -> category.id == dropdownState }?.category
                                ?: "(no category)"
                        },
                    )

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        shape = RectangleShape,
                        modifier = Modifier.Companion
                            .width(dropdownWidth)
                            .border(
                                2.dp, MaterialTheme.colorScheme.tertiary, shape = RectangleShape
                            )
                            .padding(0.dp)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "(no category)",
                                    textAlign = TextAlign.Companion.Center,
                                    modifier = Modifier.Companion.fillMaxSize(),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            },
                            onClick = {
                                dropdownState = null
                                dropdownExpanded = false
                            },
                            modifier = Modifier.Companion
                                .fillMaxSize()
                                .padding(0.dp)
                        )

                        categories.forEach {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = it.category,
                                        textAlign = TextAlign.Companion.Center,
                                        modifier = Modifier.Companion.fillMaxSize(),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                },
                                onClick = {
                                    dropdownState = it.id
                                    dropdownExpanded = false
                                },
                                modifier = Modifier.Companion
                                    .fillMaxSize()
                                    .padding(0.dp)
                            )
                        }
                    }
                }
            }

            TextField(
                value = textState,
                onValueChange = { textState = it },
                label = { Text("Tag") },
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(all = 8.dp)
            )

            Box(
                modifier = Modifier.Companion.padding(0.dp)
            ) {
                if (tag === null) {
                    TagCapellaButton(
                        onClick = {
                            tagViewModel.insertTag(textState, dropdownState)
                        },
                        modifier = Modifier.Companion
                            .fillMaxWidth()
                            .align(Alignment.Companion.BottomCenter)
                            .height(36.dp)
                            .border(2.dp, MaterialTheme.colorScheme.tertiary)
                            .padding(0.dp)
                    ) {
                        Text("add")
                    }
                } else {
                    TagCapellaButton(
                        onClick = {
                            tagViewModel.updateTag(
                                id = tag.id,
                                tag = textState,
                                category = dropdownState
                            )
                        },
                        modifier = Modifier.Companion
                            .fillMaxWidth()
                            .align(Alignment.Companion.BottomCenter)
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