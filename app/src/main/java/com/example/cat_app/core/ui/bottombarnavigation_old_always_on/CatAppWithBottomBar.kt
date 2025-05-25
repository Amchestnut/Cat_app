//package com.example.cat_app.core.ui.bottombarnavigation
//
//import androidx.activity.compose.BackHandler
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Icon
//import androidx.compose.material3.NavigationBar
//import androidx.compose.material3.NavigationBarItem
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.rememberNavController
//
//@Composable
//fun CatAppWithBottomBar() {
//
//    val homeController    = rememberNavController()
//    val quizController    = rememberNavController()
//    val profileController = rememberNavController()
//
//    val tabNavigation = mapOf(
//        BottomTab.Home to homeController,
//        BottomTab.Quiz to quizController,
//        BottomTab.Profile to profileController
//    )
//
//    var currentTab by rememberSaveable {
//        mutableStateOf(BottomTab.Home)
//    }
//
//    BackHandler (enabled = currentTab != BottomTab.Home) {
//        currentTab = BottomTab.Home
//    }
//
//    Scaffold (
//        bottomBar = {
//            NavigationBar {
//                BottomTab.entries.forEach { tab ->
//                    NavigationBarItem(
//                        selected = currentTab == tab,
//                        icon = {
//                            Icon(tab.icon, tab.label)
//                        },
//                        label = {
//                            Text(tab.label)
//                        },
//                        onClick = {
//                            currentTab = tab
//                        }
//                    )
//                }
//            }
//        }
//    ) { innerPadding ->
//
//        val controller = tabNavigation.getValue(currentTab)
//        NavHost(
//            navController = controller,
//            startDestination = currentTab.root,
//            modifier = Modifier
////                .padding(innerPadding)
//                .fillMaxSize()
//                .padding(innerPadding)
//        ) {
//
//            // inject each tab’s graph
//            homeGraph(controller)
//            quizGraph(controller)
//            profileGraph(controller)
//        }
//
////        @OptIn(ExperimentalAnimationApi::class)
////        NavHost(
////            navController       = controller,
////            startDestination    = currentTab.root,
////            enterTransition     = { EnterTransition.None },
////            exitTransition      = { ExitTransition.None },
////            popEnterTransition  = { EnterTransition.None },
////            popExitTransition   = { ExitTransition.None },
////            modifier            = Modifier.padding(innerPadding)
////        ) {
////            homeGraph(controller)
////            quizGraph(controller)
////            profileGraph(controller)
////        }
//    }
//
//}