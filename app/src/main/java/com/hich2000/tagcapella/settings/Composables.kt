package com.hich2000.tagcapella.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hich2000.tagcapella.utils.LocalNavController
import com.hich2000.tagcapella.utils.NavItem
import com.hich2000.tagcapella.utils.TagCapellaButton

@Composable
fun SettingsScreen() {
    val navController = LocalNavController.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TagCapellaButton(
            onClick = {
//                now I can use the navController like this
                navController.navigate(NavItem.Settings.Folders.title)
            },
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, MaterialTheme.colorScheme.tertiary)
        ) {
            Text("Music folders")
        }
    }
}

@Composable
fun FolderScreen() {

    //this is how to request the system for permission for specific folders
//    registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { test ->
//        println("here")
//        println(test)
//    }.launch(null)

    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        Text("hello")
    }
}