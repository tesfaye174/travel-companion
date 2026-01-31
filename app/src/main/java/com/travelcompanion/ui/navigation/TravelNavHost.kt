package com.travelcompanion.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.travelcompanion.ui.MainViewModel
import com.travelcompanion.ui.screens.HomeScreen
import com.travelcompanion.ui.screens.MapScreen
import com.travelcompanion.ui.screens.StatsScreen

@Composable
fun TravelNavHost(navController: NavHostController, viewModel: MainViewModel) {
    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToMap = { navController.navigate("map") }
            )
        }

        composable(
            route = "map?tripId={tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.LongType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getLong("tripId")
            MapScreen(tripId = tripId)
        }

        composable("stats") {
            StatsScreen(viewModel = viewModel)
        }
    }
}
