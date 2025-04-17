package com.example.cat_app.all_species_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Privremeni model za demo
data class Breed(val id: Int, val name: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllSpeciesScreen(
    viewModel: AllSpeciesViewModel,
    onDetailInformationClick: (Int) -> Unit,
) {
    // Preuzimamo kompletan state iz viewModela
    val uiState by viewModel.state.collectAsState()

    AllSpeciesScreenContent(
        searchQuery = uiState.searchQuery,
        onSearchChange = { viewModel.setEvent(AllSpeciesScreenContract.UiEvent.SearchQueryChanged(it)) },
        breeds = uiState.filteredBreeds.ifEmpty {
            // dok nema pravih podataka, pokaži par hardkodiranih
            listOf(
                Breed(1, "Abyssinian"),
                Breed(2, "Bengal"),
                Breed(3, "Maine Coon"),
                Breed(4, "Persian"),
                Breed(5, "Siamese"),
                Breed(6, "Sphynx")
            )
        },
        onDetailInformationClick = onDetailInformationClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AllSpeciesScreenContent(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    breeds: List<Breed>,
    onDetailInformationClick: (Int) -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                label = { Text("Search breeds...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            Spacer(Modifier.height(8.dp))

            // Lista rasa
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(breeds) { breed ->
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDetailInformationClick(breed.id) },
                        // Ovde koristimo headlineContent umesto headlineText
                        headlineContent = { Text(breed.name) }
                    )
                }
            }
        }
    }
}
