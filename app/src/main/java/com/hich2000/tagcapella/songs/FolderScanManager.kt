package com.hich2000.tagcapella.songs

import androidx.compose.runtime.mutableStateListOf
import com.hich2000.tagcapella.utils.SharedPreferenceKey
import com.hich2000.tagcapella.utils.SharedPreferenceManager
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
    val folders = mutableStateListOf<String>()
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
        folders.clear()
        folders.addAll(list)
        _isInitialized.value = true
    }

    fun addFolder(folder: String): Boolean {
        //check if the folder already exists in the list first
        if (folders.contains(folder)) {
            return true
        }

        if (folders.add(folder)) {
            sharedPreferenceManager.savePreference(
                SharedPreferenceKey.FoldersToScan,
                folders
            )
            return true
        }

        return false
    }
}