package com.hich2000.tagcapella.tagsAndCategories.tags.dialogs

import androidx.lifecycle.ViewModel
import com.hich2000.tagcapella.tagsAndCategories.tags.TagDTO
import com.hich2000.tagcapella.tagsAndCategories.tags.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class TagDialogViewModel @Inject constructor(
    private val tagRepository: TagRepository,
) : ViewModel() {
    val tags: StateFlow<List<TagDTO>> get() = tagRepository.tags
}