//package com.example.cat_app.core.ui.bottombarnavigation
//
//import androidx.compose.runtime.Composable
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.rememberNavController
//import com.example.cat_app.features.splash_screen.SplashViewModel
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.navigation
//import com.example.cat_app.features.login_screen.LoginScreen
//import com.example.cat_app.features.login_screen.LoginViewModel
//import com.example.cat_app.features.splash_screen.SplashScreen
//
//
//@Composable
//fun AppNavigation() {
//    val rootNavigation = rememberNavController()
//
//    NavHost(
//        navController = rootNavigation,
//        startDestination = "splash",
//    ) {
//        // 1. Splash
//        composable("splash") {
//            val viewModel = hiltViewModel<SplashViewModel>()
//
//            SplashScreen(
//                viewModel     = viewModel,
//                navController = rootNavigation
//            )
//        }
//
//        // 2. Authentication graph
//        navigation(route = "auth", startDestination = "login"){
//            composable("login"){
//                val viewModel = hiltViewModel<LoginViewModel>()
//
//                LoginScreen(
//                    viewModel = viewModel,
//                    onLoginSuccess = {
//                        rootNavigation.navigate("main") {
//                            popUpTo("auth") { inclusive = true }
//                        }
//                    }
//                )
//            }
//        }
//
//        composable ("main") {
//            CatAppWithBottomBar()
//        }
//
//    }
//}
//
