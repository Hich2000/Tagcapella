package com.hich2000.tagcapella.music.songScreen

import androidx.lifecycle.ViewModel
import com.hich2000.tagcapella.music.queueManager.Song
import com.hich2000.tagcapella.music.queueManager.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SongScreenViewModel @Inject constructor(
    private val songRepository: SongRepository,
) : ViewModel() {
    val songRepoInitialized: StateFlow<Boolean> get() = songRepository.isInitialized
    val songs: StateFlow<List<Song>> get() = songRepository.songList
}