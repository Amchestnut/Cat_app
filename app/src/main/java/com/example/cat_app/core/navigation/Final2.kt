//package com.example.cat_app.core.navigation
//
//import androidx.compose.foundation.layout.Arrangement
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
///*  MainNavigation.kt
// *  put under  app/src/main/java/com/example/cat_app/core/ui
// */
//
//
///* ------------------------------------------------------------------ */
///* 1) Tabs – **enum** guarantees exactly one instance per value       */
///* ------------------------------------------------------------------ */
//private enum class BottomTab(
//    val route : String,
//    val icon  : androidx.compose.ui.graphics.vector.ImageVector,
//    val label : String
//) {
//    Cats       ("all_species", Icons.Default.Pets,          "Cats"),
//    Quiz       ("quiz",        Icons.Default.QuestionAnswer,"Quiz"),
//    Leaderboard("leaderboard", Icons.Default.Leaderboard,   "Leaderboard"),
//    Profile    ("profile",     Icons.Default.Person,        "Profile");
//}
//
///* ------------------------------------------------------------------ */
///* 2) The only Composable you need to call from your Activity         */
///* ------------------------------------------------------------------ */
//@Composable
//fun FinalNavigation() {
//
//    val navController        = rememberNavController()
//    val backStackEntry by     navController.currentBackStackEntryAsState()
//
//    /* ------------------------------------------------------------ */
//    /*  Decide when the bottom bar is visible                       */
//    /* ------------------------------------------------------------ */
//    val showBottomBar = remember(backStackEntry) {
//        val dest   = backStackEntry?.destination
//        val parent = dest?.parent                               // could be null
//
//        when {
//            /* Direct children of "main" graph → visible */
//            parent?.route == "main" -> true
//
//            /* Start destination of a nested graph under "main" → visible
//               (that’s our "quiz/intro" screen). */
//            parent?.parent?.route == "main" &&
//                    dest?.id == (parent as? NavGraph)?.startDestinationId -> true
//
//            else -> false
//        }
//    }
//
//    /* ------------------------------------------------------------ */
//    /*  Scaffold & NavHost                                          */
//    /* ------------------------------------------------------------ */
//    Scaffold(
//        bottomBar = {
//            if (showBottomBar) {
//                NavigationBar {
//                    BottomTab.values().forEach { tab ->
//
//                        val selected = backStackEntry
//                            ?.destination
//                            ?.hierarchy
//                            ?.any { it.route == tab.route } == true
//
//                        NavigationBarItem(
//                            selected    = selected,
//                            icon        = { Icon(tab.icon, contentDescription = tab.label) },
//                            label       = { Text(tab.label) },
//                            onClick     = {
//                                navController.navigate(tab.route) {
//                                    popUpTo(navController.graph.findStartDestination().id) {
//                                        saveState = true
//                                    }
//                                    launchSingleTop = true
//                                    restoreState    = true
//                                }
//                            }
//                        )
//                    }
//                }
//            }
//        }
//    ) { padding ->
//
//        NavHost(
//            navController    = navController,
//            startDestination = "splash",
//            modifier         = Modifier
//                .fillMaxSize()
//                .padding(padding)
//        ) {
//
//            /* ------------ Splash & Login (no bottom bar) ------------ */
//            composable("splash") {
//                val vm = hiltViewModel<SplashViewModel>()
//                SplashScreen(vm, navController)
//            }
//            composable("login") {
//                val vm = hiltViewModel<LoginViewModel>()
//                LoginScreen(vm) {
//                    navController.navigate(BottomTab.Cats.route) {
//                        popUpTo("login") { inclusive = true }
//                    }
//                }
//            }
//
//            /* ----------------- MAIN TABBED GRAPH ------------------ */
//            navigation(
//                startDestination = BottomTab.Cats.route,
//                route            = "main"
//            ) {
//
//                /* CATS  ------------------------------------------------ */
//                composable(BottomTab.Cats.route) {
//                    val parentEntry = remember(backStackEntry) {
//                        navController.getBackStackEntry("main")
//                    }
//                    val vm = hiltViewModel<AllSpeciesViewModel>(parentEntry)
//                    AllSpeciesScreen(
//                        viewModel = vm,
//                        onDetailInformationClick = { id ->
//                            navController.navigate("details/$id")
//                        },
//                        onStartQuizClick = {
//                            navController.navigate(BottomTab.Quiz.route)
//                        }
//                    )
//                }
//
//                /* QUIZ  ------------------------------------------------ */
//                navigation(
//                    startDestination = "quiz/intro",
//                    route            = BottomTab.Quiz.route
//                ) {
//
//                    composable("quiz/intro") { entry ->
//                        val vm = hiltViewModel<QuizViewModel>(
//                            remember(entry) {
//                                navController.getBackStackEntry(BottomTab.Quiz.route)
//                            }
//                        )
//                        QuizIntroScreen(
//                            onStart = {
//                                vm.setEvent(QuizScreenContract.UiEvent.LoadQuiz)
//                                navController.navigate("quiz/questions")
//                            }
//                        )
//                    }
//
//                    composable("quiz/questions") { entry ->
//                        val vm = hiltViewModel<QuizViewModel>(
//                            remember(entry) {
//                                navController.getBackStackEntry(BottomTab.Quiz.route)
//                            }
//                        )
//                        QuizQuestionScreen(
//                            viewModel = vm,
//                            onExitQuiz = {
//                                navController.popBackStack(
//                                    BottomTab.Cats.route,
//                                    inclusive = false
//                                )
//                            }
//                        )
//                    }
//
//                    composable("quiz/result") { entry ->
//                        val vm = hiltViewModel<QuizViewModel>(
//                            remember(entry) {
//                                navController.getBackStackEntry(BottomTab.Quiz.route)
//                            }
//                        )
//                        QuizResultScreen(
//                            viewModel = vm,
//                            onClose = {
//                                navController.popBackStack(
//                                    BottomTab.Cats.route,
//                                    inclusive = false
//                                )
//                            },
//                            onShare = { vm.setEvent(QuizScreenContract.UiEvent.SharePressed) }
//                        )
//                    }
//                }
//
//                /* LEADERBOARD ------------------------------------------ */
//                composable(BottomTab.Leaderboard.route) {
//                    LeaderboardScreen()
//                }
//
//                /* PROFILE ---------------------------------------------- */
//                composable(BottomTab.Profile.route) {
//                    ProfileScreen { navController.navigate("profile/edit") }
//                }
//                composable("profile/edit") {
//                    EditProfileScreen { navController.popBackStack() }
//                }
//            }
//
//            /* ----------- DETAIL / GALLERY / PHOTO (no bar) ----------- */
//            composable(
//                "details/{speciesId}",
//                arguments = listOf(navArgument("speciesId") { type = NavType.StringType })
//            ) {
//                val vm = hiltViewModel<SpeciesDetailsViewModel>()
//                SpeciesDetailsScreen(
//                    viewModel = vm,
//                    onClose   = { navController.popBackStack() },
//                    onGalleryClick = {
//                        val id = vm.state.value.breed!!.id
//                        navController.navigate("gallery/$id")
//                    }
//                )
//            }
//
//            composable(
//                "gallery/{breedId}",
//                arguments = listOf(navArgument("breedId") { type = NavType.StringType })
//            ) { bse ->
//                val breedId = bse.arguments!!.getString("breedId")!!
//                val vm = hiltViewModel<BreedGalleryViewModel>()
//                BreedGalleryScreen(
//                    viewModel = vm,
//                    onPhotoClick = { _, idx ->
//                        navController.navigate("photoViewer/$breedId/$idx")
//                    },
//                    onClose = { navController.popBackStack() }
//                )
//            }
//
//            composable(
//                "photoViewer/{breedId}/{startIndex}",
//                arguments = listOf(
//                    navArgument("breedId")    { type = NavType.StringType },
//                    navArgument("startIndex") { type = NavType.IntType    },
//                )
//            ) {
//                val vm = hiltViewModel<PhotoViewerViewModel>()
//                PhotoViewerScreen(vm) { navController.popBackStack() }
//            }
//        }
//    }
//}
//
///* ------------------------------------------------------------------ */
///* 3) Tiny placeholder screens so the file compiles                    */
///* ------------------------------------------------------------------ */
//@Composable fun LeaderboardScreen() {
//    Placeholder("Leaderboard")
//}
//
//@Composable fun ProfileScreen(onEditClick: () -> Unit) {
//    Placeholder("Profile") { Button(onClick = onEditClick) { Text("Edit") } }
//}
//@Composable fun EditProfileScreen(onClose: () -> Unit) {
//    Placeholder("Edit profile") { Button(onClick = onClose) { Text("Back") } }
//}
//@Composable private fun Placeholder(
//    label: String,
//    extra: @Composable (() -> Unit)? = null
//) {
//    Column(
//        Modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text(label)
//        extra?.invoke()
//    }
//}
