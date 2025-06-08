package com.example.cat_app.features.details_screen

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.cat_app.core.ui.theme.ErrorRed600
import com.example.cat_app.core.ui.theme.ScoreYellow
import com.example.cat_app.core.ui.theme.SuccessGreen
import com.example.cat_app.core.ui.theme.WarningYellow
import com.example.cat_app.features.allspecies.domain.Breed


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeciesDetailsScreen(
    viewModel: SpeciesDetailsViewModel,
    onClose: () -> Unit,
    onGalleryClick: () -> Unit,
) {
    val UiState = viewModel.state.collectAsState()
    val state = UiState.value

    // nebitno je gde smo ovo stavili, ovo nije imperativni kod da se izvrsava odozgo ka dole.  Bitno je na koji key reaguje
    LaunchedEffect(viewModel) {
        viewModel.effect.collect {
            // handle side effects if needed.
            // But here we dont have any, this is the last screen in the flow
        }
    }

//    val breed = uiState.value.breed!!   // OVO NIJE BEZBEDNO, jer sta ako podaci ne stignu? Sve crashuje. Zato je ovaj drugi nacin ispod bezbedniji
    when {
        state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        state.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: ${state.error.message}")
        }
        state.breed != null -> SpeciesDetailsScreen(
            state = state,
            eventPublisher = viewModel::setEvent,
            breed = state.breed,
            onClose = onClose,
            onGalleryClick = onGalleryClick,
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SpeciesDetailsScreen(
    state : SpeciesDetailsScreenContract.UiState,
    eventPublisher : (SpeciesDetailsScreenContract.UiEvent) -> Unit,
    onClose : () -> Unit,
    breed: Breed,
    onGalleryClick : () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(breed.name.orEmpty(),
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
                    .padding(horizontal = 2.dp)
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .height(250.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    AsyncImage(
                        model = breed.imageUrl,
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
                        text = breed.name,
                        style = MaterialTheme.typography.headlineMedium.copy(color = Color.White, fontSize = 24.sp),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    )
                }

                Spacer(Modifier.height(8.dp))

                LifeSpanBar(
                    lifeSpan = breed.lifeSpan,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer(Modifier.height(16.dp))

                SizeCard(
                    weightMetric   = breed.weightMetric,
                    weightImperial = breed.weightImperial,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer(Modifier.height(24.dp))

                // Details Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        breed.origin?.let {
                            Text(
                                text = "Origin country:",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(text = it, style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(8.dp))
                        }
                        breed.temperament?.let {
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
                        breed.description?.let {
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

                // Five behavior/needs widgets
                BreedStats(breed)

                Spacer(Modifier.height(16.dp))

                // Rarity + Wikipedia link
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (breed.rare) {
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
                            containerColor = ScoreYellow, // nice green
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
                        breed.wikipediaUrl?.let { url ->
                            ctx.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            )
                        }
                    }) {
                        Text("Learn on Wikipedia")
                    }
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = onGalleryClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("View Gallery")
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}


@Composable
private fun BreedStats(breed: Breed) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Behavioral Profile",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(12.dp))

        // ─── First row ─────────────────────────────────────────
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatCard("Adaptability",  breed.adaptability,   Modifier.weight(1f))
            StatCard("Energy",        breed.energyLevel,    Modifier.weight(1f))
            StatCard("Affection",     breed.affectionLevel, Modifier.weight(1f))
        }

        Spacer(Modifier.height(12.dp))

        // ─── Second row ────────────────────────────────────────
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatCard("Intelligence",  breed.intelligence,   Modifier.weight(1f))
            StatCard("Social Needs",  breed.socialNeeds,    Modifier.weight(1f))
            StatCard("Grooming",      breed.grooming,       Modifier.weight(1f))
        }
    }
}

@Composable
fun StatCard(
    label: String,
    value: Int,
    modifier: Modifier = Modifier
) {
    // animate progress from 0→value/5
    val progress by animateFloatAsState(targetValue = (value / 5f).coerceIn(0f,1f))

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress   = progress,
                    modifier   = Modifier.size(56.dp),
                    strokeWidth = 6.dp
                )
                Text(
                    text  = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text      = label,
                style     = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun LifeSpanBar(
    lifeSpan: String?,
    modifier: Modifier = Modifier,
    speciesMax: Int = 20               // longest cat life that we want to display (max 20 yrs)
) {
    if (lifeSpan.isNullOrBlank()) return

    // ─── parse "12 - 15" → 12..15 ───────────────────────────────────────────────
    val (min, max) = lifeSpan
        .split("-")
        .map { it.trim().toIntOrNull() ?: 0 }
        .let { it.first() to it.last() }

    val average = (min + max) / 2f
    val progress = average / speciesMax

    Column(modifier) {
        Text("Typical life‑span", style = MaterialTheme.typography.labelLarge)

        Spacer(Modifier.height(6.dp))

        // nice animated colour‑blend progress bar
        val animated by animateFloatAsState(progress)
        Box(
            Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(Color.LightGray.copy(alpha = .3f))
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animated)
                    .background(
                        Brush.horizontalGradient(
                            listOf(SuccessGreen  , WarningYellow  , ErrorRed600)
                        )
                    )
            )
        }

        Text(
            text = "$min‑$max years",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}


@Composable
fun SizeCard(
    weightMetric: String?,
    weightImperial: String?,
    modifier: Modifier = Modifier
) {
    if (weightMetric == null && weightImperial == null) return

    var metric by remember { mutableStateOf(true) }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(
                    start = 12.dp,  // ← push everything in from the left
                    top = 8.dp,
                    end = 12.dp,
                    bottom = 8.dp
                )
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Weight", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.weight(1f))
                Switch(
                    checked = metric,
                    onCheckedChange = { metric = it }
                )
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = if (metric) "${weightMetric ?: "?"} kg" else "${weightImperial ?: "?"} lb",
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}


