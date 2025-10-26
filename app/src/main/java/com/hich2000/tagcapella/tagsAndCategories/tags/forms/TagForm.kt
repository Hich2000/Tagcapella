package com.hich2000.tagcapella.tagsAndCategories.tags.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hich2000.tagcapella.tagsAndCategories.tags.Tag
import com.hich2000.tagcapella.utils.composables.TagCapellaButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagForm(
    tag: Tag? = null,
    onSaveAction: () -> Unit,
    tagFormViewModel: TagFormViewModel = hiltViewModel()
) {

    DisposableEffect(Unit) {
        //this resets the state of te viewmodel if this composable is removed from composition tree
        //otherwise the text state and dropdown state remain if you dismiss the dialog without updating/inserting
        onDispose {
            tagFormViewModel.resetState()
        }
    }

    val textState by tagFormViewModel.textState.collectAsState()
    val dropdownState by tagFormViewModel.dropdownState.collectAsState()
    val categories by tagFormViewModel.categories.collectAsState()
    val dropdownExpanded by tagFormViewModel.dropdownExpanded.collectAsState()

    if (tag !== null) {
        tagFormViewModel.setUpdateState(tag)
    }

    Column(
        modifier = Modifier.Companion
            .padding(16.dp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.tertiary,
                shape = RectangleShape
            )
            .background(MaterialTheme.colorScheme.background)
    ) {

        BoxWithConstraints(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(all = 8.dp)
        ) {
            val dropdownWidth = this.maxWidth

            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { tagFormViewModel.toggleDropdown() },
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
                    onDismissRequest = { tagFormViewModel.closeDropdown() },
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
                            tagFormViewModel.setDropdownState(null)
                            tagFormViewModel.closeDropdown()
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
                                tagFormViewModel.setDropdownState(it.id)
                                tagFormViewModel.closeDropdown()
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
            onValueChange = {
                tagFormViewModel.setTextState(it)
            },
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
                        tagFormViewModel.insertTag()
                        onSaveAction()
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
                        tagFormViewModel.updateTag(tag.id)
                        onSaveAction()
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