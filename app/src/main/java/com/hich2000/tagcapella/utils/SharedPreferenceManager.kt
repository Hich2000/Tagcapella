package com.hich2000.tagcapella.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KClass

@Singleton
class SharedPreferenceManager @Inject constructor(
    application: Application
) {

    private val gson = Gson()

    private val sharedPreferences = application.getSharedPreferences(
        "com.hich2000.tagcapella.application_preferences",
        Context.MODE_PRIVATE
    )

    private val putTypeHandlers: Map<KClass<*>, (SharedPreferences.Editor, String, Any) -> Unit> =
        mapOf(
            Int::class to { editor, key, value -> editor.putInt(key, value as Int) },
            Boolean::class to { editor, key, value -> editor.putBoolean(key, value as Boolean) },
            String::class to { editor, key, value -> editor.putString(key, value as String) },
            Long::class to { editor, key, value -> editor.putLong(key, value as Long) },
            List::class to { editor, key, value ->
                if (value is List<*>) {
                    val json = gson.toJson(value)
                    editor.putString(key, json)
                } else {
                    throw IllegalArgumentException("Expected List but got ${value::class}")
                }
            }
        )

    private val getTypeHandlers: Map<KClass<*>, (SharedPreferences, String, Any) -> Any?> = mapOf(
        Int::class to { store, key, defaultValue -> store.getInt(key, defaultValue as Int) },
        Boolean::class to { store, key, defaultValue ->
            store.getBoolean(
                key,
                defaultValue as Boolean
            )
        },
        String::class to { store, key, defaultValue ->
            store.getString(
                key,
                defaultValue as String
            )
        },
        Long::class to { store, key, defaultValue ->
            store.getLong(
                key,
                defaultValue as Long
            )
        },
        List::class to { store, key, defaultValue -> // Retrieve List<String>
            val json = store.getString(key, null)
            try {
                if (!json.isNullOrEmpty()) {
                    val type = object : TypeToken<List<String>>() {}.type
                    gson.fromJson<List<String>>(json, type) ?: defaultValue
                } else {
                    defaultValue
                }
            } catch (e: Throwable) {
                defaultValue
            }
        }
    )

    fun savePreference(key: SharedPreferenceKeys, value: Any) {
        val handler = putTypeHandlers[key.type]
            ?: throw IllegalArgumentException("Unsupported type: ${key.type}")
        val editor = sharedPreferences.edit()
        handler(editor, key.key, value)
        editor.apply()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getPreference(
        key: SharedPreferenceKeys,
        defaultValue: T
    ): T {
        val handler = getTypeHandlers[key.type]
            ?: throw IllegalArgumentException("Unsupported type: ${key.type}")
        return handler(sharedPreferences, key.key, defaultValue) as T
    }
}