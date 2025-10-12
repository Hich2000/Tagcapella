package com.hich2000.tagcapella.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.hich2000.tagcapella.songs.FolderScanManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FolderScanViewModel @Inject constructor(
    private val folderScanManager: FolderScanManager
) : ViewModel() {
    val foldersToScan = folderScanManager.foldersToScan

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