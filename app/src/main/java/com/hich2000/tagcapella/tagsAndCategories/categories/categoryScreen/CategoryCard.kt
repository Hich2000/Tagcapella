package com.hich2000.tagcapella.tagsAndCategories.categories.categoryScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hich2000.tagcapella.tagsAndCategories.categories.Category

@Composable
fun CategoryCard(
    category: Category,
    editCallback: (() -> Unit)? = null,
    deleteCallback: (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.background
) {

    Card(
        modifier = Modifier.Companion
            .border(2.dp, MaterialTheme.colorScheme.tertiary, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .height(50.dp),
    ) {
        Row(
            verticalAlignment = Alignment.Companion.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.Companion
                .fillMaxSize()
                .border(
                    2.dp,
                    Color.Companion.Red,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                )
                .background(backgroundColor)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Label,
                contentDescription = "Label",
                modifier = Modifier.Companion.weight(0.15f),
                tint = MaterialTheme.colorScheme.secondary
            )
            Text(
                category.category,
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
        }
    }
}