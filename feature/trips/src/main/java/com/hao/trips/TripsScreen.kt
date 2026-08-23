package com.hao.trips

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hao.data.model.LocalTrack
import com.hao.ui.HikeCardVertical

@Composable
fun TripsScreen(
    modifier: Modifier = Modifier,
    onHikeClick: (LocalTrack) -> Unit,
    viewModel: TripsViewModel = hiltViewModel()
) {
    val favoriteHikes by viewModel.favoriteHikes.collectAsStateWithLifecycle()
    val doneHikes by viewModel.doneHikes.collectAsStateWithLifecycle()
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Favourite", "Done")

    Column(modifier = modifier) {
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index }
                )
            }
        }
        when (tabIndex) {
            0 -> FavouriteScreen(
                hikes = favoriteHikes,
                onToggleFavorite = viewModel::toggleFavorite,
                onHikeClick = onHikeClick
            )

            1 -> DoneScreen(
                hikes = doneHikes,
                onHikeClick = onHikeClick,
                onToggleFavorite = viewModel::toggleFavorite
            )
        }
    }
}

@Composable
fun FavouriteScreen(
    hikes: List<LocalTrack>,
    onToggleFavorite: (LocalTrack) -> Unit,
    onHikeClick: (LocalTrack) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(hikes, key = { it.assetId }) { hike ->
            // For now, onToggleFavorite is empty, as we might not want to unfavorite from this screen.
            // Or we could pass the viewModel's toggle function here.
            HikeCardVertical(
                hike = hike,
                onToggleFavorite = onToggleFavorite,
                onClick = onHikeClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun DoneScreen(
    hikes: List<LocalTrack>,
    onHikeClick: (LocalTrack) -> Unit,
    onToggleFavorite: (LocalTrack) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(hikes, key = { it.assetId }) { hike ->
            HikeCardVertical(
                hike = hike,
                onToggleFavorite = onToggleFavorite,
                onClick = onHikeClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
