package com.hich2000.tagcapella.tags

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hich200.tagcapella.Tags
import com.hich2000.tagcapella.LocalTagViewModel

@Composable
fun TagList() {

    val tagViewModel = LocalTagViewModel.current
    val tags = remember { tagViewModel.tags }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .border(2.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
    ) {
        tags.forEach {
            TagCard(it)
        }
    }

}

@Composable
fun TagCard(tag: Tags) {
    Card(
        modifier = Modifier
            .border(2.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .background(Color.Gray)
            .height(75.dp)
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, Color.Red, shape = RoundedCornerShape(8.dp))
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Label,
                contentDescription = "Label"
            )
            Text(
                tag.tag,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
            )
        }
    }
}