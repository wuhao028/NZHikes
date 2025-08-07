package com.hao.nzhikes.explore

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hao.data.data.model.Track

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val tabs = listOf("Tracks", "Campsite", "Hut")
    var selectedTabIndex by remember { mutableStateOf(0) }
    var isSearchExpanded by remember { mutableStateOf(false) }
    val transition = updateTransition(targetState = isSearchExpanded, label = "SearchTransition")

    val searchBoxWidth by transition.animateDp(label = "SearchWidth") { expanded ->
        if (expanded) 300.dp else 200.dp
    }

    val searchBoxAlpha by transition.animateFloat(label = "SearchAlpha") { expanded ->
        if (expanded) 1f else 0.8f
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 32.dp)
    ) {
        // 搜索框
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .height(56.dp)
                .width(searchBoxWidth)
                .alpha(searchBoxAlpha)
                .background(Color(0xFFF0F0F0), RoundedCornerShape(28.dp))
                .clickable { isSearchExpanded = !isSearchExpanded }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "Search...",
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tab 行
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),
            contentColor = Color.Black
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tab 内容
        when (selectedTabIndex) {
            0 -> TracksList(tracks = uiState.tracks)
            1 -> CampsitesList(campsites = uiState.campsites)
            2 -> HutsList(huts = uiState.huts)
        }
    }
}


@Composable
private fun TracksList(tracks: List<Track>) {
    LazyColumn(
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


