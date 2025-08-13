package com.hao.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hao.data.model.LocalTrack
import com.hao.ui.HikeCardVertical


@Composable
fun HikingHomePage(
    listState: LazyListState,
    uiState: HomeUiState,
    onToggleFavorite: (LocalTrack) -> Unit,
    onHikeClick: (LocalTrack) -> Unit
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Day Hikes", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
        }

        items(uiState.dayHikes.chunked(2)) { pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                for (hike in pair) {
                    HikeCardVertical(
                        hike = hike,
                        onToggleFavorite = onToggleFavorite,
                        onClick = onHikeClick,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        item {
            Text("Great Walks", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
        }

        items(uiState.greatWalks.chunked(2)) { pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                for (walk in pair) {
                    HikeCardVertical(
                        hike = walk,
                        onToggleFavorite = onToggleFavorite,
                        onClick = onHikeClick,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}


