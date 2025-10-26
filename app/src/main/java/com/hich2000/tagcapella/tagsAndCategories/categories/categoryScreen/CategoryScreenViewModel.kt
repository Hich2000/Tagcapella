package com.hich2000.tagcapella.tagsAndCategories.categories.categoryScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hich2000.tagcapella.tagsAndCategories.categories.Category
import com.hich2000.tagcapella.tagsAndCategories.categories.CategoryRepository
import com.hich2000.tagcapella.utils.ToastEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryScreenViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    val categories: StateFlow<List<Category>> get() = categoryRepository.categories

    private val _showDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> get() = _showDialog

    private val _selectedCategory: MutableStateFlow<Category?> = MutableStateFlow(null)
    val selectedCategory: StateFlow<Category?> get() = _selectedCategory


    fun insertCategory(category: String) {
        viewModelScope.launch {
            try {
                categoryRepository.insertCategory(category)
            } catch (_: Throwable) {
                ToastEventBus.send("Category already exists with name: $category")
            }
        }
    }

    fun updateCategory(id: Long, category: String) {
        viewModelScope.launch {
            try {
                categoryRepository.updateCategory(id = id, category = category)
            } catch (_: Exception) {
                ToastEventBus.send("Category already exists with name: $category")
            }
        }
    }

    fun deleteCategory(id: Long) {
        categoryRepository.deleteCategory(id)
    }

    fun openDialog(category: Category? = null) {
        _showDialog.value = true
        _selectedCategory.value = category
    }

    fun closeDialog() {
        _showDialog.value = false
        _selectedCategory.value = null
    }
}