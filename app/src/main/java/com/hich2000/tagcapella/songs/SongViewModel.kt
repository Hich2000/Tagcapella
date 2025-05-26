package com.hich2000.tagcapella.songs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hich2000.tagcapella.tags.TagDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    private val songRepository: SongRepository
) : ViewModel() {

    private val _songList = MutableStateFlow<List<Song>>(emptyList())
    val songList: StateFlow<List<Song>> get() = _songList

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> get() = _isInitialized

    init {
        initializeSongList()
    }

    private fun initializeSongList() {
        viewModelScope.launch {
            val scannedSongs = songRepository.scanMusicFolder()
            _songList.value = scannedSongs
            _isInitialized.value = true
        }
    }

    fun filterSongs(includeTags: List<TagDTO>, excludeTags: List<TagDTO>) {
        viewModelScope.launch {
            val filteredList = songRepository.filterSongList(includeTags, excludeTags)
            _songList.value = filteredList
        }
    }
}