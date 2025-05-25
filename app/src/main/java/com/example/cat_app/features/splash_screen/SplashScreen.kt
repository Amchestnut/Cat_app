package com.example.cat_app.features.splash_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.wear.compose.material.CircularProgressIndicator

// ARHITEKTURA, BITNO:
// sta bi krsilo MVI ovde?
// da composable salje EVENT nazad kad primi side effect, to bi bila greska.
// U MVI, efekti idu samo iz VIEWMODELA prema UI, jedini izuzetak kada saljem event u VIEWMODEL je kada korisnik nesto uradi (klik, unos teksta...)

// takodje, krsilo bi MVI da nam je navigacija deo UI STATE umesto da bude deo SideEffect-a
// takodje, krislo bi mvi kada bismo iz VIEWMODELA direktno zvali navController (mesam UI dependenci u viewmodelu)

//@Composable
//fun SplashScreen(
//    viewModel: SplashViewModel = hiltViewModel(),
//    navController: NavController
//) {
//    // u tvom slučaju state je uvek Loading, pa ga možeš iskoristiti za prikaz spinnera
//    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//        CircularProgressIndicator()
//    }
//
//    // ovo je dobro MVI, mi ovde "hvatamo" side effect i izvrsavamo ga (navigacija, toast, sta god)
//    LaunchedEffect (Unit) {
//        viewModel.effect.collect { side ->
//            when (side) {
//                is SplashScreenContract.SideEffect.NavigateToOnLogin ->
//                    navController.navigate("auth") { popUpTo("splash") { inclusive = true } }
//
//                is SplashScreenContract.SideEffect.NavigateToMain ->
//                    navController.navigate("main") { popUpTo("splash") { inclusive = true } }
//            }
//        }
//    }
//}



// stari splash screen (za staru navigaciju)
@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    navController: NavController
) {
    // u tvom slučaju state je uvek Loading, pa ga možeš iskoristiti za prikaz spinnera
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }

    // ovo je dobro MVI, mi ovde "hvatamo" side effect i izvrsavamo ga (navigacija, toast, sta god)
    LaunchedEffect (Unit) {
        viewModel.effect.collect { side ->
            when (side) {
                is SplashScreenContract.SideEffect.NavigateToOnLogin ->
                    navController.navigate("login") { popUpTo("splash") { inclusive = true } }

                is SplashScreenContract.SideEffect.NavigateToMain ->
                    navController.navigate("all_species") { popUpTo("splash") { inclusive = true } }
            }
        }
    }
}