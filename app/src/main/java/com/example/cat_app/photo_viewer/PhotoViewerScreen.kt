package com.example.cat_app.photo_viewer

// TODO

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
//import com.google.accompanist.pager.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close


//@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
//@Composable
//fun PhotoViewerScreen(
//    viewModel: PhotoViewerViewModel = hiltViewModel(),
//    onClose: () -> Unit
//) {
//    val state by viewModel.state.collectAsState()
////    val pagerState = rememberPagerState(initialPage = state.currentIndex)
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("${pagerState.currentPage + 1}/${state.images.size}") },
//                navigationIcon = {
//                    IconButton(onClick = onClose) {
//                        Icon(Icons.Default.Close, contentDescription = "Close")
//                    }
//                }
//            )
//        }
//    ) { paddingValues ->
//        HorizontalPager(
//            count = state.images.size,
//            state = pagerState,
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//        ) { page ->
//            LaunchedEffect(page) {
//                viewModel.setPage(page)
//            }
//            AsyncImage(
//                model = state.images[page],
//                contentDescription = null,
//                modifier = Modifier.fillMaxSize(),
//                contentScale = ContentScale.Fit
//            )
//        }
//    }
//}