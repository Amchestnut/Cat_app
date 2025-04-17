package com.example.cat_app

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cat_app.all_species_screen.AllSpeciesScreen
import com.example.cat_app.all_species_screen.AllSpeciesViewModel

@Composable
fun MainNavigation() {
    val navController = rememberNavController()     // I needed to import the library compose navigation for this

    NavHost(
        navController = navController,
        startDestination = "all_species"
    ){
        all_species(
            route = "all_species",
            navController = navController,
        )
        species_details(
            route = "details/{$SPECIES_ID}",
//            arguments = listOf(
//                // todo: to add...
//            )
        )
    }
}


private fun NavGraphBuilder.all_species(
    route : String,
    navController : NavController,
) = composable(route = route) {
    val viewModel = hiltViewModel<AllSpeciesViewModel>()

    AllSpeciesScreen(
        viewModel = viewModel,
        onDetailInformationClick = { species_id ->
//            navController.navigate("details/$species_id")   // mozda moze bolje da se napise
            // TODO: za sada nista, necu da mi pukne app
        }
    )
}

private fun NavGraphBuilder.species_details(
    route : String,
) = composable(route) {

}