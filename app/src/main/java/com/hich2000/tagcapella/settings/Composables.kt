package com.hich2000.tagcapella.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
fun FolderScreen(
    folderScanViewModel: FolderScanViewModel = hiltViewModel()
) {

    val folders by folderScanViewModel.foldersToScan.collectAsState()

    //this is how to request the system for permission for specific folders
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
            uri?.let { folderScanViewModel.addScanFolder(uri) }
        }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        folders.forEachIndexed { index, folder ->
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    folder,
                    modifier = Modifier
                        .weight(0.9f)
                )
                IconButton(
                    onClick = {
                        folderScanViewModel.removeScanFolder(index)
                    },
                    modifier = Modifier.weight(0.1f)
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
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("Add folder to scan")
        }
    }
}