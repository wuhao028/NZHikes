package com.hao.nzhikes.navigation

import android.util.Log
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hao.explore.CampsiteDetailScreen
import com.hao.explore.HomeScreen
import com.hao.explore.HutDetailScreen
import com.hao.explore.SearchScreen
import com.hao.explore.TrackDetailScreen
import com.hao.explore.model.SearchResult
import com.hao.me.MeScreen
import com.hao.trips.TripsScreen

@OptIn(ExperimentalSharedTransitionApi::class)
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
        SharedTransitionLayout {
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.Home.route,
            ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    modifier = Modifier.padding(innerPadding),
                    onSearchClick = { searchType ->
                        navController.navigate("${BottomNavItem.Search.route}/$searchType")
                    },
                    onHikeClick = { hike -> navController.navigate("track/${hike.assetId}") },
                    onCampsiteClick = { campsite -> navController.navigate("campsite/${campsite.assetId}") },
                    onHutClick = { hut -> navController.navigate("hut/${hut.assetId}") },
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this
                )
            }
            composable(BottomNavItem.Trips.route) {
                TripsScreen(
                    modifier = Modifier.padding(innerPadding),
                    onHikeClick = { hike -> navController.navigate("track/${hike.assetId}") }
                )
            }
            composable(BottomNavItem.Me.route) {
                MeScreen()
            }
            composable(
                route = "${BottomNavItem.Search.route}/{searchType}",
                arguments = listOf(navArgument("searchType") { type = NavType.IntType })
            ) {
                SearchScreen(
                    onItemClick = { result ->
                        when (result) {
                            is SearchResult.TrackResult -> {
                                Log.d(
                                    "AppNavigation",
                                    "Navigating to track: ${result.track.assetId}"
                                )
                                navController.navigate("track/${result.track.assetId}") {
                                    popUpTo(BottomNavItem.Home.route)
                                }
                            }

                            is SearchResult.CampsiteResult -> {
                                navController.navigate("campsite/${result.campsite.assetId}") {
                                    launchSingleTop = true
                                }
                            }

                            is SearchResult.HutResult -> {
                                navController.navigate("hut/${result.hut.assetId}") {
                                    launchSingleTop = true
                                }
                            }
                        }
                    },
                    onCancel = { navController.popBackStack() },
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this
                )
            }
            composable(
                route = "track/{assetId}",
                arguments = listOf(navArgument("assetId") { type = NavType.StringType })
            ) {
                TrackDetailScreen(
                    onBackClick = { navController.navigateUp() }
                )
            }
            
            composable(
                route = "campsite/{assetId}",
                arguments = listOf(navArgument("assetId") { type = NavType.StringType })
            ) { backStackEntry ->
                CampsiteDetailScreen(
                    onBackClick = { navController.navigateUp() },
                    viewModel = hiltViewModel()
                )
            }
            
            composable(
                route = "hut/{assetId}",
                arguments = listOf(navArgument("assetId") { type = NavType.StringType })
            ) { backStackEntry ->
                HutDetailScreen(
                    onBackClick = { navController.navigateUp() },
                    viewModel = hiltViewModel()
                )
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
        BottomNavItem.Me
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { screen ->
            val title = stringResource(screen.titleRes)
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = title) },
                label = { Text(title) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

@Preview(name = "Bottom navigation", showBackground = true, widthDp = 390)
@Composable
private fun BottomNavBarPreview() {
    MaterialTheme {
        BottomNavBar(navController = rememberNavController())
    }
}
