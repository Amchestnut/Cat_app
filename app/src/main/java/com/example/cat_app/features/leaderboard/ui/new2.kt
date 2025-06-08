//package com.example.cat_app.features.leaderboard.ui
//
//package com.example.cat_app.features.leaderboard.ui
//
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.EmojiEvents
//import androidx.compose.material.icons.filled.Pets
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.material3.Icon
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Surface
//import androidx.compose.material3.SnackbarHost
//import androidx.compose.material3.SnackbarHostState
//import androidx.compose.ui.unit.Dp
//
////nimport androidx.compose.material3.SnackbarHostState
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import com.example.cat_app.features.leaderboard.ui.LeaderboardContract.LeaderboardItem
//
//// Cat-themed colors
//private val CatBeige = Color(0xFFF5F5DC)
//private val CatBrown = Color(0xFFA67B5B)
//private val CatWhite = Color(0xFFFFFFFF)
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun LeaderboardScreen(
//    viewModel: LeaderboardViewModel = hiltViewModel()
//) {
//    val state by viewModel.state.collectAsState()
//    val snackbarHostState = remember { SnackbarHostState() }
//
//    LaunchedEffect(viewModel) {
//        viewModel.effect.collect { effect ->
//            when (effect) {
//                is LeaderboardContract.SideEffect.ShowError ->
//                    snackbarHostState.showSnackbar(effect.message)
//            }
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("🐾 Cat Quiz Leaderboard", fontWeight = FontWeight.Bold) },
//                modifier = Modifier.background(CatBeige)
//            )
//        },
//        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
//    ) { padding ->
//        Surface(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(CatBeige)
//                .padding(padding)
//        ) {
//            if (state.loading) {
//                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                    androidx.compose.material3.CircularProgressIndicator()
//                }
//            } else if (state.errorMessage != null) {
//                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                    Text(state.errorMessage ?: "Error", color = MaterialTheme.colorScheme.error)
//                }
//            } else {
//                Column {
//                    // Top 3 podium
//                    val top3 = state.items.take(3)
//                    Podium(top3)
//
//                    Spacer(Modifier.height(16.dp))
//
//                    // Rest of leaderboard from 4 onwards
//                    LazyColumn(
//                        modifier = Modifier.fillMaxSize(),
//                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
//                        verticalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        items(state.items.drop(3)) { item ->
//                            LeaderboardListRow(item)
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun Podium(topItems: List<LeaderboardItem>) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 16.dp),
//        horizontalArrangement = Arrangement.SpaceEvenly,
//        verticalAlignment = Alignment.Bottom
//    ) {
//        // Second place
//        if (topItems.size > 1) PodiumItem(item = topItems[1], rankPosition = 2, height = 180.dp)
//        // First place
//        if (topItems.isNotEmpty()) PodiumItem(item = topItems[0], rankPosition = 1, height = 220.dp)
//        // Third place
//        if (topItems.size > 2) PodiumItem(item = topItems[2], rankPosition = 3, height = 160.dp)
//    }
//}
//
//@Composable
//private fun PodiumItem(item: LeaderboardItem, rankPosition: Int, height: Dp) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Bottom
//    ) {
//        // Crown for first place
//        if (rankPosition == 1) {
//            Icon(
//                imageVector = Icons.Filled.EmojiEvents,
//                contentDescription = null,
//                tint = CatBrown,
//                modifier = Modifier.size(32.dp)
//            )
//        } else {
//            Spacer(Modifier.height(32.dp))
//        }
//
//        Surface(
//            modifier = Modifier
//                .size(width = 100.dp, height = height)
//                .clip(RoundedCornerShape(12.dp)),
//            color = CatWhite,
//            tonalElevation = 4.dp
//        ) {
//            Column(
//                modifier = Modifier.fillMaxSize().padding(8.dp),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.SpaceBetween
//            ) {
//                // Avatar placeholder
//                Box(
//                    modifier = Modifier
//                        .size(48.dp)
//                        .clip(CircleShape)
//                        .background(CatBeige)
//                ) {
//                    Icon(
//                        imageVector = Icons.Filled.Pets,
//                        contentDescription = null,
//                        tint = CatBrown,
//                        modifier = Modifier.align(Alignment.Center)
//                    )
//                }
//
//                Text(text = item.nickname, fontWeight = FontWeight.Bold)
//                Text(text = "Plays: ${item.plays}")
//                Text(text = String.format("%.1f", item.result))
//                Text(text = "#$rankPosition", color = CatBrown, fontWeight = FontWeight.Bold)
//            }
//        }
//    }
//}
//
//@Composable
//private fun LeaderboardListRow(item: LeaderboardItem) {
//    Card(
//        shape = RoundedCornerShape(8.dp),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(IntrinsicSize.Min)
//    ) {
//        Row(
//            modifier = Modifier
//                .background(CatWhite)
//                .padding(12.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                text = "${item.rank}.",
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.width(32.dp)
//            )
//            Spacer(Modifier.width(8.dp))
//            Text(
//                text = item.nickname,
//                fontWeight = FontWeight.Medium,
//                modifier = Modifier.weight(1f)
//            )
//            Spacer(Modifier.width(8.dp))
//            Text(text = String.format("%.1f", item.result))
//            Spacer(Modifier.width(16.dp))
//            Text(text = "${item.plays}")
//        }
//    }
//}
