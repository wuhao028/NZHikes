package com.hao.nzhikes.navigation

import androidx.compose.foundation.layout.Box
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hao.explore.HomeScreen
import com.hao.explore.HomeViewModel
import com.hao.explore.SearchScreen
import com.hao.me.MeScreen
import com.hao.trips.TripsScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
                NavHost(navController, startDestination = AppNavDestination.Home.route) {
            composable(AppNavDestination.Home.route) {
                val viewModel: HomeViewModel = hiltViewModel()
                HomeScreen(
                    onToggleFavorite = viewModel::toggleFavorite,
                    onSearchClick = { navController.navigate(AppNavDestination.Search.route) }
                )
            }
            composable(AppNavDestination.Trips.route) {
                TripsScreen()
            }
            composable(AppNavDestination.Me.route) {
                MeScreen()
            }
            composable(AppNavDestination.Search.route) {
                SearchScreen(onTrackClick = { /* TODO */ })
            }
        }
    }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Trips,
        BottomNavItem.Me,
        BottomNavItem.Search
    )
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

            items.filter { it.route != AppNavDestination.Search.route }.forEach { screen ->
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
