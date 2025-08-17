package com.hao.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object ThemeManager {
    var isDarkMode by mutableStateOf(false)
        private set
    
    fun toggleDarkMode() {
        isDarkMode = !isDarkMode
    }
    
    fun updateDarkMode(enabled: Boolean) {
        isDarkMode = enabled
    }
}
