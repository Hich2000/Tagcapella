package com.hich2000.tagcapella.music.controls

import android.app.Application
import androidx.lifecycle.ViewModel
import com.hich2000.tagcapella.music.mediaController.MediaControllerManager
import com.hich2000.tagcapella.songs.SongRepository
import com.hich2000.tagcapella.tagsAndCategories.tags.TagRepository
import com.hich2000.tagcapella.utils.sharedPreferences.SharedPreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MusicControlsViewModel @Inject constructor(
    private val mediaControllerManager: MediaControllerManager,
    private val songRepository: SongRepository,
    private val sharedPreferenceManager: SharedPreferenceManager,
    private val application: Application,
    private val tagRepository: TagRepository
) : ViewModel() {
}