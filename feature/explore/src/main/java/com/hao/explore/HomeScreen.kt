package com.hao.nzhikes.explore

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hao.data.data.model.Track

@Composable
private fun TracksList(tracks: List<Track>, listState: LazyListState) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tracks) { track ->
            TrackItem(track = track)
        }
    }
}

@Composable
private fun CampsitesList(campsites: List<Campsite>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(campsites) { campsite ->
            CampsiteItem(campsite = campsite)
        }
    }
}

@Composable
private fun HutsList(huts: List<Hut>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(huts) { hut ->
            HutItem(hut = hut)
        }
    }
}

@Composable
private fun TrackItem(track: Track) {
    Card {
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
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val tabs = listOf("Tracks", "Campsite", "Hut")
    val icons =
        listOf(Icons.Default.DirectionsWalk, Icons.Default.LocalFireDepartment, Icons.Default.Home)
    val listState = rememberLazyListState()
    val tracksListState = rememberLazyListState()
    var selectedTabIndex by remember { mutableStateOf(0) }

    val showIcons by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset < 10
        }
    }

    val iconAlpha by animateFloatAsState(
        targetValue = if (showIcons) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "iconAlpha"
    )

    val iconSize by animateDpAsState(
        targetValue = if (showIcons) 24.dp else 0.dp,
        animationSpec = tween(durationMillis = 300),
        label = "iconSize"
    )

    Column {
        SearchBar()
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = icons[index],
                            contentDescription = title,
                            modifier = Modifier
                                .size(iconSize)
                                .alpha(iconAlpha)
                        )
                        Text(text = title)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tab 内容
        when (selectedTabIndex) {
            0 -> TracksList(tracks = uiState.tracks, listState)
            1 -> CampsitesList(campsites = uiState.campsites)
            2 -> HutsList(huts = uiState.huts)
        }


        // 内容区
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(30) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(100.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Item $index", fontSize = 20.sp)
                }
            }
        }
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



