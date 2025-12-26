package com.hich2000.tagcapella.settings.themesScreen

import androidx.lifecycle.ViewModel
import com.hich2000.tagcapella.theme.ThemeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ThemesScreenViewModel @Inject constructor(
    val themeState: ThemeState
) : ViewModel() {
    val useSystemTheme: StateFlow<Boolean> get() = themeState.useSystemTheme
    val selectedTheme: StateFlow<SelectableThemes> get() = themeState.selectedTheme
    fun handleUseSystemThemeCheckbox() = themeState.handleUseSystemThemeCheckbox()
    fun setSelectedTheme(theme: SelectableThemes) = themeState.setSelectedTheme(theme)
}