package com.hich2000.tagcapella.tagsAndCategories.tags.tagScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hich2000.tagcapella.main.navigation.LocalNavController
import com.hich2000.tagcapella.main.navigation.Route
import com.hich2000.tagcapella.tagsAndCategories.tags.forms.TagForm
import com.hich2000.tagcapella.tagsAndCategories.tags.tagList.TagCard
import com.hich2000.tagcapella.tagsAndCategories.tags.tagList.TagList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagScreen(
    tagScreenViewModel: TagScreenViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val showTagDialog by tagScreenViewModel.showDialog.collectAsState()
    val clickedTag by tagScreenViewModel.clickedTag.collectAsState()
    val tagList by tagScreenViewModel.tags.collectAsState()

    if (showTagDialog) {
        BasicAlertDialog(
            onDismissRequest = {
                tagScreenViewModel.closeDialog()
                tagScreenViewModel.setClickedTag(null)
            },
        ) {
            TagForm(
                tag = clickedTag,
                onSaveAction = {
                    tagScreenViewModel.closeDialog()
                }
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        TagList(
            tagList = tagList,
            tagCard = { tag ->
                val editCallback = {
                    tagScreenViewModel.setClickedTag(tag)
                    tagScreenViewModel.openDialog()
                }
                val songCallback = {
                    navController.navigate(Route.Tags.Songs.createRoute(tag.id))
                }
                val deleteCallback = {
                    tagScreenViewModel.deleteTag(tag.id)
                }

                TagCard(
                    tag = tag,
                    editCallback = editCallback,
                    songCallback = songCallback,
                    deleteCallback = deleteCallback
                )
            }
        )
    }
}