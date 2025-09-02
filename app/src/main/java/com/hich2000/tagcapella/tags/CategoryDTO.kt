package com.hich2000.tagcapella.tags

import com.hich2000.tagcapella.utils.Database
import javax.inject.Inject

class CategoryDTO @Inject constructor(val id: Long, val category: String, val database: Database) {

    private var db = database.db

}

class CategoryDTOFactory @Inject constructor(private val database: Database) {

    fun getCategorybyId(categoryId: Long): CategoryDTO? {
        val db = database.db
        val categoryResult = db.categoryQueries.selectCategoryById(categoryId) { id, category ->
            CategoryDTO(id, category, database)
        }.executeAsOneOrNull()

        return categoryResult
    }
}