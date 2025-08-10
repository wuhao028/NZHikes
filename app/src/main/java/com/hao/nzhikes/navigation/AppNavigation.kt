package com.hao.nzhikes.navigation

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hao.explore.HomeScreen
import com.hao.explore.SearchScreen
import com.hao.explore.TrackDetailScreen
import com.hao.me.MeScreen
import com.hao.trips.TripsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarDestinations = listOf(
        BottomNavItem.Home.route,
        BottomNavItem.Trips.route,
        BottomNavItem.Me.route
    )

    val shouldShowBottomBar = currentDestination?.route in bottomBarDestinations
    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                BottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    onSearchClick = { navController.navigate(BottomNavItem.Search.route) },
                    onHikeClick = { hike -> navController.navigate("track/${hike.assetId}") }
                )
            }
            composable(BottomNavItem.Trips.route) {
                TripsScreen(
                    onHikeClick = { hike -> navController.navigate("track/${hike.assetId}") }
                )
            }
            composable(BottomNavItem.Me.route) {
                MeScreen()
            }
            composable(BottomNavItem.Search.route) {
                SearchScreen(
                    onTrackClick = { assetId -> 
                        Log.d("AppNavigation", "Navigating to track: $assetId")
                        navController.navigate("track/$assetId") {
                            popUpTo(BottomNavItem.Home.route)
                        }
                    },
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
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Trips,
        BottomNavItem.Me
    )
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
