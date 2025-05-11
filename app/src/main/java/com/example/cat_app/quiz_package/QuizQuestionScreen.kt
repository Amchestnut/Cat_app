package com.example.cat_app.quiz_package

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
import androidx.compose.runtime.Composable
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



//@Composable
//fun QuizQuestionScreen (
//    viewModel: QuizViewModel,
//    onCancel: (String) -> Unit,
//    onFinished: (String) -> Unit,
//){
//
//}


// cat_app/quiz_package/QuizQuestionScreen.kt
@Composable
fun QuizQuestionScreen(
    viewModel: QuizViewModel
) {
    val ui by viewModel.state.collectAsState()

    BackHandler  {
        viewModel.setEvent(UiEvent.CancelPressed)
    }

    if (ui.error != null) {
        Text("Error: ${ui.error!!.message}")
        return
    }

    val q = ui.questions.getOrNull(ui.currentIdx) ?: return

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        /* tajmer */
        Text(
            text = "Time left: ${ui.remainingMillis / 1000}s",
            style = MaterialTheme.typography.titleMedium
        )

        when (q) {
            is ImageToNameQuestion -> ImageQuestionView(q) {
                viewModel.setEvent(UiEvent.AnswerChosen(it))
            }
            is OriginQuestion -> SimpleChoiceView(q.breedName, q.choices) {
                viewModel.setEvent(UiEvent.AnswerChosen(it))
            }
            is LifeSpanQuestion -> SimpleChoiceView(
                "Prosečan životni vek rase ${q.breedName}",
                q.choices
            ) { viewModel.setEvent(UiEvent.AnswerChosen(it)) }
        }

        Text(text = "Question ${ui.currentIdx + 1}/${ui.questions.size}")
    }
}

/* — helper UI blokovi — */
@Composable
private fun ImageQuestionView(
    q: ImageToNameQuestion,
    onChoice: (String) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            model = q.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(16.dp))

        q.choices.forEach { choice ->
            Button(
                onClick = { onChoice(choice) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(choice)
            }
        }
    }
}

@Composable
private fun SimpleChoiceView(
    question: String,
    choices: List<String>,
    onChoice: (String) -> Unit
) {
    Column {
        Text(question, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        choices.forEach { c ->
            OutlinedButton(
                onClick = { onChoice(c) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(c)
            }
        }
    }
}
