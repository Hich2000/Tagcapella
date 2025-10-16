package com.hich2000.tagcapella.settings.folderScreen

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.hich2000.tagcapella.songs.FolderScanManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class FolderScreenViewModel @Inject constructor(
    private val folderScanManager: FolderScanManager
): ViewModel() {
    val foldersToScan: StateFlow<List<String>> get() = folderScanManager.foldersToScan

    fun addScanFolder(contentUri: Uri): Boolean {
        val folderPath: String? = contentUri.path?.split(":")?.last()
        if (folderPath.isNullOrEmpty()) {
            return false
        }
        return folderScanManager.addScanFolder("$folderPath/")
    }

    fun removeScanFolder(index: Int) {
        folderScanManager.removeScanFolder(index)
    }
}