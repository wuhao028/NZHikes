package com.hao.nzhikes.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val title: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Trips : BottomNavItem("trips", Icons.Default.Place, "Trips")
    object Me : BottomNavItem("me", Icons.Default.Person, "Me")
}
