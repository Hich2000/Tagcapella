package com.hich2000.tagcapella.songs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    private val songRepository: SongRepository,
    private val folderScanManager: FolderScanManager
) : ViewModel() {
    val songList: StateFlow<List<Song>> get() = songRepository.songList

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> get() = _isInitialized

    init {
        initializeSongList()
    }

    private fun initializeSongList() {
        viewModelScope.launch {
            folderScanManager.isInitialized.first { it }
            songRepository.triggerScan()
            _isInitialized.value = true
        }
    }
}