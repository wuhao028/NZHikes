package com.hao.nzhikes.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hao.explore.HomeScreen
import com.hao.explore.SearchScreen
import com.hao.explore.TrackDetailScreen
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
                onSearchClick = { navController.navigate(BottomNavItem.Search.route) }
            )
        }
        composable(BottomNavItem.Trips.route) {
            TripsScreen()
        }
        composable(BottomNavItem.Me.route) {
            MeScreen()
        }
        composable(BottomNavItem.Search.route) {
            SearchScreen(
                onTrackClick = { assetId -> navController.navigate("track/$assetId") },
                onCancel = { navController.popBackStack() }
            )
        }
        composable(
            route = "track/{assetId}",
            arguments = listOf(navArgument("assetId") { type = NavType.StringType })
        ) {
            TrackDetailScreen()
        }
    }
}
