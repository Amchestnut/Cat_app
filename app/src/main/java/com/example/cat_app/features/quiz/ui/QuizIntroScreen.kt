package com.example.cat_app.features.quiz.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Scoreboard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cat_app.R // Pretpostavimo da imate resurs slike, npr. R.drawable.cat_quiz_background

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizIntroScreen(
    onStart: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Cat Trivia",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary // Malo boje za top bar
                )
            )
        },
        bottomBar = {
            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .height(56.dp), // Povećana visina dugmeta
                shape = RoundedCornerShape(12.dp), // Zaobljenije ivice dugmeta
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Istaknuta boja dugmeta
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Start Quiz",
                    fontSize = 20.sp, // Veći tekst na dugmetu
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    ) { innerPadding ->
        // Dodajemo Box za gradijent
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // Prekrivanje gradijentom za bolju čitljivost teksta
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.2f), Color.Black.copy(alpha = 0.6f))
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center // Centriraj sadržaj vertikalno
            ) {
                Spacer(Modifier.height(32.dp)) // Dodatni razmak od top bar-a

                // Kartica za pravila kviza
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)) // Polu-transparentna kartica
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.Start, // Poravnanje teksta unutar kartice
                        verticalArrangement = Arrangement.spacedBy(12.dp) // Razmak između stavki
                    ) {
                        Text(
                            text = "Rules of the Game:",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))

                        // Pojedinačne stavke sa ikonama
                        QuizRuleItem(icon = Icons.Default.LockClock, text = "5-minute timer")
                        QuizRuleItem(icon = Icons.Default.Numbers, text = "20 questions")
                        QuizRuleItem(icon = Icons.Default.Info, text = "No going back")
                        QuizRuleItem(icon = Icons.Default.Scoreboard, text = "Share score on leaderboard")
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Opis cilja kviza
                Text(
                    text = "Aim: answer as many correctly as you can before time runs out. Test your feline knowledge!",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color.White.copy(alpha = 0.9f), // Svetliji tekst na tamnoj pozadini
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Pomoćna Composable funkcija za stavke pravila (da se izbegne ponavljanje koda)
@Composable
fun QuizRuleItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Column {
        Text(
            text = "• $text", // Bullet point za stil
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
        )
    }
}