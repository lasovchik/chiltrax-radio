package com.chilltrax.radio.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.chilltrax.radio.presentation.editor.StationEditorScreen
import com.chilltrax.radio.presentation.editor.StationEditorViewModel
import com.chilltrax.radio.presentation.home.PlayerScreen
import com.chilltrax.radio.presentation.home.RadioViewModel

object Destinations {
    const val HOME = "home"
    const val ARG_STATION_ID = "stationId"
    const val EDITOR_ROUTE = "editor?stationId={stationId}"

    fun editor(stationId: Long? = null): String =
        if (stationId == null) "editor" else "editor?stationId=$stationId"
}

@Composable
fun ChilltraxNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Destinations.HOME,
    ) {
        composable(Destinations.HOME) {
            val viewModel: RadioViewModel = hiltViewModel()
            PlayerScreen(
                viewModel = viewModel,
                onAddStation = {
                    navController.navigate(Destinations.editor())
                },
                onEditStation = { station ->
                    navController.navigate(Destinations.editor(station.id))
                },
            )
        }

        composable(
            route = Destinations.EDITOR_ROUTE,
            arguments = listOf(
                navArgument(Destinations.ARG_STATION_ID) {
                    type = NavType.LongType
                    defaultValue = -1L
                },
            ),
        ) {
            val viewModel: StationEditorViewModel = hiltViewModel()
            StationEditorScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
