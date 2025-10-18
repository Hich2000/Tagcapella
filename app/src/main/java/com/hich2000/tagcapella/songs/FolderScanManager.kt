package com.hich2000.tagcapella.songs

import com.hich2000.tagcapella.utils.sharedPreferences.SharedPreferenceKey
import com.hich2000.tagcapella.utils.sharedPreferences.SharedPreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FolderScanManager @Inject constructor(
    private val sharedPreferenceManager: SharedPreferenceManager
) {
    // Define a CoroutineScope for the repository
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _foldersToScan = MutableStateFlow<List<String>>(emptyList())
    val foldersToScan: StateFlow<List<String>> get() = _foldersToScan

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized

    init {
        repositoryScope.launch {
            loadFolders()
        }
    }

    suspend fun loadFolders() {
        val list = withContext(Dispatchers.IO) {
            sharedPreferenceManager.getPreference(SharedPreferenceKey.FoldersToScan, emptyList())
        }
        _foldersToScan.value = list
        _isInitialized.value = true
    }

    fun addScanFolder(folder: String): Boolean {
        //check if the folder already exists in the list first
        if (foldersToScan.value.contains(folder)) {
            return true
        }

        //fallback mechanism
        val currentList = _foldersToScan.value

        try {
            _foldersToScan.value = foldersToScan.value + folder
            sharedPreferenceManager.savePreference(
                SharedPreferenceKey.FoldersToScan,
                _foldersToScan.value
            )
            return true
        } catch (_: Exception) {
            _foldersToScan.value = currentList
            sharedPreferenceManager.savePreference(
                SharedPreferenceKey.FoldersToScan,
                currentList
            )
            return false
        }
    }

    fun removeScanFolder(index: Int) {
        val fallBackList = _foldersToScan.value

        try {
            val mutableList = fallBackList.toMutableList()
            mutableList.removeAt(index)
            _foldersToScan.value = mutableList
            sharedPreferenceManager.savePreference(
                SharedPreferenceKey.FoldersToScan,
                _foldersToScan.value
            )
        } catch (_: IndexOutOfBoundsException) {
            _foldersToScan.value = fallBackList
            sharedPreferenceManager.savePreference(
                SharedPreferenceKey.FoldersToScan,
                _foldersToScan.value
            )
        }
    }
}