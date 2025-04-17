package com.example.cat_app.all_species_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.cat_app.domain.Breed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllSpeciesScreen(
    viewModel: AllSpeciesViewModel = hiltViewModel(),       // Hilt ubacuje ViewModel
    onDetailInformationClick: (String) -> Unit,             // sad id tipično String
) {
    val uiState by viewModel.state.collectAsState()

    AllSpeciesScreenContent(
        loading     = uiState.loading,
        error       = uiState.error,
        searchQuery = uiState.searchQuery,
        onSearchChange = { viewModel.setEvent(AllSpeciesScreenContract.UiEvent.SearchQueryChanged(it)) },
        breeds      = uiState.filteredBreeds,
        onDetailInformationClick = onDetailInformationClick
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
    onDetailInformationClick: (String) -> Unit
) {
    Scaffold { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 1) Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                label       = { Text("Search breeds...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier    = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            Spacer(Modifier.height(8.dp))

            // 2) Content area: Loading / Error / List
            when {
                loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                            ListItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onDetailInformationClick(breed.id) },
                                leadingContent = {
                                    AsyncImage(
                                        model = breed.imageUrl,
                                        contentDescription = breed.name,
                                        modifier = Modifier.size(56.dp)
                                    )
                                },
                                headlineContent = { Text(breed.name) }
                            )
                        }
                    }
                }
            }
        }
    }
}
