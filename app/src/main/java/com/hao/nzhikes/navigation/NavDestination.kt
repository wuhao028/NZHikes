package com.hao.nzhikes.navigation

sealed class AppNavDestination(val route: String) {
    object Home : AppNavDestination("home")
    object Trips : AppNavDestination("trips")
    object Me : AppNavDestination("me")
    object Search : AppNavDestination("search")
}
