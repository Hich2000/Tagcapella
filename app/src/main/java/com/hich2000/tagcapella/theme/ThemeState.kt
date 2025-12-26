package com.hich2000.tagcapella.theme

import com.hich2000.tagcapella.settings.themesScreen.SelectableThemes
import com.hich2000.tagcapella.utils.sharedPreferences.SharedPreferenceKey
import com.hich2000.tagcapella.utils.sharedPreferences.SharedPreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeState @Inject constructor(
    val sharedPreferenceManager: SharedPreferenceManager
) {
    private val _useSystemTheme: MutableStateFlow<Boolean> = MutableStateFlow(
        sharedPreferenceManager.getPreference(SharedPreferenceKey.UseSystemTheme, true)
    )
    val useSystemTheme: StateFlow<Boolean> get() = _useSystemTheme

    private val _selectedTheme: MutableStateFlow<SelectableThemes> = MutableStateFlow(
        sharedPreferenceManager.getPreference(SharedPreferenceKey.SelectedTheme, SelectableThemes.LIGHTMODE)
    )
    val selectedTheme: StateFlow<SelectableThemes> get() = _selectedTheme


    fun handleUseSystemThemeCheckbox() {
        _useSystemTheme.value = !_useSystemTheme.value
        sharedPreferenceManager.savePreference(SharedPreferenceKey.UseSystemTheme, _useSystemTheme.value)
    }

    fun setSelectedTheme(theme: SelectableThemes) {
        _selectedTheme.value = theme
        sharedPreferenceManager.savePreference(SharedPreferenceKey.SelectedTheme, _selectedTheme.value)
    }
}