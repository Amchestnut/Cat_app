package com.example.cat_app.features.quiz.ui

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cat_app.R
import com.example.cat_app.core.navigation.BottomNavScreen
import com.example.cat_app.features.quiz.domain.QUESTIONS_PER_GAME
import kotlinx.coroutines.delay
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

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

    var shareClicked by rememberSaveable { mutableStateOf(false) }
    var doneClicked  by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is QuizScreenContract.SideEffect.ScoreShared -> {
                    Toast
                        .makeText(context, "Score shared! 🎉", Toast.LENGTH_SHORT)
                        .show()

                    navController.navigate(BottomNavScreen.AllSpecies.route) {
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

    // 1) LaunchedEffect za Share za share button
    LaunchedEffect(shareClicked) {
        if (shareClicked) {
            delay(500)    // trebalo bi ustvari da sacekam da se skala vrati na 1f (sa 0.66 na 1)
            onShare()         // tek sad podelim score, kao neki mali delay
            shareClicked = false
        }
    }
    // 2) LaunchedEffect za Done button
    LaunchedEffect(doneClicked) {
        if (doneClicked) {
            delay(500)
            onDoneClick()
            doneClicked = false
        }
    }

    // Animacija za dugmice, tweenuju se malo, u sustini smanjuje se kada kliknem i zadrzim i prevucem :D
    val shareInteractionSource = remember { MutableInteractionSource() }
    val isSharePressed by shareInteractionSource.collectIsPressedAsState()
    val shareScale by animateFloatAsState(
        targetValue = if (isSharePressed) 0.66f else 1f,
        animationSpec = tween(durationMillis = 500),
        label = "shareScaleAnimation"
    )

    val doneInteractionSource = remember { MutableInteractionSource() }
    val isDonePressed by doneInteractionSource.collectIsPressedAsState()
    val doneScale by animateFloatAsState(
        targetValue = if (isDonePressed) 0.66f else 1f,
        animationSpec = tween(durationMillis = 500),
        label = "doneScaleAnimation"
    )

    Scaffold { padding ->
        // Box je neophodan da bi se KonfettiView prikazao preko ostatka ovog sadrzaja
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
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
                        Image(
                            painter = painterResource( R.drawable.ic_trophy_gold),
                            contentDescription = null,
                            modifier = Modifier.size(148.dp),
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
                    onClick = { shareClicked = true },
                    enabled = !ui.posting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .scale(shareScale), // Primenjujem ovu animaciju
                    interactionSource = shareInteractionSource // Povezujemo izvor interakcije
                ) {
                    Text("Share to Leaderboard")
                }

                Spacer(Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { doneClicked = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .scale(doneScale), // Primenjujemo animaciju
                    interactionSource = doneInteractionSource // Povezujemo izvor interakcije
                ) {
                    Text("Done")
                }
            }

            // Konfeti Animacija
            // Konfeti se automatski prikazuju na ulasku u ekran (kad zavrsim ovaj kviz)
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = remember { celebratoryConfetti() },
            )
        }
    }
}

// Definicija za izgled i ponašanje konfeta
private fun celebratoryConfetti(): List<Party> {
    val party = Party(
        speed = 0f,
        maxSpeed = 30f,
        damping = 0.9f,
        spread = 360,
        colors = listOf(
            0xFFFCE18A.toInt(),
            0xFFFF726D.toInt(),
            0xFFF4306D.toInt(),
            0xFFB48DEF.toInt()
        ),
        emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
        position = Position.Relative(0.5, 0.3)
    )
    return listOf(
        party,
        party.copy(
            position = Position.Relative(0.2, 0.3)
        ),
        party.copy(
            position = Position.Relative(0.8, 0.3)
        )
    )
}