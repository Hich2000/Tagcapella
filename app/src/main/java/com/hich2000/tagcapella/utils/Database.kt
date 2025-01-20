package com.hich2000.tagcapella.utils

import android.app.Application
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.hich200.tagcapella.TagcapellaDb
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Database @Inject constructor(
    application: Application
) {

    private var _db: TagcapellaDb
    val db: TagcapellaDb get() = _db

    init {
        val driver: SqlDriver = AndroidSqliteDriver(
            TagcapellaDb.Schema,
            application,
            "tagcapella.db",
            callback = object : AndroidSqliteDriver.Callback(TagcapellaDb.Schema) {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    db.setForeignKeyConstraintsEnabled(true)
                }
            })
        _db = TagcapellaDb(driver)
    }

}