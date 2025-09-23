package com.hich2000.tagcapella.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hich2000.tagcapella.utils.ToastEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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
        viewModelScope.launch {
            try {
                categoryRepository.insertCategory(category)
                _categories.value = selectAllCategories()
            } catch (_: Throwable) {
                ToastEventBus.send("Category already exists with name: $category")
            }
        }
    }

    fun updateCategory(id: Long, category: String) {
        viewModelScope.launch {
            try {
                categoryRepository.updateCategory(id = id, category = category)
                _categories.value = selectAllCategories()
            } catch (_: Exception) {
                ToastEventBus.send("Category already exists with name: $category")
            }
        }
    }

    fun deleteCategory(id: Long) {
        val deleteIndex = _categories.value.indexOfFirst { it.id == id }
        categoryRepository.deleteCategory(_categories.value[deleteIndex].id)
        _categories.value = selectAllCategories()
    }
}