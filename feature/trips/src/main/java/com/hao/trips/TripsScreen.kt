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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hao.data.model.Hike
import com.hao.ui.HikeCardVertical

@Composable
fun TripsScreen(
    modifier: Modifier = Modifier,
    onHikeClick: (Hike) -> Unit,
    viewModel: TripsViewModel = hiltViewModel()
) {
    val favoriteHikes by viewModel.favoriteHikes.collectAsState()
    val doneHikes by viewModel.doneHikes.collectAsState()
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Favourite", "Done")

    Column(modifier = modifier) {
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(text = { Text(title) },
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
    hikes: List<Hike>,
    onToggleFavorite: (Hike) -> Unit,
    onHikeClick: (Hike) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(hikes) { hike ->
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
    hikes: List<Hike>,
    onHikeClick: (Hike) -> Unit,
    onToggleFavorite: (Hike) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(hikes) { hike ->
            HikeCardVertical(
                hike = hike,
                onToggleFavorite = onToggleFavorite,
                onClick = onHikeClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
