//package com.example.cat_app.core.ui.bottombarnavigation
//
//import androidx.compose.runtime.remember
//import androidx.navigation.NavController
//import androidx.navigation.NavGraphBuilder
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.compose.composable
//import androidx.navigation.navigation
//import com.example.cat_app.features.quiz.ui.QuizIntroScreen
//import com.example.cat_app.features.quiz.ui.QuizQuestionScreen
//import com.example.cat_app.features.quiz.ui.QuizResultScreen
//import com.example.cat_app.features.quiz.ui.QuizScreenContract
//import com.example.cat_app.features.quiz.ui.QuizViewModel
//
//fun NavGraphBuilder.quizGraph(nav: NavController) {
//    navigation(
//        route = BottomTab.Quiz.root,
//        startDestination = "quiz/intro"
//    ) {
//
//        composable("quiz/intro") { entry ->
//            val parent = remember (entry) { nav.getBackStackEntry(BottomTab.Quiz.root) }
//            val vm = hiltViewModel<QuizViewModel>(parent)
//
//            QuizIntroScreen(
//                onStart = {
//                    vm.setEvent(QuizScreenContract.UiEvent.LoadQuiz)
//                    nav.navigate("quiz/questions")
//                }
//            )
//        }
//
//        composable("quiz/questions") { entry ->
//            val parent = remember(entry) { nav.getBackStackEntry(BottomTab.Quiz.root) }
//            val vm = hiltViewModel<QuizViewModel>(parent)
//
//            QuizQuestionScreen(
//                viewModel = vm,
//                onExitQuiz = { nav.popBackStack(BottomTab.Home.root, false) }
//            )
//        }
//
//        composable("quiz/result") { entry ->
//            val parent = remember(entry) { nav.getBackStackEntry(BottomTab.Quiz.root) }
//            val vm = hiltViewModel<QuizViewModel>(parent)
//
//            QuizResultScreen(
//                viewModel = vm,
//                onClose = { nav.popBackStack(BottomTab.Home.root, false) },
//                onShare = { vm.setEvent(QuizScreenContract.UiEvent.SharePressed) }
//            )
//        }
//    }
//}
