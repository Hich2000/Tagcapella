package com.hich2000.tagcapella.tagsAndCategories.tags.forms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hich2000.tagcapella.tagsAndCategories.categories.CategoryRepository
import com.hich2000.tagcapella.tagsAndCategories.tags.TagDTO
import com.hich2000.tagcapella.tagsAndCategories.tags.TagRepository
import com.hich2000.tagcapella.utils.ToastEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagFormViewModel @Inject constructor(
    private val tagRepository: TagRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _isUpdate: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isUpdate: StateFlow<Boolean> get() = _isUpdate

    private val _textState: MutableStateFlow<String> = MutableStateFlow("")
    val textState: StateFlow<String> get() = _textState

    private val _dropdownState: MutableStateFlow<Long?> = MutableStateFlow(null)
    val dropdownState: StateFlow<Long?> get() = _dropdownState

    private val _dropdownExpanded: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val dropdownExpanded: StateFlow<Boolean> get() = _dropdownExpanded

    val categories get() = categoryRepository.categories

    fun setUpdateState(tag: TagDTO) {
        if (!isUpdate.value) {
            setTextState(tag.tag)
            setDropdownState(tag.categoryId)
            _isUpdate.value = true
        }
    }

    fun insertTag() {
        viewModelScope.launch {
            try {
                tagRepository.insertTag(textState.value, dropdownState.value)
                resetState()
            } catch (_: Throwable) {
                ToastEventBus.send("Tag already exists with name: ${textState.value}")
            }
        }
    }

    fun updateTag(tagId: Long) {
        viewModelScope.launch {
            try {
                tagRepository.updateTag(tagId, textState.value, dropdownState.value)
                resetState()
            } catch (_: Throwable) {
                ToastEventBus.send("Tag already exists with name: ${textState.value}")
            }
        }
    }

    fun resetState() {
        _textState.value = ""
        _dropdownState.value = null
        _isUpdate.value = false
        closeDropdown()
    }

    fun toggleDropdown() {
        _dropdownExpanded.value = !_dropdownExpanded.value
    }

    fun closeDropdown() {
        _dropdownExpanded.value = false
    }

    fun setDropdownState(value: Long?) {
        _dropdownState.value = value
    }

    fun setTextState(text: String) {
        _textState.value = text
    }
}