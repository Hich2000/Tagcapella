package com.hich2000.tagcapella.utils

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class SharedPreferenceManager @Inject constructor(
    application: Application
) {
    private val gson = Gson()

    private val sharedPreferences = application.getSharedPreferences(
        "com.hich2000.tagcapella.application_preferences",
        Context.MODE_PRIVATE
    )

    fun <T> savePreference(key: SharedPreferenceKey<T>, value: T) {
        sharedPreferences.edit {
            when (key) {
                is SharedPreferenceKey.PlayerRepeatMode -> putInt(key.key, value as Int)
                is SharedPreferenceKey.PlayerShuffleMode -> putBoolean(key.key, value as Boolean)
                is SharedPreferenceKey.LastSongPlayed -> putString(key.key, value as String)
                is SharedPreferenceKey.LastSongPosition -> putLong(key.key, value as Long)
                is SharedPreferenceKey.PermissionsAlreadyRequested -> putBoolean(key.key, value as Boolean)
                is SharedPreferenceKey.IncludedTags -> {
                    val json = gson.toJson(value)
                    putString(key.key, json)
                }
                is SharedPreferenceKey.ExcludedTags -> {
                    val json = gson.toJson(value)
                    putString(key.key, json)
                }
                is SharedPreferenceKey.FoldersToScan -> {
                    val json = gson.toJson(value)
                    putString(key.key, json)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getPreference(key: SharedPreferenceKey<T>, defaultValue: T): T {
        return when (key) {
            is SharedPreferenceKey.PlayerRepeatMode -> sharedPreferences.getInt(key.key, defaultValue as Int) as T
            is SharedPreferenceKey.PlayerShuffleMode -> sharedPreferences.getBoolean(key.key, defaultValue as Boolean) as T
            is SharedPreferenceKey.LastSongPlayed -> sharedPreferences.getString(key.key, defaultValue as String) as T
            is SharedPreferenceKey.LastSongPosition -> sharedPreferences.getLong(key.key, defaultValue as Long) as T
            is SharedPreferenceKey.PermissionsAlreadyRequested -> sharedPreferences.getBoolean(key.key, defaultValue as Boolean) as T
            is SharedPreferenceKey.IncludedTags -> {
                val json = sharedPreferences.getString(key.key, null)
                if (!json.isNullOrEmpty()) {
                    val type = object : TypeToken<List<String>>() {}.type
                    gson.fromJson<List<String>>(json, type) ?: defaultValue
                } else {
                    defaultValue
                }
            }
            is SharedPreferenceKey.ExcludedTags -> {
                val json = sharedPreferences.getString(key.key, null)
                if (!json.isNullOrEmpty()) {
                    val type = object : TypeToken<List<String>>() {}.type
                    gson.fromJson<List<String>>(json, type) ?: defaultValue
                } else {
                    defaultValue
                }
            }
            is SharedPreferenceKey.FoldersToScan -> {
                val json = sharedPreferences.getString(key.key, null)
                if (!json.isNullOrEmpty()) {
                    val type = object : TypeToken<List<String>>() {}.type
                    gson.fromJson<List<String>>(json, type) ?: defaultValue
                } else {
                    defaultValue
                }
            }

        } as T
    }
}