package com.hich2000.tagcapella.categories

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private var _categories = MutableStateFlow<List<CategoryDTO>>(emptyList())
    val categories: StateFlow<List<CategoryDTO>> get() = _categories

    init {
        _categories.value = selectAllCategories()
    }

    private fun selectAllCategories(): List<CategoryDTO> {
        return categoryRepository.selectAllCategories()
    }

    fun insertCategory(category: String) {
        categoryRepository.insertCategory(category)
        _categories.value = selectAllCategories()
    }

    fun updateCategory(id: Long, category: String) {
        categoryRepository.updateCategory(id = id, category = category)
        _categories.value = selectAllCategories()
    }

    fun deleteCategory(id: Long) {
        val deleteIndex = _categories.value.indexOfFirst { it.id == id }
        categoryRepository.deleteCategory(_categories.value[deleteIndex].id)
        _categories.value = selectAllCategories()
    }
}