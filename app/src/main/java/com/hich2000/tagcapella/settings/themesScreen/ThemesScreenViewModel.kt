package com.hich2000.tagcapella.settings.themesScreen

import androidx.lifecycle.ViewModel
import com.hich2000.tagcapella.utils.sharedPreferences.SharedPreferenceKey
import com.hich2000.tagcapella.utils.sharedPreferences.SharedPreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ThemesScreenViewModel @Inject constructor(
    sharedPreferenceManager: SharedPreferenceManager
) : ViewModel() {

    var useSystemTheme: MutableStateFlow<Boolean> = MutableStateFlow(
        sharedPreferenceManager.getPreference(SharedPreferenceKey.UseSystemTheme, true)
    )

//    val selectedTheme: MutableStateFlow<String> = MutableStateFlow(
//        sharedPreferenceManager.getPreference(
//            SharedPreferenceKey.SelectedTheme,
//            SelectableThemes.LIGHTMODE.toString()
//        )
//    )
}