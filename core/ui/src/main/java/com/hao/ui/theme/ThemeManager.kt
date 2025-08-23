package com.hao.ui.theme

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private object ThemePreferencesKeys {
    val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
}

@Singleton
class ThemeManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")
    
    var isDarkMode by mutableStateOf(false)
        private set

    val darkModeFlow: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
            }
        }
        .map { preferences ->
            preferences[ThemePreferencesKeys.DARK_MODE_KEY] ?: false
        }

    suspend fun toggleDarkMode() {
        updateDarkMode(!isDarkMode)
    }

    suspend fun updateDarkMode(enabled: Boolean) {
        try {
            context.dataStore.edit { preferences ->
                preferences[ThemePreferencesKeys.DARK_MODE_KEY] = enabled
            }
            isDarkMode = enabled
        } catch (exception: IOException) {
            throw ThemeManagerException("Failed to save theme preference", exception)
        }
    }

    suspend fun initialize() {
        try {
            context.dataStore.data.collect { preferences ->
                isDarkMode = preferences[ThemePreferencesKeys.DARK_MODE_KEY] ?: false
            }
        } catch (exception: IOException) {
            isDarkMode = false
        }
    }
}

class ThemeManagerException(message: String, cause: Throwable? = null) : Exception(message, cause)
