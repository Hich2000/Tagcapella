package com.hich2000.tagcapella.tagsAndCategories.tags.dialogs

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.hich2000.tagcapella.tagsAndCategories.tags.Tag
import com.hich2000.tagcapella.tagsAndCategories.tags.tagList.TagList
import com.hich2000.tagcapella.utils.composables.TagCapellaButton

@Composable
fun TagDialog(
    onButtonPress: () -> Unit = {},
    tagCardComposable: @Composable (tag: Tag) -> Unit,
    onDismissRequest: () -> Unit = {},
    tagDialogViewModel: TagDialogViewModel = hiltViewModel(),
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        val tagList by tagDialogViewModel.tags.collectAsState()

        Card(
            shape = CutCornerShape(0.dp),
            modifier = Modifier
                .padding(top = 16.dp, bottom = 16.dp)
                .fillMaxSize()
                .border(2.dp, MaterialTheme.colorScheme.tertiary)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                TagList(
                    tagList = tagList,
                    tagCard = tagCardComposable,
                )

                TagCapellaButton(
                    onClick = {
                        onButtonPress()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .height(36.dp)
                        .border(2.dp, MaterialTheme.colorScheme.tertiary)
                ) {
                    Text(text = "Save")
                }
            }
        }
    }
}