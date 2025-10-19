package com.hich2000.tagcapella.tagsAndCategories.categories

import com.hich200.tagcapella.TagcapellaDb
import com.hich2000.tagcapella.utils.Database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    database: Database
) {

    private val db: TagcapellaDb = database.db

    private var _categories = MutableStateFlow<List<CategoryDTO>>(emptyList())
    val categories: StateFlow<List<CategoryDTO>> get() = _categories

    init {
        initCategoryList()
    }

    fun initCategoryList() {
        _categories.value = selectAllCategories()
    }

    fun selectAllCategories(): List<CategoryDTO> {
        return db.categoryQueries.selectAll { id, category -> CategoryDTO(id, category) }.executeAsList()
            .toList()
    }

    fun insertCategory(newCategory: String): CategoryDTO {
        db.categoryQueries.insertCategory(newCategory)
        initCategoryList()
        return db.categoryQueries.lastInsertedCategory { id, category -> CategoryDTO(id, category) }
            .executeAsOne()
    }

    fun updateCategory(id: Long, category: String) {
        db.categoryQueries.updateCategory(category, id)
        initCategoryList()
    }

    fun deleteCategory(id: Long) {
        db.categoryQueries.deleteCategory(id)
        initCategoryList()
    }
}