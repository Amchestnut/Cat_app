package com.example.cat_app.core.ui

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cat_app.features.allspecies.ui.AllSpeciesScreen
import com.example.cat_app.features.allspecies.ui.AllSpeciesViewModel
import com.example.cat_app.features.breedgallery.ui.BreedGalleryScreen
import com.example.cat_app.features.breedgallery.ui.BreedGalleryViewModel
import com.example.cat_app.features.details_screen.SpeciesDetailsScreen
import com.example.cat_app.features.details_screen.SpeciesDetailsViewModel
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
import androidx.navigation.NavType
import androidx.navigation.navigation
import com.example.cat_app.features.leaderboard.ui.LeaderboardScreen
import com.example.cat_app.features.leaderboard.ui.LeaderboardViewModel
import com.example.cat_app.features.profile.ui.EditProfileScreen
import com.example.cat_app.features.profile.ui.ProfileScreen


// Bottom bar destinations
sealed class BottomNavScreen(val route: String, val iconVector: androidx.compose.ui.graphics.vector.ImageVector, val label: String) {
    object AllSpecies : BottomNavScreen("all_species", Icons.Filled.Pets, "Cats")
    object Quiz : BottomNavScreen("quiz", Icons.Filled.QuestionAnswer, "Quiz")
    object Leaderboard : BottomNavScreen("leaderboard", Icons.Filled.Leaderboard, "Leaderboard")
    object Profile : BottomNavScreen("profile", Icons.Filled.Person, "Profile")
}

private val TopLevelTabRoutes = setOf(
    BottomNavScreen.AllSpecies.route,
    BottomNavScreen.Quiz.route,
    BottomNavScreen.Leaderboard.route,
    BottomNavScreen.Profile.route
)

