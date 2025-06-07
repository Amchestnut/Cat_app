//package com.example.cat_app.core.ui
//
//
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Leaderboard
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material.icons.filled.Pets
//import androidx.compose.material.icons.filled.QuestionAnswer
//import androidx.compose.material3.Button
//import androidx.compose.material3.Icon
//import androidx.compose.material3.NavigationBar
//import androidx.compose.material3.NavigationBarItem
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavGraph
//import androidx.navigation.NavDestination.Companion.hierarchy
//import androidx.navigation.NavGraph.Companion.findStartDestination
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.currentBackStackEntryAsState
//import androidx.navigation.compose.rememberNavController
//import androidx.navigation.navigation
//import androidx.navigation.navArgument
//import com.example.cat_app.features.allspecies.ui.AllSpeciesScreen
//import com.example.cat_app.features.allspecies.ui.AllSpeciesViewModel
//import com.example.cat_app.features.breedgallery.ui.BreedGalleryScreen
//import com.example.cat_app.features.breedgallery.ui.BreedGalleryViewModel
//import com.example.cat_app.features.details_screen.SpeciesDetailsScreen
//import com.example.cat_app.features.details_screen.SpeciesDetailsViewModel
//import com.example.cat_app.features.login_screen.LoginScreen
//import com.example.cat_app.features.login_screen.LoginViewModel
//import com.example.cat_app.features.photo_viewer.PhotoViewerScreen
//import com.example.cat_app.features.photo_viewer.PhotoViewerViewModel
//import com.example.cat_app.features.quiz.ui.*
//import com.example.cat_app.features.splash_screen.SplashScreen
//import com.example.cat_app.features.splash_screen.SplashViewModel
//import androidx.navigation.NavType
//
//// ---------------------
//// 1) Define your tabs
//// ---------------------
//private sealed class BottomNavigationButtons(
//    val route: String,
//    val icon: androidx.compose.ui.graphics.vector.ImageVector,
//    val label: String
//) {
//    object AllSpecies  : BottomNavigationButtons("all_species",  Icons.Filled.Pets,          "Cats")
//    object Quiz        : BottomNavigationButtons("quiz",         Icons.Filled.QuestionAnswer, "Quiz")
//    object Leaderboard : BottomNavigationButtons("leaderboard",  Icons.Filled.Leaderboard,    "Leaderboard")
//    object Profile     : BottomNavigationButtons("profile",      Icons.Filled.Person,         "Profile")
//
//    companion object {
//        val items = listOf(AllSpecies, Quiz, Leaderboard, Profile)
//    }
//}
//
//// -------------------------------------------------
//// 2) The one-and-only BottomNavigation composable
//// -------------------------------------------------
//@Composable
//fun FinalNavigation() {
//    val navController = rememberNavController()
//    val backStackEntry by navController.currentBackStackEntryAsState()
//
//    // Show bar on:
//    //  • direct children of "main" (Cats, Leaderboard, Profile)
//    //  • the startDest of the Quiz nested graph ("quiz/intro")
//    val showBottomBar = remember(backStackEntry) {
//        val dest   = backStackEntry?.destination
//        val parent = dest?.parent
//        when {
//            parent?.route == "main" -> true
//            parent?.parent?.route == "main" &&
//                    dest?.id == (parent as? NavGraph)?.startDestinationId -> true
//            else -> false
//        }
//    }
//
//    Scaffold(
//        bottomBar = {
//            if (showBottomBar) {
//                NavigationBar {
//                    BottomNavigationButtons.items.forEach { screen ->
//                        val selected = backStackEntry
//                            ?.destination
//                            ?.hierarchy
//                            ?.any { it.route == screen.route } == true
//
//                        NavigationBarItem(
//                            selected   = selected,
//                            icon       = { Icon(screen.icon, contentDescription = screen.label) },
//                            label      = { Text(screen.label) },
//                            onClick    = {
//                                navController.navigate(screen.route) {
//                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
//                                    launchSingleTop = true
//                                    restoreState    = true
//                                }
//                            }
//                        )
//                    }
//                }
//            }
//        }
//    ) { paddingValues ->
//        NavHost(
//            navController    = navController,
//            startDestination = "splash",
//            modifier         = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//        ) {
//
//            // ---- SPLASH & LOGIN ----
//            composable("splash") {
//                val vm = hiltViewModel<SplashViewModel>()
//                SplashScreen(vm, navController)
//            }
//            composable("login") {
//                val vm = hiltViewModel<LoginViewModel>()
//                LoginScreen(vm) {
//                    navController.navigate(BottomNavigationButtons.AllSpecies.route) {
//                        popUpTo("login") { inclusive = true }
//                    }
//                }
//            }
//
//            // -------- MAIN (TABBED) --------
//            navigation(
//                startDestination = BottomNavigationButtons.AllSpecies.route,
//                route            = "main"
//            ) {
//                // Cats tab
//                composable(BottomNavigationButtons.AllSpecies.route) {
//                    val parentEntry = remember(backStackEntry) {
//                        navController.getBackStackEntry("main")
//                    }
//                    val vm = hiltViewModel<AllSpeciesViewModel>(parentEntry)
//                    AllSpeciesScreen(
//                        viewModel               = vm,
//                        onDetailInformationClick = { id ->
//                            navController.navigate("details/$id")
//                        },
//                        onStartQuizClick       = {
//                            navController.navigate(BottomNavigationButtons.Quiz.route)
//                        }
//                    )
//                }
//
//                // Quiz nested graph
//                navigation(
//                    startDestination = "quiz/intro",
//                    route            = BottomNavigationButtons.Quiz.route
//                ) {
//                    composable("quiz/intro") { entry ->
//                        val vm = hiltViewModel<QuizViewModel>(
//                            remember(entry) {
//                                navController.getBackStackEntry(BottomNavigationButtons.Quiz.route)
//                            }
//                        )
//                        QuizIntroScreen(onStart = {
//                            vm.setEvent(QuizScreenContract.UiEvent.LoadQuiz)
//                            navController.navigate("quiz/questions")
//                        })
//                    }
//                    composable("quiz/questions") { entry ->
//                        val vm = hiltViewModel<QuizViewModel>(
//                            remember(entry) {
//                                navController.getBackStackEntry(BottomNavigationButtons.Quiz.route)
//                            }
//                        )
//                        QuizQuestionScreen(onExitQuiz = {
//                            navController.popBackStack(
//                                BottomNavigationButtons.AllSpecies.route,
//                                inclusive = false
//                            )
//                        }, viewModel = vm)
//                    }
//                    composable("quiz/result") { entry ->
//                        val vm = hiltViewModel<QuizViewModel>(
//                            remember(entry) {
//                                navController.getBackStackEntry(BottomNavigationButtons.Quiz.route)
//                            }
//                        )
//                        QuizResultScreen(
//                            viewModel = vm,
//                            onClose   = {
//                                navController.popBackStack(
//                                    BottomNavigationButtons.AllSpecies.route,
//                                    inclusive = false
//                                )
//                            },
//                            onShare   = { vm.setEvent(QuizScreenContract.UiEvent.SharePressed) }
//                        )
//                    }
//                }
//
//                // Leaderboard tab
//                composable(BottomNavigationButtons.Leaderboard.route) {
//                    LeaderboardScreen()
//                }
//
//                // Profile tab
//                composable(BottomNavigationButtons.Profile.route) {
//                    ProfileScreen(onEditClick = {
//                        navController.navigate("profile/edit")
//                    })
//                }
//                composable("profile/edit") {
//                    EditProfileScreen { navController.popBackStack() }
//                }
//            }
//
//            // ---- DETAILS & GALLERY (no bottom bar) ----
//            composable(
//                "details/{speciesId}",
//                arguments = listOf(navArgument("speciesId") { type = NavType.StringType })
//            ) {
//                val vm = hiltViewModel<SpeciesDetailsViewModel>()
//                SpeciesDetailsScreen(
//                    viewModel       = vm,
//                    onClose         = { navController.popBackStack() },
//                    onGalleryClick  = {
//                        val id = vm.state.value.breed!!.id
//                        navController.navigate("gallery/$id")
//                    }
//                )
//            }
//            composable(
//                "gallery/{breedId}",
//                arguments = listOf(navArgument("breedId") { type = NavType.StringType })
//            ) { backEntry ->
//                val breedId = backEntry.arguments!!.getString("breedId")!!
//                val vm      = hiltViewModel<BreedGalleryViewModel>()
//                BreedGalleryScreen(
//                    viewModel = vm,
//                    onPhotoClick = { _, idx ->
//                        navController.navigate("photoViewer/$breedId/$idx")
//                    },
//                    onClose = { navController.popBackStack() }
//                )
//            }
//            composable(
//                "photoViewer/{breedId}/{startIndex}",
//                arguments = listOf(
//                    navArgument("breedId")    { type = NavType.StringType },
//                    navArgument("startIndex") { type = NavType.IntType }
//                )
//            ) {
//                val vm = hiltViewModel<PhotoViewerViewModel>()
//                PhotoViewerScreen(
//                    viewModel = vm,
//                    onClose   = { navController.popBackStack() }
//                )
//            }
//        }
//    }
//}
//
//
//
//
//// --- PLACEHOLDER SCREENS ---
//
//@Composable
//fun LeaderboardScreen() {
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
//    ) {
//        Text("Leaderboard")
//    }
//}
//
//@Composable
//fun ProfileScreen(onEditClick: () -> Unit) {
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
//    ) {
//        Text("Profile")
//        Button(onClick = onEditClick) {
//            Text("Edit Profile")
//        }
//    }
//}
//
//@Composable
//fun EditProfileScreen(onClose: () -> Unit) {
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
//    ) {
//        Text("Edit Profile")
//        Button(onClick = onClose) {
//            Text("Back")
//        }
//    }
//}
