package com.example.cat_app.features.quiz.ui

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cat_app.core.ui.BottomNavScreen
import com.example.cat_app.features.quiz.domain.QUESTIONS_PER_GAME


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizResultScreen(
    navController: NavController,
    viewModel: QuizViewModel,
    onDoneClick: () -> Unit,
    onShare: () -> Unit
) {
    val ui by viewModel.state.collectAsState()

    val context = LocalContext.current

    // Create a SnackbarHostState, ali TOAST je bolji, jer ovaj snackbar blokira izvrsavanje, pa cekam 2 sekunde nakon klika na dugme - da bi se izvrsila navigacija
    // val snackbarHostState = remember { SnackbarHostState() }

    // Listen for the ScoreShared side-effect
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when(effect){
                is QuizScreenContract.SideEffect.ScoreShared -> {
                    // prvo pokazem snackbar, eventualno neku animaciju, pa onda navigacija
                    // ali ne mogu samo da idem .showSnackBar, jer je ovo suspend funkcija, i moramo da cekamo da se ona zavrsi (nekih 2s) pa tek onda se zove navigacija
                    // snackbarHostState.showSnackbar("Score shared! 🎉")
                    Toast
                        .makeText(context, "Score shared! 🎉", Toast.LENGTH_SHORT)
                        .show()

                    navController.navigate(BottomNavScreen.AllSpecies.route) {
                        // tear down the entire quiz subgraph
                        popUpTo(BottomNavScreen.Quiz.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
                else -> Unit
            }
        }
    }

    Scaffold(
        // attach the SnackbarHost
//        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // nice card around the score
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .align(Alignment.CenterHorizontally),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Your Score",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${ui.totalScore} / ${QUESTIONS_PER_GAME * 5}",
                        style = MaterialTheme.typography.displaySmall
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onShare,
                enabled = !ui.posting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Share to Leaderboard")
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onDoneClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Done")
            }
        }
    }
}
