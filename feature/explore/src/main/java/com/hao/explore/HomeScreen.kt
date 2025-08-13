package com.hao.explore

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hao.data.data.model.Track
import com.hao.data.model.Campsite
import com.hao.data.model.Hike
import com.hao.data.model.Hut

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit,
    onHikeClick: (Hike) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val tabs = listOf("Tracks", "Campsite", "Hut")
    val icons =
        listOf(
            painterResource(com.hao.ui.R.drawable.ic_hiking),
            painterResource(com.hao.ui.R.drawable.ic_camping),
            painterResource(com.hao.ui.R.drawable.ic_hut)
        )
    val listState = rememberLazyListState()
    var selectedTabIndex by remember { mutableStateOf(0) }

    val scrollY by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex * 200 +
                    listState.firstVisibleItemScrollOffset
        }
    }
    val scrollThreshold = 200f

    val animationProgress = remember {
        derivedStateOf {
            val progress = (scrollY / scrollThreshold).coerceIn(0f, 1f)
            progress
        }
    }

    val iconSize by animateDpAsState(
        targetValue = (80 * (1f - animationProgress.value)).dp,
        label = "iconSize"
    )

    val tabContainerHeight by animateDpAsState(
        targetValue = 120.dp - (80.dp * animationProgress.value),
        label = "tabContainerHeight"
    )

    Column {
        Spacer(modifier = Modifier.height(24.dp))
        SearchBar(onSearchClick = onSearchClick)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(tabContainerHeight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, title ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clickable { selectedTabIndex = index }
                ) {
                    Image(
                        painter = icons[index],
                        contentDescription = title,
                        modifier = Modifier
                            .size(iconSize)
                    )
                    Text(
                        text = title,
                        color = if (selectedTabIndex == index) Color.Black else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        when (selectedTabIndex) {
            0 -> TracksList(
                uiState = uiState,
                listState = listState,
                onHikeClick = onHikeClick,
                viewModel = viewModel
            )

            1 -> CampsitesList(campsites = uiState.campsites, viewModel = viewModel)
            2 -> HutsList(huts = uiState.huts, viewModel = viewModel)
        }
    }
}

@Composable
private fun TracksList(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    listState: LazyListState,
    onHikeClick: (Hike) -> Unit,
    viewModel: HomeViewModel
) {
    Column(modifier = modifier.fillMaxSize()) {
        HikingHomePage(
            listState = listState,
            uiState = uiState,
            onToggleFavorite = { viewModel.toggleFavorite(it) },
            onHikeClick = onHikeClick
        )
    }
}

@Composable
private fun CampsitesList(campsites: List<Campsite>, viewModel: HomeViewModel) {
    val searchQuery by viewModel.campsiteSearchQuery.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onCampsiteSearchQueryChanged(it) },
            label = { Text("Search Campsites") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(campsites) { campsite ->
                CampsiteItem(campsite = campsite)
            }
        }
    }
}

@Composable
private fun HutsList(huts: List<Hut>, viewModel: HomeViewModel) {
    val searchQuery by viewModel.hutSearchQuery.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onHutSearchQueryChanged(it) },
            label = { Text("Search Huts") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(huts) { hut ->
                HutItem(hut = hut)
            }
        }
    }
}

@Composable
private fun TrackItem(track: Track, onClick: () -> Unit) {
    Card(
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = track.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun CampsiteItem(campsite: Campsite) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = campsite.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("${campsite.facilities.joinToString(", ")}")
        }
    }
}

@Composable
private fun HutItem(hut: Hut) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = hut.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("${hut.beds} beds • ${hut.facilities.joinToString(", ")}")
        }
    }
}

@Composable
fun SearchBar(onSearchClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable { onSearchClick() }
    ) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            enabled = false,
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search Icon")
            },
            placeholder = {
                Text(text = "Search for a track")
            },
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
