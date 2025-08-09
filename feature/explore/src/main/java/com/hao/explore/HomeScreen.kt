package com.hao.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    Column {
        Spacer(modifier = Modifier.height(24.dp))
        SearchBar()
        HikingHomePage(
            listState = listState,
            uiState = uiState,
            onToggleFavorite = { hike -> viewModel.toggleFavorite(hike) }
        )
    }
}

@Composable
fun SearchBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .height(56.dp)
            .background(Color.White, shape = RoundedCornerShape(28.dp))
            .shadow(4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "Search tracks, campsites...",
            modifier = Modifier.padding(horizontal = 16.dp),
            color = Color.Gray
        )
    }
}



