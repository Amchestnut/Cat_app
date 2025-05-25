//package com.example.cat_app.core.ui.bottombarnavigation
//
//import androidx.navigation.NavController
//import androidx.navigation.NavGraphBuilder
//import androidx.navigation.compose.composable
//import androidx.navigation.navigation
//
//fun NavGraphBuilder.profileGraph(nav: NavController) {
//    navigation(
//        route = BottomTab.Profile.root,
//        startDestination = "profile/main"
//    ) {
//
//        composable("profile/main") {
//            // TODO
////            ProfileScreen(
////                onLogout = {
////                    // easiest: ask the ROOT nav to go back to login
////                    nav.context.findActivity()?.let { activity ->
////                        activity.finish()           // kill MainActivity
////                        // or use a shared ViewModel event to AppNavigation
////                    }
////                }
////            )
//        }
//    }
//}
