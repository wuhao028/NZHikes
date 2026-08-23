package com.hao.nzhikes.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.annotation.StringRes
import com.hao.nzhikes.R

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    @StringRes val titleRes: Int
) {
    object Home : BottomNavItem("home", Icons.Default.Home, R.string.nav_home)
    object Trips : BottomNavItem("trips", Icons.Default.Place, R.string.nav_trips)
    object Me : BottomNavItem("me", Icons.Default.Person, R.string.nav_me)
    object Search : BottomNavItem("search", Icons.Default.Search, R.string.nav_search)
}
