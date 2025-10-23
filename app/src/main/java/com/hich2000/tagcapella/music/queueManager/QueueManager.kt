package com.hich2000.tagcapella.music.queueManager

import com.hich2000.tagcapella.music.Song
import com.hich2000.tagcapella.music.SongRepository
import com.hich2000.tagcapella.tagsAndCategories.tags.TagDTO
import com.hich2000.tagcapella.tagsAndCategories.tags.TagRepository
import com.hich2000.tagcapella.utils.sharedPreferences.SharedPreferenceKey
import com.hich2000.tagcapella.utils.sharedPreferences.SharedPreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QueueManager @Inject constructor(
    private val songRepository: SongRepository,
    private val tagRepository: TagRepository,
    private val sharedPreferenceManager: SharedPreferenceManager
) {

    private val _currentQueue: MutableStateFlow<List<Song>> = MutableStateFlow(emptyList())
    val currentQueue: StateFlow<List<Song>> get() = _currentQueue

    private var _includedTags = MutableStateFlow<List<TagDTO>>(emptyList())
    val includedTags: StateFlow<List<TagDTO>> get() = _includedTags

    private val _excludedTags = MutableStateFlow<List<TagDTO>>(emptyList())
    val excludedTags: StateFlow<List<TagDTO>> get() = _excludedTags

    suspend fun updateQueue() {
        val newQueue = songRepository.filterSongList(_includedTags.value, _excludedTags.value)
        _currentQueue.value = newQueue
    }

    suspend fun initFilters() {
        //get included and excluded tag ids
        val includedTagIds: List<Long> = sharedPreferenceManager.getPreference(
            SharedPreferenceKey.IncludedTags,
            listOf()
        )
        val excludedTagIds: List<Long> = sharedPreferenceManager.getPreference(
            SharedPreferenceKey.ExcludedTags,
            listOf()
        )

        //use the list of ids to make a list of DTOs
        _includedTags.value = includedTagIds.map { tagRepository.getTagById(it)!! }
        _excludedTags.value = excludedTagIds.map { tagRepository.getTagById(it)!! }

        updateQueue()
    }

    suspend fun addIncludedTag(tag: TagDTO) {
        _includedTags.value = _includedTags.value + tag
        saveTagsFilters()
        updateQueue()
    }

    suspend fun removeIncludedTag(tag: TagDTO) {
        _includedTags.value = _includedTags.value - tag
        saveTagsFilters()
        updateQueue()
    }

    suspend fun addExcludedTag(tag: TagDTO) {
        _excludedTags.value = _excludedTags.value + tag
        saveTagsFilters()
        updateQueue()
    }

    suspend fun removeExcludedTag(tag: TagDTO) {
        _excludedTags.value = _excludedTags.value - tag
        saveTagsFilters()
        updateQueue()
    }

    private fun saveTagsFilters() {
        val includedIds = _includedTags.value.map { it.id }
        val excludedIds = _excludedTags.value.map { it.id }

        sharedPreferenceManager.savePreference(SharedPreferenceKey.IncludedTags, includedIds)
        sharedPreferenceManager.savePreference(SharedPreferenceKey.ExcludedTags, excludedIds)
    }

}