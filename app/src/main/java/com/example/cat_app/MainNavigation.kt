package com.example.cat_app

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cat_app.all_species_screen.AllSpeciesScreen
import com.example.cat_app.all_species_screen.AllSpeciesViewModel
import com.example.cat_app.details_screen.SpeciesDetailsViewModel
import com.example.cat_app.details_screen.SpeciesDetailsScreen
import com.example.cat_app.login_screen.LoginScreen
import com.example.cat_app.login_screen.LoginViewModel
import com.example.cat_app.splash_screen.SplashScreen
import com.example.cat_app.splash_screen.SplashViewModel

@Composable
fun MainNavigation() {
    val navController = rememberNavController()     // I needed to import the library compose navigation for this

    NavHost(
        navController = navController,
        startDestination = "splash"
    ){
        splash_screen(navController)
        login_screen(navController)
        all_species(
            route = "all_species",
            navController = navController,
        )
        species_details(
            route = "details/{$ARG_SPECIES_ID}",
            arguments = listOf(
                navArgument(ARG_SPECIES_ID) {
                    type = NavType.StringType
                }
            ),
            navController = navController
        )
    }
}

private fun NavGraphBuilder.splash_screen(
    navController: NavController
) = composable("splash"){
    val viewModel = hiltViewModel<SplashViewModel>()

    SplashScreen(viewModel, navController)
}

private fun NavGraphBuilder.login_screen(
    navController: NavController
) = composable(route = "login"){
    val viewModel = hiltViewModel<LoginViewModel>()

    LoginScreen(
        viewModel,
        onLoginSuccess = {
            navController.navigate("all_species"){
                popUpTo("login") {inclusive = true}
            }
        }
    )
}


private fun NavGraphBuilder.all_species(
    route : String,
    navController : NavController,
) = composable(route = route) {
    val viewModel = hiltViewModel<AllSpeciesViewModel>()

    AllSpeciesScreen(
        viewModel = viewModel,
        onDetailInformationClick = { species_id ->
            navController.navigate("details/$species_id")   // mozda moze bolje da se napise
            //  za sada je ovo jedina change screen akcija, necu da mi pukne app
        }
    )
}

private fun NavGraphBuilder.species_details(
    route : String,
    arguments: List<NamedNavArgument>,
    navController: NavController
) = composable(route) {
    val viewModel = hiltViewModel<SpeciesDetailsViewModel>()

    Log.d("test", viewModel.toString())
    SpeciesDetailsScreen(
        viewModel = viewModel,
        // TODO: ako zelimo neki change button za nesto, mozemo ovde da dodamo
        onClose = {
            navController.navigateUp()
        }
    )

}