package com.hich2000.tagcapella.tagsAndCategories.tags.tagList

import androidx.compose.foundation.ScrollState
import androidx.lifecycle.ViewModel
import com.hich2000.tagcapella.tagsAndCategories.categories.CategoryDTO
import com.hich2000.tagcapella.tagsAndCategories.categories.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class TagListViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val _columnScroll = MutableStateFlow(ScrollState(0))
    val columnScrollState: StateFlow<ScrollState> get() = _columnScroll

    private val _scrollState = MutableStateFlow(ScrollState(0))
    val scrollState: StateFlow<ScrollState> get() = _scrollState

    private val _selectedCategory = MutableStateFlow<Long?>(null)
    val selectedCategory: StateFlow<Long?> get() = _selectedCategory

    val categories: StateFlow<List<CategoryDTO>> get() = categoryRepository.categories

    fun setSelectedCategory(category: Long?) {
        _selectedCategory.value = category
    }
}