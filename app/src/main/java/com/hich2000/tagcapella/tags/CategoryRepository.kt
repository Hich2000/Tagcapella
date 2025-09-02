package com.hich2000.tagcapella.tags

import com.hich200.tagcapella.TagcapellaDb
import com.hich2000.tagcapella.utils.Database
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val database: Database
) {

    private val db: TagcapellaDb = database.db

    fun selectAllCategories(): List<CategoryDTO> {
        return db.categoryQueries.selectAll { id, category -> CategoryDTO(id, category, database) }.executeAsList()
            .toList()
    }

    fun insertCategory(newCategory: String): CategoryDTO {
        db.categoryQueries.insertCategory(newCategory)
        return db.categoryQueries.lastInsertedCategory { id, category -> CategoryDTO(id, category, database) }
            .executeAsOne()
    }

    fun updateCategory(id: Long, category: String) {
        db.categoryQueries.updateCategory(category, id)
        //todo add something here to check if the update was successful
    }

    fun deleteCategory(id: Long) {
        db.categoryQueries.deleteCategory(id)
        //todo add something here to check if the delete was successful
    }
}