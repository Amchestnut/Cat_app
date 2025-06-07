///*  CatAppNavigationSingle.kt  */
//package com.example.cat_app.core.navigation
//
//import androidx.activity.compose.BackHandler
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.List
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material.icons.filled.Pets
//import androidx.compose.material.icons.filled.School
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.navigation.NavDestination.Companion.hierarchy
//import androidx.navigation.NavGraph.Companion.findStartDestination
//import androidx.navigation.compose.*
//import com.example.cat_app.features.allspecies.ui.AllSpeciesScreen
//import com.example.cat_app.features.details_screen.SpeciesDetailsScreen
//
//@Composable
//fun CatAppNavigationSingle() {
//    val nav = rememberNavController()
//
//    /* ------- Bottom-bar tabovi ----------------------------------------- */
//    val tabs = listOf(
//        Tab("cats",        Icons.Default.Pets,   "Cats"),
//        Tab("quiz",        Icons.Default.School, "Quiz"),
//        Tab("leaderboard", Icons.Default.List,   "Scores"),
//        Tab("profile",     Icons.Default.Person, "Profile")
//    )
//    var selected by remember { mutableStateOf(tabs.first()) }
//
//    BackHandler(enabled = selected.route != "cats") { selected = tabs.first() }
//
//    /* pokaži bar samo na root destinacijama svakog taba */
//    val showBar by derivedStateOf {
//        val dest = nav.currentBackStackEntry?.destination
//        dest?.hierarchy?.any { it.route == selected.route } ?: false
//    }
//
//    Scaffold(
//        bottomBar = {
//            if (showBar) {
//                NavigationBar {
//                    tabs.forEach { tab ->
//                        NavigationBarItem(
//                            selected = tab == selected,
//                            onClick  = {
//                                selected = tab
//                                nav.navigate(tab.route) {
//                                    launchSingleTop = true
//                                    restoreState    = true
//                                    popUpTo(nav.graph.findStartDestination().id) {
//                                        saveState = true
//                                    }
//                                }
//                            },
//                            icon  = { Icon(tab.icon, null) },
//                            label = { Text(tab.label) }
//                        )
//                    }
//                }
//            }
//        }
//    ) { padding ->
//        /* -------------- Jedan NavHost, svi grafovi --------------------- */
//        NavHost(nav, startDestination = "cats", modifier = Modifier.padding(padding)) {
//
//            /* Cats graf */
//            navigation(route = "cats", startDestination = "cats/list") {
//                composable("cats/list") { AllSpeciesScreen(nav) }
//                composable("cats/details/{id}") { SpeciesDetailsScreen(nav) }
//                composable("cats/gallery/{breedId}") { BreedGalleryScreen(nav) }
//                composable("cats/photo/{breedId}/{index}") { PhotoViewerScreen(nav) }
//            }
//
//            /* Quiz graf */
//            navigation(route = "quiz", startDestination = "quiz/intro") {
//                composable("quiz/intro")    { QuizIntroScreen(nav) }
//                composable("quiz/questions"){ QuizQuestionScreen(nav) }
//                composable("quiz/result")   { QuizResultScreen(nav) }
//            }
//
//            /* Leaderboard (jedan ekran) */
//            composable("leaderboard") { LeaderboardScreen() }
//
//            /* Profile graf */
//            navigation(route = "profile", startDestination = "profile/info") {
//                composable("profile/info") { ProfileScreen(nav) }
//                composable("profile/edit") { EditProfileScreen(nav) }
//            }
//        }
//    }
//}
//
///* jednostavna data-klasa za tab */
//private data class Tab(val route: String, val icon: ImageVector, val label: String)
