package com.hich2000.tagcapella.tags

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hich200.tagcapella.Tag
import com.hich2000.tagcapella.LocalTagViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TagList() {

    val tagViewModel = LocalTagViewModel.current
    val tags = remember { tagViewModel.tags }
    val columnScroll = rememberScrollState()
    val showDialog = remember { mutableStateOf(false) }
    val editDialogTag = remember { mutableStateOf<Tag?>(null) }

    if (showDialog.value) {
        BasicAlertDialog(
            onDismissRequest = {
                showDialog.value = false
                editDialogTag.value = null
            },
        ) {
            TagForm(
                tag = editDialogTag.value,
                tagViewModel = tagViewModel
            )
        }
    }


    Scaffold(
        floatingActionButton = {
            SmallFloatingActionButton(onClick = {
                showDialog.value = true
            }) {
                Icon(
                    Icons.Default.Add, contentDescription = "Add label"
                )
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, Color.Blue, shape = RoundedCornerShape(8.dp))
                .verticalScroll(columnScroll)
        ) {
            tags.forEach {
                TagCard(
                    tag = it,
                    tagViewModel = tagViewModel,
                    editCallBack = {
                        editDialogTag.value = it
                        showDialog.value = true
                    }
                )
            }
        }
    }
}

@Composable
fun TagCard(tag: Tag, tagViewModel: TagViewModel, editCallBack: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .border(2.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .background(Color.Gray)
            .height(75.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, Color.Red, shape = RoundedCornerShape(8.dp))
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Label, contentDescription = "Label"
            )
            Text(
                tag.tag,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )

            if (tag.id.toInt() != 0) {
                IconButton(
                    onClick = {
                        tagViewModel.deleteTag(tag.id)
                    },
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
                IconButton(
                    onClick = editCallBack
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
fun TagForm(tag: Tag? = null, tagViewModel: TagViewModel) {
    var textState by remember { mutableStateOf(if (tag is Tag) tag.tag else "") }

    Surface(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.large,
        tonalElevation = AlertDialogDefaults.TonalElevation
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            TextField(
                value = textState,
                onValueChange = { textState = it },
                label = { Text("Tag") },
            )

            if (tag === null) {
                Button(
                    onClick = {
                        tagViewModel.insertTag(textState)
                    },
                ) {
                    Text("add")
                }
            } else {
                Button(
                    onClick = {
                        tagViewModel.updateTag(
                            id = tag.id,
                            tag = textState
                        )
                    },
                ) {
                    Text("update")
                }
            }
        }
    }
}