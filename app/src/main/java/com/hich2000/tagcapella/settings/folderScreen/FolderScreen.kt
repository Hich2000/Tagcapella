package com.hich2000.tagcapella.settings.folderScreen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.hich2000.tagcapella.utils.composables.TagCapellaButton

@Composable
fun FolderScreen(
    folderScreenViewModel: FolderScreenViewModel = hiltViewModel()
) {

    val folders by folderScreenViewModel.foldersToScan.collectAsState()

    //this is how to request the system for permission for specific folders
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
            uri?.let { folderScreenViewModel.addScanFolder(uri) }
        }

    Column(
        modifier = Modifier.Companion.fillMaxSize()
    ) {
        folders.forEachIndexed { index, folder ->
            Row(
                modifier = Modifier.Companion.fillMaxWidth()
            ) {
                Text(
                    folder,
                    modifier = Modifier.Companion
                        .weight(0.9f)
                )
                IconButton(
                    onClick = {
                        folderScreenViewModel.removeScanFolder(index)
                    },
                    modifier = Modifier.Companion.weight(0.1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete scan folder",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
        TagCapellaButton(
            onClick = {
                launcher.launch(null)
            },
            modifier = Modifier.Companion
                .fillMaxWidth()
        ) {
            Text("Add folder to scan")
        }
    }
}