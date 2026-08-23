package com.hao.nzhikes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hao.nzhikes.navigation.AppNavigation
import com.hao.nzhikes.ui.theme.NZHikesTheme
import com.hao.ui.theme.ThemeManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var themeManager: ThemeManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        
        setContent {
            MainContent(themeManager = themeManager)
        }
    }
}

@Composable
private fun MainContent(themeManager: ThemeManager) {
    val isDarkMode by themeManager.darkModeFlow.collectAsStateWithLifecycle(initialValue = false)
    
    NZHikesTheme(darkTheme = isDarkMode) {
        AppNavigation()
    }
}
