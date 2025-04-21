package com.example.cat_app.details_screen

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.cat_app.domain.Breed


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeciesDetailsScreen(
    viewModel: SpeciesDetailsViewModel,
    onClose: () -> Unit,
) {
    val uiState = viewModel.state.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect {
            // handle side effects if needed
        }
    }

    val state = uiState.value
    val breed = state.breed

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(breed?.name.orEmpty(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (breed == null && state.loading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        else if (breed == null && state.error != null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Error: ${state.error.message}")
            }
        }
        else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .height(250.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    AsyncImage(
                        model = breed!!.imageUrl,
                        contentDescription = breed.name,
                        modifier = Modifier
                            .matchParentSize()
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                                    startY = 100f
                                )
                            )
                    )
                    Text(
                        text = breed?.name.orEmpty(),
                        style = MaterialTheme.typography.headlineMedium.copy(color = Color.White, fontSize = 24.sp),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    )
                }

                Spacer(Modifier.height(8.dp))

                //  ─────── Basics ───────
//                Column(Modifier.padding(horizontal = 16.dp)) {
//                    Text(breed!!.name, style = MaterialTheme.typography.headlineMedium)
//                    breed.lifeSpan?.let {
//                        Text("Life span: $it years", style = MaterialTheme.typography.bodyMedium)
//                    }
//                    breed.weightMetric?.let {
//                        Text("Avg weight: $it kg", style = MaterialTheme.typography.bodyMedium)
//                    }
//                }


                Spacer(Modifier.height(16.dp))

                // Details Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        breed?.origin?.let {
                            Text(
                                text = "Origin country:",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(text = it, style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(8.dp))
                        }
                        breed?.temperament?.let {
                            Text(
                                text = "Temperament:",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                        breed?.description?.let {
                            Text(
                                text = "About:",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                //  ─────── Five behavior/needs widgets ───────
                BreedStats(breed!!)

                Spacer(Modifier.height(16.dp))

                //  ─────── Rarity badge + Wikipedia link ───────
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (breed!!.rare) {
                        Badge(
                            modifier = Modifier.padding(end = 8.dp),
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor   = MaterialTheme.colorScheme.onErrorContainer
                        ) {
                            Text(
                                text = "Rare Breed",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                    else {
                        Badge(
                            modifier = Modifier.padding(end = 8.dp),
                            containerColor = Color(0xFF81C784), // nice green
                            contentColor   = Color.White
                        ) {
                            Text(
                                text = "Common Breed",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    val ctx = LocalContext.current
                    TextButton(onClick = {
                        breed!!.wikipediaUrl?.let { url ->
                            ctx.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            )
                        }
                    }) {
                        Text("Learn on Wikipedia")
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}


@Composable
fun BreedStats(breed: Breed) {
    Column(Modifier.padding(16.dp)) {
        Text(
            text = "Behavioral Profile",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(12.dp))

        // Row 1: 3 stats
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard("Adaptability",    breed.adaptability,    Modifier.weight(1f))
            StatCard("Energy",         breed.energyLevel,     Modifier.weight(1f))
            StatCard("Affection",      breed.affectionLevel,  Modifier.weight(1f))
        }

        Spacer(Modifier.height(12.dp))

        // Row 2: 3 stats
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard("Intelligence",   breed.intelligence,    Modifier.weight(1f))
            StatCard("Social Needs",   breed.socialNeeds,     Modifier.weight(1f))
            StatCard("Grooming",      breed.grooming,  Modifier.weight(1f))
        }
    }
}

@Composable
fun StatCard(label: String, value: Int, modifier: Modifier = Modifier) {
    // animate from 0 to value 5 on first composition
    val progress by animateFloatAsState(targetValue = value / 5f)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            Modifier
                .padding(12.dp)
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = progress,
                    modifier = Modifier.size(56.dp),
                    strokeWidth = 6.dp
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}
