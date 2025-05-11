package com.example.cat_app.quiz_package

import androidx.compose.runtime.Composable
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.cat_app.quiz_package.questions.ImageToNameQuestion
import com.example.cat_app.quiz_package.questions.LifeSpanQuestion
import com.example.cat_app.quiz_package.questions.OriginQuestion
import com.example.cat_app.quiz_package.QuizScreenContract.UiState
import com.example.cat_app.quiz_package.QuizScreenContract.UiEvent
import com.example.cat_app.quiz_package.QuizScreenContract.SideEffect

@Composable
fun QuizResultScreen(
    viewModel: QuizViewModel,
    onClose: () -> Unit,
    onShare: () -> Unit
) {
    val ui by viewModel.state.collectAsState()
    val score = viewModel.totalScore()

    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Your score:", style = MaterialTheme.typography.titleLarge)
        Text("$score / ${QUESTIONS_PER_GAME * 5}", style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onShare, enabled = !ui.posting) {
            Text("Share to Leaderboard")
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onClose) { Text("Done") }
    }
}
