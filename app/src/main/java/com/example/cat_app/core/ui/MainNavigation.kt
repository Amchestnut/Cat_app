package com.example.cat_app.core.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.cat_app.features.allspecies.ui.AllSpeciesScreen
import com.example.cat_app.features.allspecies.ui.AllSpeciesViewModel
import com.example.cat_app.features.breedgallery.ui.BreedGalleryScreen
import com.example.cat_app.features.breedgallery.ui.BreedGalleryViewModel
import com.example.cat_app.features.details_screen.SpeciesDetailsViewModel
import com.example.cat_app.features.details_screen.SpeciesDetailsScreen
import com.example.cat_app.features.login_screen.LoginScreen
import com.example.cat_app.features.login_screen.LoginViewModel
import com.example.cat_app.features.photo_viewer.PhotoViewerScreen
import com.example.cat_app.features.photo_viewer.PhotoViewerViewModel
import com.example.cat_app.features.quiz.ui.QuizIntroScreen
import com.example.cat_app.features.quiz.ui.QuizQuestionScreen
import com.example.cat_app.features.quiz.ui.QuizResultScreen
import com.example.cat_app.features.quiz.ui.QuizScreenContract

import com.example.cat_app.features.quiz.ui.QuizViewModel
import com.example.cat_app.features.splash_screen.SplashScreen
import com.example.cat_app.features.splash_screen.SplashViewModel

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

        breed_gallery(
            route = "gallery/{breedId}",
            arguments = listOf(navArgument("breedId"){type=NavType.StringType}),
            navController = navController
        )

        photo_viewer_screen(
            navController,
        )

        /// hmmm da li ovako raditi ili dodati ipak novi NAV-GRAF?
//        quiz_screen(
//            navController,
//        )

        quiz_graph(navController)
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
        },
        onStartQuizClick = {
            navController.navigate("quiz")
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
        },
        onGalleryClick = {
            val id = viewModel.state.value.breed!!.id
            navController.navigate("gallery/$id")
        }
    )

}


private fun NavGraphBuilder.breed_gallery(
    route: String,
    arguments: List<NamedNavArgument>,
    navController: NavController
) = composable(
    route = "gallery/{breedId}",
    arguments = listOf(navArgument("breedId") { type = NavType.StringType })
) {   backStackEntry ->  // treba mi ovo zbog breedId zbog photoViewer-a

    val breedId = backStackEntry.arguments?.getString("breedId")!!
    val viewModel = hiltViewModel<BreedGalleryViewModel>()

    BreedGalleryScreen(
        viewModel = viewModel,
//        onPhotoClick = { urls, index ->
//            // vrv cu ovde da navigiram u full-screan pager
//            navController.navigate("photoViewer/$index")    // mozda druga ruta
//        },

        // ni ovako nisam mogao
//        onPhotoClick = { urls, index ->
//            // 1) Navigiramo na PhotoViewer sa indeksom
//            val route = "photoViewer/$index"
//            navController.navigate(route) {
//                launchSingleTop = true
//            }
//            // 2) Ubacimo listu URLs u SavedStateHandle te nove destinacije
//            navController.currentBackStackEntry
//                ?.savedStateHandle
//                ?.set("images", urls)
//        },

        onPhotoClick = { _, index ->
            // sada prosledjujem breedId i index
            navController.navigate("photoViewer/$breedId/$index")
        },

        onClose = {
            navController.navigateUp()
        }
    )
}

private fun NavGraphBuilder.photo_viewer_screen(
    navController: NavController,
) = composable (
    route = "photoViewer/{breedId}/{startIndex}",
//    arguments = listOf(navArgument("startIndex") {
//        type = NavType.IntType
//    })
    arguments = listOf(
        navArgument("breedId") { type = NavType.StringType },
        navArgument("startIndex") { type = NavType.IntType }
    )
) {
    // now a backStackEntry exists with "startIndex" and with the
    // SavedStateHandle populated below
    val viewModel = hiltViewModel<PhotoViewerViewModel>()

    PhotoViewerScreen(
        viewModel = viewModel,
        onClose = { navController.popBackStack() }
    )
}


