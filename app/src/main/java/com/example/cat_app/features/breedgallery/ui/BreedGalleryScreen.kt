package com.example.cat_app.features.breedgallery.ui

import android.util.Log
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import coil3.compose.AsyncImage


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import com.example.cat_app.features.breedgallery.domain.BreedImage


/// Grid of photos for a given breed. Opens from the breed details screen.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedGalleryScreen(
    viewModel: BreedGalleryViewModel = hiltViewModel(),
    onPhotoClick: (List<String>, Int) -> Unit,
    onClose: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect (state.breedId) {
        viewModel.setEvent(BreedGalleryContract.UiEvent.LoadImages(breedId = state.breedId))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gallery") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            when {
                state.loading -> CenteredProgress()
                state.error != null -> CenteredError(state.error!!.message)
                else -> PhotoGrid(images = state.images, onPhotoClick)      // pravim foto grid, i prosledjujem ovaj onPhotoClick, za slucaj ako se klikne na slicicu
            }
        }
    }
}

@Composable
private fun CenteredProgress() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun CenteredError(message: String?) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Error: $message")
    }
}

@Composable
private fun PhotoGrid(
    images: List<BreedImage>,
    onPhotoClick: (List<String>, Int) -> Unit
) {
    val urls = images.map { it.url }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(images) { index, img ->
            Card(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clickable {
                        onPhotoClick(urls, index)
                        Log.d("PhotoGrid", "Clicked image #$index → ${img.url}")
                    },
                shape = RoundedCornerShape(8.dp)
            ) {
                AsyncImage(
                    model = img.url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}


