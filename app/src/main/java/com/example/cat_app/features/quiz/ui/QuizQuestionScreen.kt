package com.example.cat_app.features.quiz.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.cat_app.features.quiz.ui.QuizScreenContract.UiEvent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizQuestionScreen(
    viewModel: QuizViewModel,
    onExitQuiz: () -> Unit,
) {
    val ui by viewModel.state.collectAsState()

    // exit kviz logika:
    var showExitDialog by remember { mutableStateOf(false) }

    // Collect side-effects for showing dialog or navigation
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { eff ->
            when (eff) {
                is QuizScreenContract.SideEffect.ShowCancelDialog ->
                    showExitDialog = true
                is QuizScreenContract.SideEffect.NavigateToResult ->
                    onExitQuiz()
                else -> { /* no-op */ }
            }
        }
    }

    BackHandler { viewModel.setEvent(UiEvent.CancelPressed) }

    if (ui.error != null) {
        Text("Error: ${ui.error!!.message}")
        return
    }
    val q = ui.questions.getOrNull(ui.currentIdx) ?: return

    /*
    Column(Modifier.fillMaxSize()) {
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Preostalo: ${ui.remainingMillis / 1000}s",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        // gornji deo: tekst + slika
        Column(
            modifier = Modifier
                .weight(2f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            // 1) Holder za tekst pitanja
            Card (
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text(
                    text = q.questionText,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            AsyncImage(
                model           = q.imageUrl,
                contentDescription = null,
                modifier        = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale    = ContentScale.Crop
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(q.choices) { choice ->
                Button(
                    onClick = { viewModel.setEvent(UiEvent.AnswerChosen(choice)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(choice)
                }
            }
        }
    }
    */

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kviz znanja o mačkama") },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.setEvent(QuizScreenContract.UiEvent.CancelPressed) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Nazad"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (ui.error != null) {
            Text(
                "Error: ${ui.error!!.message}",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            )
            return@Scaffold
        }
        val q = ui.questions.getOrNull(ui.currentIdx) ?: return@Scaffold

        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Timer
            Text(
                text = "Preostalo: ${ui.remainingMillis / 1000}s",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            // Pitanje + slika
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
                    model           = q.imageUrl,
                    contentDescription = null,
                    modifier        = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale    = ContentScale.Crop
                )
            }

            // Odgovori kao grid
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
                    Button(
                        onClick = { viewModel.setEvent(QuizScreenContract.UiEvent.AnswerChosen(choice)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(choice)
                    }
                }
            }
        }

        // Exit confirmation dialog
        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                title = { Text("Prekinuti kviz?") },
                text = { Text("Sigurno želite da prekinete kviz i izgubite skor?") },
                confirmButton = {
                    TextButton(onClick = {
                        showExitDialog = false
                        onExitQuiz()
                    }) {
                        Text("Da")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExitDialog = false }) {
                        Text("Ne")
                    }
                }
            )
        }
    }


}