// radi , ali izgleda pravim 3 instance VIEW MODELA, pa se restartuje..
//private fun NavGraphBuilder.quiz_graph(nav: NavController) {
//    navigation(
//        route = "quiz",
//        startDestination = "quiz/intro"
//    ) {
//
//        composable("quiz/intro") { entry ->
//            val viewModel = hiltViewModel<QuizViewModel>(entry)
//            QuizIntroScreen(
//                onStart = {
//                    viewModel.setEvent(QuizScreenContract.UiEvent.LoadQuiz)
//                    nav.navigate("quiz/questions")
//                }
//            )
//        }
//
//        composable("quiz/questions") { entry ->
//            val viewModel = hiltViewModel<QuizViewModel>(entry)
//            QuizQuestionScreen(viewModel)
//
//            // Auto-navigate kada viewmodel posalje SideEffect.NavigateToResult
//            // Iz QUESTIONS screen-a prelazimo u result.
//            LaunchedEffect(Unit) {
//                viewModel.effect.collect { effect ->
//                    when (effect) {
//                        is QuizScreenContract.SideEffect.NavigateToResult ->
//                            nav.navigate("quiz/result") {
//                                popUpTo("quiz/questions") { inclusive = true }
//                            }
//                        is QuizScreenContract.SideEffect.ShowCancelDialog ->
//                            // ovde cu da prikazem dijalog, kasnije
//                            Unit
//                        else -> Unit
//                    }
//                }
//            }
//        }
//
//        composable("quiz/result") { entry ->
//            val viewModel = hiltViewModel<QuizViewModel>(entry)
//            QuizResultScreen(
//                viewModel = viewModel,
//                onClose = { nav.popBackStack("all_species", false) },
//                onShare = { viewModel.setEvent(QuizScreenContract.UiEvent.SharePressed) }
//            )
//        }
//    }
//}

private fun NavGraphBuilder.quiz_graph(nav: NavController) {
    navigation(route = "quiz", startDestination = "quiz/intro") {

        composable("quiz/intro") { backStackEntry ->
            // ovo je backStackEntry za DESTINACIJU, ali mi želimo VM na nivou "quiz" grafa:
            val parentEntry = remember(backStackEntry) { nav.getBackStackEntry("quiz") }
            val vm = hiltViewModel<QuizViewModel>(parentEntry)

            QuizIntroScreen(
                onStart = {
                    vm.setEvent(QuizScreenContract.UiEvent.LoadQuiz)  // ako hoćeš da ponovo učitaš
                    nav.navigate("quiz/questions")
                }
            )

            LaunchedEffect(Unit) {
                vm.effect.collect { eff ->
                    when (eff) {
                        is QuizScreenContract.SideEffect.NavigateToResult ->
                            nav.navigate("quiz/result") {
                                popUpTo("quiz/questions") { inclusive = true }
                            }
                        else -> {}
                    }
                }
            }
        }

        composable("quiz/questions") { backStackEntry ->
            // opet isti parentEntry za ceo "quiz" graf
            val parentEntry = remember(backStackEntry) { nav.getBackStackEntry("quiz") }
            val viewModel = hiltViewModel<QuizViewModel>(parentEntry)

            LaunchedEffect(viewModel.effect) {
                viewModel.effect.collect { eff ->
                    when (eff) {
                        is QuizScreenContract.SideEffect.NavigateToResult ->
                            nav.navigate("quiz/result") {
                                popUpTo("quiz/questions") { inclusive = true }
                            }
                        else -> {}
                    }
                }
            }

            QuizQuestionScreen(
                viewModel = viewModel,
                onExitQuiz = { nav.popBackStack("all_species", false) },
            )
        }

        composable("quiz/result") { backStackEntry ->
            // i ovde ista instanca
            val parentEntry = remember(backStackEntry) { nav.getBackStackEntry("quiz") }
            val viewModel = hiltViewModel<QuizViewModel>(parentEntry)

            QuizResultScreen(
                viewModel = viewModel,
                onClose = { nav.popBackStack("all_species", false) },
                onShare = { viewModel.setEvent(QuizScreenContract.UiEvent.SharePressed) }
            )
        }
    }
}
