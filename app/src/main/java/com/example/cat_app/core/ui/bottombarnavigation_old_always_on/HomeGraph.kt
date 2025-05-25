//package com.example.cat_app.core.ui.bottombarnavigation
//
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavController
//import androidx.navigation.NavGraphBuilder
//import androidx.navigation.NavType
//import androidx.navigation.compose.composable
//import androidx.navigation.navArgument
//import androidx.navigation.navigation
//import com.example.cat_app.features.allspecies.ui.AllSpeciesScreen
//import com.example.cat_app.features.allspecies.ui.AllSpeciesViewModel
//import com.example.cat_app.features.breedgallery.ui.BreedGalleryScreen
//import com.example.cat_app.features.breedgallery.ui.BreedGalleryViewModel
//import com.example.cat_app.features.details_screen.SpeciesDetailsScreen
//import com.example.cat_app.features.details_screen.SpeciesDetailsViewModel
//import com.example.cat_app.features.photo_viewer.PhotoViewerScreen
//import com.example.cat_app.features.photo_viewer.PhotoViewerViewModel
//
//fun NavGraphBuilder.homeGraph(nav: NavController) {
//    navigation(
//        route = BottomTab.Home.root,
//        startDestination = "home/list"
//    ) {
//        composable("home/list") {
//            val viewModel = hiltViewModel<AllSpeciesViewModel>()
//
//            AllSpeciesScreen(
//                viewModel = viewModel,
//                onDetailInformationClick = { id ->
//                    nav.navigate("home/details/$id")
//                },
//                onStartQuizClick = {
//                    nav.popBackStack()            // izbrisi sve do sad
//                    nav.navigate("quiz")    // destination == tab root
//                }
//            )
//        }
//
//        // breed details
//        composable(
//            "home/details/{id}",
//            arguments = listOf(navArgument("id") {
//                type = NavType.StringType
//            })
//        ) {
//            val viewModel = hiltViewModel<SpeciesDetailsViewModel>()
//            SpeciesDetailsScreen(
//                viewModel      = viewModel,
//                onGalleryClick = {
//                    val id = viewModel.state.value.breed!!.id
//                    nav.navigate("home/gallery/$id")
//                },
//                onClose        = { nav.popBackStack() }
//            )
//        }
//
//        // gallery
//        composable(
//            "home/gallery/{breedId}",
//            arguments = listOf(navArgument("breedId"){type=NavType.StringType})
//        ) { entry ->
//            val vm = hiltViewModel<BreedGalleryViewModel>(entry)
//            BreedGalleryScreen (
//                viewModel = vm,
//                onPhotoClick = { _, idx ->
//                    nav.navigate("home/photo/$idx")
//                },
//                onClose = { nav.popBackStack() }
//            )
//        }
//
//
//        // full screen photo viewer
//        composable(
//            "home/photo/{idx}",
//            arguments = listOf(navArgument("idx"){type=NavType.IntType})
//        ) { entry ->
//            val vm = hiltViewModel<PhotoViewerViewModel>(entry)
//            PhotoViewerScreen(
//                viewModel = vm,
//                onClose = { nav.popBackStack() }
//            )
//        }
//
//    }
//
//}