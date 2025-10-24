package com.hich2000.tagcapella.tagsAndCategories.tags.tagList

import androidx.compose.foundation.ScrollState
import androidx.lifecycle.ViewModel
import com.hich2000.tagcapella.tagsAndCategories.categories.Category
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

    val categories: StateFlow<List<Category>> get() = categoryRepository.categories

    fun setSelectedCategory(category: Long?) {
        _selectedCategory.value = category
    }

    //If there is only 1 category, and then you filter on it on top and then delete the category it causes a funny bug.
    // This basically prevents user from having to restart the app in this case by reverting to all filter
    // when the selected category no longer exists.
    fun verifySelectedCategory() {
        selectedCategory.value.let { selectedCategory ->
            if (!categories.value.any{ it.id == selectedCategory }) {
                _selectedCategory.value = null
            }
        }
    }
}