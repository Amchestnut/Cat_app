package com.example.cat_app.features.allspecies.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.cat_app.R
import com.example.cat_app.features.allspecies.domain.Breed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllSpeciesScreen(
    viewModel: AllSpeciesViewModel = hiltViewModel(),       // Hilt ubacuje ViewModel
    onDetailInformationClick: (String) -> Unit,             // sad id tipično String
    onStartQuizClick: (String) -> Unit,
) {
    val uiState by viewModel.state.collectAsState()

    AllSpeciesScreenContent(
        loading     = uiState.loading,
        error       = uiState.error,
        searchQuery = uiState.searchQuery,
        onSearchChange = {
            viewModel.setEvent(AllSpeciesScreenContract.UiEvent.SearchQueryChanged(it))
        },
        breeds      = uiState.filteredBreeds,
        onDetailInformationClick = onDetailInformationClick,
        onStartQuizClick = onStartQuizClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AllSpeciesScreenContent(
    loading: Boolean,
    error: Throwable?,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    breeds: List<Breed>,
    onDetailInformationClick: (String) -> Unit,
    onStartQuizClick: (String) -> Unit,
)
{
    Scaffold { padding ->

        TopAppBar(
            title = { Text("All Species") },
            actions = {
                IconButton(onClick = { onStartQuizClick("quiz") }) {
                    Icon(Icons.Default.Quiz, contentDescription = "Kviz znanja")
                }
            }
        )

        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // 1) search bar part
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    label       = { Text("Search breeds…") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier    = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                )

                // 2) This Box now takes up all remaining space, for the lazy column
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)           // <— gives it a defined height
                ) {
                    Image(
                        painter = painterResource(R.drawable.my_wallpaper),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )

                    when {
                        loading -> {
                            Box(Modifier.fillMaxSize(), Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        error != null -> {
                            Box(Modifier.fillMaxSize(), Alignment.Center) {
                                Text("Error: ${error.message}")
                            }
                        }
                        else -> {
                            LazyColumn(
                                Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp)
                            ) {
                                items(breeds) { breed ->
                                    Card(
                                        onClick = { onDetailInformationClick(breed.id) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        ListItem(
                                            leadingContent = {
                                                AsyncImage(
                                                    model = breed.imageUrl,
                                                    contentDescription = breed.name,
                                                    modifier = Modifier
                                                        .size(64.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                )
                                            },
                                            headlineContent = {
                                                Text(
                                                    breed.name,
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                            },
                                            supportingContent = {
                                                Column(Modifier.fillMaxWidth()) {
                                                    // alt‐names or fallback
                                                    val altText = breed.altNames
                                                        ?.takeIf { it.isNotBlank() }
                                                        ?: "no alternative names"
                                                    Text(
                                                        "Alt: $altText",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        maxLines = 1, overflow = TextOverflow.Ellipsis
                                                    )

                                                    // truncated description
                                                    breed.description?.let { desc ->
                                                        val truncated = desc.takeIf { it.length <= 250 }
                                                            ?: desc.take(250) + "…"
                                                        Text(
                                                            truncated,
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            modifier = Modifier.padding(top = 4.dp),
                                                            maxLines = 3, overflow = TextOverflow.Ellipsis
                                                        )
                                                    }

                                                    // temperament chips
                                                    breed.temperament
                                                        ?.split(',')
                                                        ?.map { it.trim() }
                                                        ?.take(5)
                                                        ?.takeIf { it.isNotEmpty() }
                                                        ?.let { traits ->
                                                            Row(
                                                                Modifier
                                                                    .horizontalScroll(rememberScrollState())
                                                                    .padding(top = 8.dp),
                                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                            ) {
                                                                traits.forEach { trait ->
                                                                    AssistChip(
                                                                        onClick = { /* maybe filter later */ },
                                                                        label = { Text(trait) },
                                                                        colors = AssistChipDefaults.assistChipColors(
                                                                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                                                                            labelColor     = MaterialTheme.colorScheme.onPrimaryContainer
                                                                        ),
                                                                        modifier = Modifier.defaultMinSize(minHeight = 28.dp)
                                                                    )
                                                                }
                                                            }
                                                        }
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                }

            }
        }
    }
}