@Composable
fun BottomNavigation() {
    val navController = rememberNavController()
    val entries = navController.currentBackStackEntryAsState()
    val currentRoute = entries.value?.destination?.route

    // Ovo mi sluzi da mogu da kliknem back button za bottom bar navigaciju, i da stvarno ide unazad
    BackHandler(enabled = navController.previousBackStackEntry != null) {
        navController.popBackStack()
    }

    // Only show bottom bar on these routes (or any nested under "quiz")
//    val showBottomBar = when {
//        currentRoute == BottomNavScreen.AllSpecies.route                 -> true
//        currentRoute?.startsWith(BottomNavScreen.Quiz.route) == true     -> true
//        currentRoute == BottomNavScreen.Leaderboard.route                -> true
//        currentRoute == BottomNavScreen.Profile.route                    -> true
//        else                                                             -> false
//    }

    val currentDest      = entries.value?.destination
//    val parent           = currentDest?.parent

    val showBottomBar = when {
        /* case 1 – we are on one of the 4 tab routes */
        currentDest?.route in TopLevelTabRoutes                                    -> true

        /* case 2 – we’re inside the Quiz graph *and* on its start dest (quiz/intro) */
        currentDest?.parent?.route == BottomNavScreen.Quiz.route &&
                currentDest.id == (currentDest.parent as NavGraph).startDestinationId     -> true

        else -> false
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar{
                    listOf(
                        BottomNavScreen.AllSpecies,
                        BottomNavScreen.Quiz,
                        BottomNavScreen.Leaderboard,
                        BottomNavScreen.Profile
                    ).forEach { screen ->
                        val selected = currentRoute == screen.route ||
                                (screen is BottomNavScreen.Quiz && currentRoute?.startsWith(screen.route) == true)

                        NavigationBarItem(
                            icon = { Icon(screen.iconVector, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    // Pop up to the start destination of the graph to
                                    // avoid building up a large stack of destinations

//                                    popUpTo(navController.graph.findStartDestination().id) {      ne moze ovo, skida previse stvari
                                    popUpTo("main") {
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
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // --- SPLASH & LOGIN FLOW ---
            composable("splash") {
                val vm = hiltViewModel<SplashViewModel>()
                SplashScreen(vm, navController)
            }
            composable("login") {
                val vm = hiltViewModel<LoginViewModel>()
                LoginScreen(vm) {
                    navController.navigate(BottomNavScreen.AllSpecies.route) {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }

            // --- MAIN (BOTTOM NAV) GRAPH ---
            navigation(
                startDestination = BottomNavScreen.AllSpecies.route,
                route = "main"
            ) {
                composable(BottomNavScreen.AllSpecies.route) {
                    // 1) observe the navController’s current backStackEntry as state
                    val currentEntry by navController.currentBackStackEntryAsState()

                    // 2) only re-derive the parent “main” entry when the currentEntry changes
                    val parentEntry = remember(currentEntry) {
                        navController.getBackStackEntry("main")
                    }

                    val vm = hiltViewModel<AllSpeciesViewModel>(parentEntry)
                    AllSpeciesScreen(
                        viewModel = vm,
                        onDetailInformationClick = { id ->
                            navController.navigate("details/$id")
                        },
                        onStartQuizClick = {
                            navController.navigate(BottomNavScreen.Quiz.route)
                        }
                    )
                }

                // Quiz nested graph
                navigation(
                    route = BottomNavScreen.Quiz.route,
                    startDestination = "quiz/intro"
                ) {
                    composable("quiz/intro") { entry ->
                        val parent = remember(entry) { navController.getBackStackEntry(BottomNavScreen.Quiz.route) }
                        val vm = hiltViewModel<QuizViewModel>(parent)

                        QuizIntroScreen(onStart = {
                            vm.setEvent(QuizScreenContract.UiEvent.LoadQuiz)
                            navController.navigate("quiz/questions")
                        })
                    }

                    composable("quiz/questions") { entry ->
                        val parent = remember(entry) { navController.getBackStackEntry(BottomNavScreen.Quiz.route) }
                        val vm = hiltViewModel<QuizViewModel>(parent)

                        // NEW: collect the "NavigateToResult" effect and actually push the result screen
                        LaunchedEffect(vm) {
                            vm.effect.collect { effect ->
                                if (effect is QuizScreenContract.SideEffect.NavigateToResult) {
                                    navController.navigate("quiz/result")
                                }
                            }
                        }

                        QuizQuestionScreen(
                            viewModel = vm,
                            onExitQuiz = {
                                // collapse the *entire* quiz graph & show Cats
                                navController.navigate(BottomNavScreen.AllSpecies.route) {
                                    popUpTo(BottomNavScreen.Quiz.route) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    composable("quiz/result") { entry ->
                        val parent = remember(entry) { navController.getBackStackEntry(BottomNavScreen.Quiz.route) }
                        val vm = hiltViewModel<QuizViewModel>(parent)

                        QuizResultScreen(
                            viewModel = vm,

                            onClose = {
                                Log.d("NAV", "🎯 Done with quiz — popping it and going to Cats")
                                navController.navigate(BottomNavScreen.AllSpecies.route) {
                                    // tear down the entire quiz subgraph
                                    popUpTo(BottomNavScreen.Quiz.route) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            },
                            onShare = { vm.setEvent(QuizScreenContract.UiEvent.SharePressed) }
                        )
                    }
                }

                composable(BottomNavScreen.Leaderboard.route) {
                    val viewModel = hiltViewModel<LeaderboardViewModel>()
                    LeaderboardScreen(
                        viewModel = viewModel
                    )
                }

                composable(BottomNavScreen.Profile.route) {
                    ProfileScreen(onEditClick = {
                        navController.navigate("profile/edit")
                    })
                }

                composable("profile/edit") {
                    EditProfileScreen(onClose = {
                        navController.popBackStack()
                    })
                }
            }


            // --- DETAIL SCREENS (no bottom bar) ---
            composable(
                route = "details/{speciesId}",
                arguments = listOf(navArgument("speciesId") { type = NavType.StringType })
            ) { backStackEntry ->
                val vm = hiltViewModel<SpeciesDetailsViewModel>()
                SpeciesDetailsScreen(
                    viewModel = vm,
                    onClose = { navController.popBackStack() },
                    onGalleryClick = {
                        val id = vm.state.value.breed!!.id
                        navController.navigate("gallery/$id")
                    }
                )
            }

            composable(
                route = "gallery/{breedId}",
                arguments = listOf(navArgument("breedId") { type = NavType.StringType })
            ) { backStackEntry ->
                val breedId = backStackEntry.arguments!!.getString("breedId")!!
                val vm = hiltViewModel<BreedGalleryViewModel>()
                BreedGalleryScreen(
                    viewModel = vm,
                    onPhotoClick = { _, index -> navController.navigate("photoViewer/$breedId/$index") },
                    onClose = { navController.popBackStack() }
                )
            }

            composable(
                route = "photoViewer/{breedId}/{startIndex}",
                arguments = listOf(
                    navArgument("breedId") { type = NavType.StringType },
                    navArgument("startIndex") { type = NavType.IntType }
                )
            ) {
                val vm = hiltViewModel<PhotoViewerViewModel>()
                PhotoViewerScreen(
                    viewModel = vm,
                    onClose = { navController.popBackStack() }
                )
            }
        }
    }
}

// --- PLACEHOLDER SCREENS ---

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
