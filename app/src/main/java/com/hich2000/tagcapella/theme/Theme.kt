package com.hich2000.tagcapella.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.hich2000.tagcapella.settings.themesScreen.SelectableThemes
import com.hich2000.tagcapella.utils.sharedPreferences.SharedPreferenceKey
import com.hich2000.tagcapella.utils.sharedPreferences.SharedPreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

val DarkColorScheme = darkColorScheme(
    primary = Color.Black,
    secondary = Color.LightGray,
    tertiary = Color.Gray,
    onBackground = Color.LightGray
)

val LightColorScheme = lightColorScheme(
    primary = Color.White,
    secondary = Color.DarkGray,
    tertiary = Color.LightGray,
    onBackground = Color.Black
)

@Composable
fun TagcapellaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    themeViewModel: ThemeViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val sharedPreferenceManager = themeViewModel.sharedPreferenceManager
    val useSystemTheme =
        sharedPreferenceManager.getPreference(SharedPreferenceKey.UseSystemTheme, true)

    val colorScheme = if (useSystemTheme) {
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }
    } else {
        val selectedTheme = sharedPreferenceManager.getPreference(
            SharedPreferenceKey.SelectedTheme,
            SelectableThemes.DARKMODE
        )

        if (selectedTheme == SelectableThemes.DARKMODE) {
            DarkColorScheme
        } else {
            LightColorScheme
        }
    }

    //save the current color scheme to shared preference
    if (colorScheme == DarkColorScheme) {
        sharedPreferenceManager.savePreference(SharedPreferenceKey.SelectedTheme, SelectableThemes.DARKMODE)
    } else {
        sharedPreferenceManager.savePreference(SharedPreferenceKey.SelectedTheme, SelectableThemes.LIGHTMODE)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@HiltViewModel
class ThemeViewModel @Inject constructor(
    val sharedPreferenceManager: SharedPreferenceManager
) : ViewModel() {
}