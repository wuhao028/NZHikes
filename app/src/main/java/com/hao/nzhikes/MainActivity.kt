package com.hao.nzhikes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import com.hao.nzhikes.navigation.AppNavigation
import com.hao.nzhikes.ui.theme.GlobalThemeState
import com.hao.nzhikes.ui.theme.NZHikesTheme
import com.hao.ui.theme.ThemeManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var themeManager: ThemeManager
    
    private var isInitialized by mutableStateOf(false)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        
        initializeThemeManager()
        
        setContent {
            MainContent(
                themeManager = themeManager,
                isInitialized = isInitialized,
                onInitialized = { isInitialized = true }
            )
        }
    }
    
    private fun initializeThemeManager() {
        lifecycleScope.launch {
            try {
                themeManager.initialize()
                isInitialized = true
            } catch (exception: Exception) {
                isInitialized = true
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
    }
    
    override fun onPause() {
        super.onPause()
    }
    
    override fun onDestroy() {
        super.onDestroy()
    }
}

@Composable
private fun MainContent(
    themeManager: ThemeManager,
    isInitialized: Boolean,
    onInitialized: () -> Unit
) {
    val isDarkMode by themeManager.darkModeFlow.collectAsState(initial = false)
    
    LaunchedEffect(isDarkMode) {
        GlobalThemeState.isDarkMode = isDarkMode
    }
    
    LaunchedEffect(isInitialized) {
        if (isInitialized) {
            onInitialized()
        }
    }
    
    NZHikesTheme(darkTheme = isDarkMode) {
        AppNavigation()
    }
}