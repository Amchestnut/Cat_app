package com.example.cat_app.features.photo_viewer

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
//import com.google.accompanist.pager.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PhotoViewerScreen(
    viewModel: PhotoViewerViewModel = hiltViewModel(),
    onClose: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect (Unit){
        Log.d("LaunchedEffect from PhotoViewerScreen", "Opened photo viewer")
        viewModel.setEvent(PhotoViewerContract.UiEvent.LoadDetails(state.breedId))
    }

    val pagerState = rememberPagerState(
        initialPage = state.currentIndex,
        initialPageOffsetFraction = 0f,
        pageCount = { state.images.size }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${pagerState.currentPage + 1}/${state.images.size}") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, null)
                    }
                }
            )
        }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) { page ->
            // Svaki put kad pager prikaze novu stranicu menjamo u modelu STANJE, i prikazemo trenutnu stranicu
            LaunchedEffect(page) {
                Log.d("PhotoViewerScreen", "Displaying page=$page, url=${state.images.getOrNull(page)}")
                viewModel.setEvent(PhotoViewerContract.UiEvent.SetPage(page))
            }
            AsyncImage(
                model = state.images[page],
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}