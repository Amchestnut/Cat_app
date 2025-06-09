package com.example.cat_app.features.leaderboard.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.unit.Dp

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cat_app.R
import com.example.cat_app.core.ui.theme.CatBeige
import com.example.cat_app.core.ui.theme.CatBrown
import com.example.cat_app.core.ui.theme.CatWhite
import com.example.cat_app.core.ui.theme.ScoreYellow
import com.example.cat_app.core.ui.theme.SuccessGreen
import com.example.cat_app.features.leaderboard.ui.LeaderboardContract.LeaderboardItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    viewModel: LeaderboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LeaderboardContract.SideEffect.ShowError ->
                    snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        // uklanja sve automatske insets
        contentWindowInsets = WindowInsets(0),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                // gura sadržaj ispod status bara
                .statusBarsPadding()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            // header
            Text(
                text = "🐾 Cat Quiz Leaderboard",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // podium za top3
            Podium(state.items.take(3))

            Spacer(modifier = Modifier.height(4.dp))

            // lista ostalih rezultata do samog bottom nava
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                // ako ti treba bottom padding da se ne poklapa sa navigacijom, uncomment sledeću liniju:
                // contentPadding = PaddingValues(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
            ) {
                items(state.items.drop(3)) { item ->
                    LeaderboardRow(item)
                }
            }
        }
    }
}

@Composable
private fun Podium(topItems: List<LeaderboardItem>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(270.dp)
    ) {
        // 1. mesto, centar
        if (topItems.isNotEmpty()) {
            PodiumItem(
                item = topItems[0],
                rankPosition = 1,
                boxHeight = 140.dp,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        // 2. mesto, sa leve strane
        if (topItems.size > 1) {
            PodiumItem(
                item = topItems[1],
                rankPosition = 2,
                boxHeight = 140.dp,
                modifier = Modifier
                    .align(Alignment.BottomStart)      // dno, levo
                    .padding(start = 22.dp)            // udaljenije od ivice
                    .offset(y = (-30).dp)              // pomeri gore
            )
        }

        // 3. mesto, sa desne strane
        if (topItems.size > 2) {
            PodiumItem(
                item = topItems[2],
                rankPosition = 3,
                boxHeight = 140.dp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)       // dno, desno
                    .padding(end = 22.dp)             // udaljenije od ivice
                    .offset(y = (-30).dp)              // pomeri gore
            )
        }
    }
}

@Composable
private fun PodiumItem(
    item: LeaderboardItem,
    rankPosition: Int,
    boxHeight: Dp,
    modifier: Modifier = Modifier
) {
    val trophyRes = when (rankPosition) {
        1 -> R.drawable.ic_trophy_gold
        2 -> R.drawable.ic_trophy_silver
        3 -> R.drawable.ic_trophy_bronze
        else -> R.drawable.ic_trophy_gold
    }

    Column(
        modifier = modifier
            .width(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // trofej
        Image(
            painter = painterResource(id = trophyRes),
            contentDescription = "Trophy $rankPosition",
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // box sa detaljima
        Surface(
            modifier = Modifier
                .height(boxHeight)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = CatWhite,
            tonalElevation = 4.dp
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                // avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(CatBeige),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Pets, contentDescription = null, tint = CatBrown)
                }

                // nickname
                Text(item.nickname, fontWeight = FontWeight.Bold)

                // score red
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = "Score",
                        tint = ScoreYellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = String.format("%.2f", item.result),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // plays
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Pets,
                        contentDescription = "Plays",
                        tint = SuccessGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${item.plays} plays",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}


@Composable
private fun LeaderboardRow(item: LeaderboardItem) {
    Card(
        // bež kartica!!
        colors = CardDefaults.cardColors(CatBeige),
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${item.rank}.", fontWeight = FontWeight.Bold, modifier = Modifier.width(36.dp))
            Spacer(Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.nickname, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Star, contentDescription = null, tint = ScoreYellow )
                    Spacer(Modifier.width(4.dp))
                    Text(String.format("%.2f", item.result), style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.width(16.dp))
                    Icon(Icons.Filled.Pets, contentDescription = null, tint = SuccessGreen)
                    Spacer(Modifier.width(4.dp))
                    Text("${item.plays} plays", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}