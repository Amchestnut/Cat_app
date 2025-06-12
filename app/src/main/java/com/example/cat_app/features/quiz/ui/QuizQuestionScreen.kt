package com.example.cat_app.features.quiz.ui

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.cat_app.R
import com.example.cat_app.features.quiz.ui.QuizScreenContract
import com.example.cat_app.features.quiz.ui.QuizScreenContract.UiEvent
import com.example.cat_app.features.quiz.ui.QuizViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizQuestionScreen(
    viewModel: QuizViewModel,
    onExitQuiz: () -> Unit,
) {
    val ui by viewModel.state.collectAsState()
    val context = LocalContext.current

    // --- Stanja za UI interakcije ---
    var selectedChoice by rememberSaveable { mutableStateOf<String?>(null) }
    var isCorrect by rememberSaveable { mutableStateOf<Boolean?>(null) }
//    var buttonsEnabled by remember { mutableStateOf(true) }
    var pendingChoice  by rememberSaveable { mutableStateOf<String?>(null) }  // da bi buttoni imali vremena da se OBOJE
    var isAnswered by rememberSaveable { mutableStateOf(false) }

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is QuizScreenContract.SideEffect.ShowCancelDialog -> showExitDialog = true
                is QuizScreenContract.SideEffect.NavigateToResult -> onExitQuiz()
                else -> Unit
            }
        }
    }

    // Resetuj stanja kad se promeni pitanje
    LaunchedEffect(ui.currentIdx) {
        selectedChoice = null
        isCorrect = null
        isAnswered = false
    }

    // da animacija ima vremena da se okine, da se vidi
    // LaunchedEffect je helped koji iza scene otvara OWN CoroutineScope vezan za tu kompoziciju
    // Sve sto napisem unutra je -> SUSPEND CONTEXT, pa delay() radi bez problema
    LaunchedEffect(pendingChoice) {
        pendingChoice?.let { choice ->
            delay(100L)
            viewModel.setEvent(UiEvent.AnswerChosen(choice))
            pendingChoice = null
        }
    }


    BackHandler {
        viewModel.setEvent(UiEvent.CancelPressed)
    }

    if (ui.error != null) {
        Text("Error: ${ui.error!!.message}")
        return
    }
    val q = ui.questions.getOrNull(ui.currentIdx) ?: return

    // zvucni efekt
    val playSound = {
        MediaPlayer.create(context, R.raw.incorrect_answer).start()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kviz znanja o mačkama") },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.setEvent(UiEvent.CancelPressed) }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Nazad"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Gornji bar sa trenutnim pitanjem i preostalim vremenom
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pitanje: ${ui.currentIdx + 1}/${ui.questions.size}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Preostalo: ${ui.remainingMillis / 1000}s",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // ANIMIRANI SCORE BAR
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Animirana vrednost za progress bar
                val animatedProgress by animateFloatAsState(
                    targetValue = ui.totalScore.toFloat() / 100f, // Normalizujem skor na 0.0 do 1.0, umesto 0-100
                    animationSpec = tween(durationMillis = 1000),
                    label = "ScoreAnimation"
                )
                Text("Poeni: ${ui.totalScore}", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 4.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    strokeCap = StrokeCap.Round
                )
            }

            // pitanje + slika
            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = q.questionText,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                AsyncImage(
                    model = q.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Odgovori kao grid ---
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(q.choices) { choice ->
                    val isSelected        = selectedChoice == choice
                    val isCorrectChoice   = selectedChoice == ui.questions[ui.currentIdx].correctChoice
                    val isWrongSelected   = isSelected && !isCorrectChoice
                    val isAnsweredNow     = selectedChoice != null    // ili koristi tvoje isAnswered stanje

                    // Boja prema matrici slučajeva
                    val buttonColor by animateColorAsState(
                        targetValue = when {
                            !isAnsweredNow               -> MaterialTheme.colorScheme.primary   // još nije odgovoreno
                            isCorrectChoice              -> Color.Green.copy(alpha = 0.8f)   // tačan = zeleno
                            isWrongSelected              -> Color.Red.copy(alpha = 0.8f)        // pogrešan klik = crveno
                            else                         -> Color(0xFFBDBDBD)      // ostali = sivo
                        },
                        animationSpec = tween(400),
                        label = "BtnColor"
                    )

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor         = buttonColor,
                            disabledContainerColor = buttonColor     // da zadrži boju i kad se onemogući
                        ),
                        enabled = selectedChoice == null,    // posle prvog klika, sve dugmiće onemogucavamo za klik
                        onClick = {
                            if (selectedChoice == null) {            // dozvoli klik samo prvi put
                                selectedChoice = choice              // setuj odgovor
                                if (!isCorrectChoice)
                                    playSound()    // zvuk greške

                                // odlozimo sve za 1s, da ima vremena da se prikaze animacija, u LE je korutina (neophodno)
                                // zanimljivo, ne mogu ovde da uradim delay() u korutini, jer onClick {...} je NON-SUSPEND blok, a delay je SUSPEND, pa moze da radi samo unutar coroutine scope ili suspend konteksta
                                pendingChoice = choice
                            }
                        }
                    ) {
                        Text(choice, fontSize = 14.sp)
                    }
                }
            }
        }

        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                title = { Text("Prekinuti kviz?") },
                text = { Text("Sigurno želite da prekinete kviz i izgubite skor?") },
                confirmButton = {
                    TextButton(onClick = {
                        showExitDialog = false
                        onExitQuiz()
                    }) { Text("Da") }
                },
                dismissButton = {
                    TextButton(onClick = { showExitDialog = false }) { Text("Ne") }
                }
            )
        }
    }

    BackHandler { }     // Can't go back to quiz, sry
}

