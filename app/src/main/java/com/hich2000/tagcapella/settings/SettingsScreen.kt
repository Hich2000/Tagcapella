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
import com.hich2000.tagcapella.utils.navigation.LocalNavController
import com.hich2000.tagcapella.utils.navigation.NavItem
import com.hich2000.tagcapella.utils.composables.TagCapellaButton

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

