package com.hich2000.tagcapella.tags.tagList

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hich2000.tagcapella.tags.TagDTO

@Composable
fun TagCard(
    tag: TagDTO,
    editCallback: (() -> Unit)? = null,
    songCallback: (() -> Unit)? = null,
    deleteCallback: (() -> Unit)? = null,
    onClick: (tag: TagDTO) -> Unit = {},
    backgroundColor: Color = MaterialTheme.colorScheme.background,
) {
    val taggedSongs by tag.taggedSongs.collectAsState()

    Card(
        modifier = Modifier.Companion
            .border(2.dp, MaterialTheme.colorScheme.tertiary, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.tertiary)
            .height(50.dp),
        onClick = { onClick(tag) }
    ) {
        Row(
            verticalAlignment = Alignment.Companion.CenterVertically,
            modifier = Modifier.Companion
                .fillMaxSize()
                .background(backgroundColor)
                .padding(horizontal = 8.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Label,
                contentDescription = "Label",
                modifier = Modifier.Companion.weight(0.15f),
                tint = MaterialTheme.colorScheme.secondary
            )
            Text(
                tag.tag,
                textAlign = TextAlign.Companion.Center,
                modifier = Modifier.Companion
                    .align(Alignment.Companion.CenterVertically)
                    .weight(1f),
                color = MaterialTheme.colorScheme.onBackground
            )

            if (deleteCallback != null) {
                IconButton(
                    onClick = deleteCallback,
                    modifier = Modifier.Companion.weight(0.2f)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            if (editCallback != null) {
                IconButton(
                    onClick = editCallback,
                    modifier = Modifier.Companion.weight(0.2f)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            if (songCallback != null) {
                IconButton(
                    onClick = songCallback,
                    modifier = Modifier.Companion.weight(0.2f)
                ) {
                    Icon(
                        Icons.Default.MusicNote,
                        contentDescription = "Tag songs",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
                Text(
                    "(${taggedSongs.count()})",
                    modifier = Modifier.Companion.weight(0.3f),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}