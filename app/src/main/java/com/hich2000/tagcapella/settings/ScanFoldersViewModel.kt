package com.hich2000.tagcapella.settings

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.hich2000.tagcapella.utils.SharedPreferenceKey
import com.hich2000.tagcapella.utils.SharedPreferenceManager
import javax.inject.Inject

class ScanFoldersViewModel @Inject constructor(
    val sharedPreferenceManager: SharedPreferenceManager
) {

    private var _foldersToScan = mutableStateListOf<String>()
    val foldersToScan: SnapshotStateList<String> get() = _foldersToScan

    init {
        val savedList =
            sharedPreferenceManager.getPreference(SharedPreferenceKey.FoldersToScan, emptyList())
        _foldersToScan = savedList.toMutableStateList()
    }

    fun addFolder(folder: String): Boolean {
        //check if the folder already exists in the list first
        if (_foldersToScan.contains(folder)) {
            return true
        }

        if (_foldersToScan.add(folder)) {
            sharedPreferenceManager.savePreference(SharedPreferenceKey.FoldersToScan, _foldersToScan)
            return true
        }

        return false
    }

}