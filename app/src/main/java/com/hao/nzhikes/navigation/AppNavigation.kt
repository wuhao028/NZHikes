package com.hao.nzhikes.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hao.explore.HomeScreen
import com.hao.me.MeScreen
import com.hao.trips.TripsScreen

@Composable
fun AppNavigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route
    ) {
        composable(BottomNavItem.Home.route) {
            HomeScreen(
                onToggleFavorite = { },
                onSearchClick = { }
            )
        }
        composable(BottomNavItem.Trips.route) {
            TripsScreen()
        }
        composable(BottomNavItem.Me.route) {
            MeScreen()
        }
    }
}
