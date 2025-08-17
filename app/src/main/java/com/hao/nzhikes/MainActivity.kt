package com.hao.nzhikes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.hao.nzhikes.navigation.AppNavigation
import com.hao.nzhikes.ui.theme.NZHikesTheme
import com.hao.ui.theme.ThemeManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NZHikesTheme {
                AppNavigation()
            }
        }
    }
}