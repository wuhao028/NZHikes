package com.hao.explore

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@Composable
fun TrackDetailScreen(
    viewModel: TrackDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.loading -> {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                CircularProgressIndicator()
            }
        }
        uiState.error != null -> {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text(text = uiState.error ?: "Error", color = MaterialTheme.colorScheme.error)
            }
        }
        else -> {
            val data = uiState.data
            if (data != null) {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    item {
                        Text(text = data.name ?: "", style = MaterialTheme.typography.headlineSmall)
                        Text(text = data.introduction ?: "", modifier = Modifier.padding(top = 8.dp))
                    }
//                    Image(
//                        model = data.imageUrl,
//                        contentDescription = null,
//                        modifier = Modifier.padding(top = 12.dp)
//                    )
                }
            }
        }
    }
}
