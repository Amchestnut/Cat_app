package com.example.cat_app.features.allspecies.ui

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.ScaffoldDefaults.contentWindowInsets
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.cat_app.R
import com.example.cat_app.core.ui.theme.CatBorder
import com.example.cat_app.features.allspecies.domain.Breed


@Composable
fun AllSpeciesScreen(
    viewModel: AllSpeciesViewModel = hiltViewModel(),
    onDetailInformationClick: (String) -> Unit,             //  id String
) {
    // 1) Pokreće se jedna korutina čim se aktivira ovaj composable
    // 2) Ona radi state.collectAsState(), što znači “pretplati se na StateFlow”
    // 3) Čuva poslednju emitovanu vrednost u Compose‐ovom State<T>.
    // 4) Svaki put kad se StateFlow emituje nova UiState vrednost, collectAsState ažurira svoj SnapshotState.value …
    // 5) … i kad god se taj SnapshotState.value promeni, --------Compose automatski pokreće recomposition-------- za sve delove koda koji čitaju uiState (pametannnnn)
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
        onRefresh = {
            viewModel.setEvent(AllSpeciesScreenContract.UiEvent.LoadBreeds)
        }
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
    onRefresh: () -> Unit,
) {
    val listState = rememberLazyListState()

    // ovaj state je TRUE samo kada user pull-uje na dole da refreshuje (nema veze sa "loading" parametrom)
    var isRefreshing by rememberSaveable { mutableStateOf(false) }      // mada nije bitno da bude saveable ovde, ali ajde, nek se sacuva izmedju konfiguracionih

    // kada zavrsimo sa loadovanjem data (kad loading postane false), postavicemo nas lokalni isRefreshing state na false (ovo bi trebalo da sakrije indikator)
    LaunchedEffect(loading) {
        if (!loading) {
            isRefreshing = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Catapult",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color.White,
                        modifier = Modifier
//                            .align(Alignment.CenterVertically)
                            .padding(start = 8.dp, top = 18.dp)

                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CatBorder,
                    titleContentColor = Color.White
                ),
                // pomeramo naslov samo 16dp od leve ivice
//                 = MaterialTheme.typography.titleLarge,
                windowInsets = WindowInsets(0.dp),
                actions = {}
            )
        },
        contentWindowInsets = WindowInsets(0),
    ) { padding ->

        Box(
            Modifier.fillMaxWidth()
        ) {
            // Background image
            Image(
                painter = painterResource(R.drawable.my_wallpaper),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
//            val isDark = isSystemInDarkTheme()

            // Search bar at the top
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                label = { Text("Search breeds…") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Wrapuje content koji treba da se refreshuje.
            // sada je ovaj nas isRefreshing vezan za lokalni, tacno managed state
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true // Show the indicator immediately
                    onRefresh()         // Triggeruj data fetch u ViewModelu
                },
                modifier = Modifier.fillMaxSize()
            ) {

                    when {
                        // Initial loading: Pokazi centralizovani krug spinner loading
                        loading && breeds.isEmpty() -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }

                        // Error state
                        error != null -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Error: ${error.message}")
                            }
                        }

                        // Content list
                        else -> {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
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
                                                        .clip(RoundedCornerShape(8.dp)),
                                                    contentScale = ContentScale.Crop
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
                                                    // Alt names
                                                    val altText = breed.altNames
                                                        ?.takeIf { it.isNotBlank() }
                                                        ?: "no alternative names"
                                                    Text(
                                                        "Alt: $altText",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    // Description
                                                    breed.description?.let { desc ->
                                                        val truncated = if (desc.length <= 250) desc
                                                        else desc.take(250) + "…"
                                                        Text(
                                                            truncated,
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            modifier = Modifier.padding(top = 4.dp),
                                                            maxLines = 3,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                    // Temperament chips
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
                                                                        onClick = { /* later */ },
                                                                        label = { Text(trait) },
                                                                                colors = AssistChipDefaults.assistChipColors(
                                                                                containerColor = CatBorder ,  // tvoja #D49041
                                                                        labelColor     = Color.White           // ili neka druga za kontrast
                                                                        ),

//                                                                        border = null
                                                                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
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