package com.example.cat_app.features.quiz.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizIntroScreen(
    onStart: () -> Unit
) {
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Cat Trivia") }) },
        bottomBar = {
            Button (
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) { Text("Start Quiz") }
        }
    ) { inner ->
        Column(
            Modifier.padding(inner).padding(24.dp)
        ) {
            Text("🕒 5-minute timer\n🎯 20 questions\n🚫 No going back\n📊 Share score on leaderboard")
            Spacer(Modifier.height(12.dp))
            Text("Aim: answer as many correctly as you can before time runs out.")
        }
    }
}