package ru.shum.yandexapitest.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import androidx.navigation.compose.composable
import org.koin.androidx.compose.koinViewModel
import ru.shum.yandexapitest.MainScreen
import ru.shum.yandexapitest.presentation.schedule_details.ScheduleDetailsScreen

@Serializable
object Home

@Serializable
object ScheduleDetails

@Composable
fun AppHavHost(
    navController: NavHostController = rememberNavController()
) {

    val navigationViewModel = koinViewModel<NavigationViewModel>()

    NavHost(navController = navController, startDestination = Home) {
        composable<Home> {
            MainScreen(
                navigationViewModel = navigationViewModel,
                navHostController = navController
            )
        }

        composable<ScheduleDetails> {
            ScheduleDetailsScreen(
                navigationViewModel = navigationViewModel,
                navHostController = navController
            )
        }
    }
}