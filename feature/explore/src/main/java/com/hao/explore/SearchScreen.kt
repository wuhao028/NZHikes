package com.hao.explore

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onTrackClick: (String) -> Unit,
    onCancel: () -> Unit = {}
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                label = { Text("Search for a track") },
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onCancel, modifier = Modifier.padding(start = 8.dp)) {
                Text("Cancel")
            }
        }

        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            items(searchResults) { track ->
                Text(
                    text = track.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { 
                            Log.d("SearchScreen", "Track clicked: ${track.assetId}")
                            onTrackClick(track.assetId) 
                        }
                )
            }
        }
    }
}
