package com.hao.explore

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.hao.data.model.LocalTrack
import com.hao.data.remote.TrackDetailsResponse
import com.hao.data.util.CoordinateUtil
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackDetailScreen(
    onBackClick: () -> Unit = {},
    viewModel: TrackDetailViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = uiState.data?.name ?: stringResource(R.string.track_details),
                        style = MaterialTheme.typography.titleMedium
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        when {
            uiState.loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    Text(
                        text = uiState.error ?: stringResource(R.string.error_title),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            else -> uiState.data?.let { data ->
                TrackDetailContent(
                    data = data,
                    hike = uiState.hike,
                    onToggleFavorite = viewModel::toggleFavorite,
                    onToggleDone = viewModel::toggleDone,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
fun TrackDetailContent(
    data: TrackDetailsResponse,
    hike: LocalTrack?,
    onToggleFavorite: (LocalTrack) -> Unit,
    onToggleDone: (LocalTrack) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            AsyncImage(
                model = data.introductionThumbnail,
                contentDescription = data.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            )
        }

        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = data.name ?: stringResource(R.string.track_name),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = data.locationString ?: stringResource(R.string.location),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    IconButton(onClick = { hike?.let { onToggleFavorite(it) } }) {
                        Icon(
                            imageVector = if (hike?.isFavorite == true) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = stringResource(R.string.favorite)
                        )
                    }
                    IconButton(onClick = { hike?.let { onToggleDone(it) } }) {
                        Icon(
                            imageVector = if (hike?.isDone == true) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                            contentDescription = stringResource(R.string.mark_as_done)
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                InfoCard(
                    icon = Icons.Default.Hiking,
                    label = stringResource(R.string.distance),
                    value = data.distance ?: stringResource(R.string.not_available)
                )
                InfoCard(
                    icon = Icons.Default.Schedule,
                    label = stringResource(R.string.duration),
                    value = data.walkDuration ?: stringResource(R.string.not_available)
                )
                InfoCard(
                    icon = Icons.Default.Terrain,
                    label = stringResource(R.string.difficulty),
                    value = data.walkTrackCategory?.firstOrNull()
                        ?: stringResource(R.string.not_available)
                )
            }
        }

        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.about_track),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = data.introduction ?: stringResource(R.string.no_introduction),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.track_map),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                val context = LocalContext.current
                MapCard(context = context, track = data)
            }
        }
    }
}

@Composable
fun MapCard(
    context: Context,
    track: TrackDetailsResponse,
    modifier: Modifier = Modifier
) {
    val geoPoints = remember(track.line) {
        track.line?.flatMap { line ->
            line.mapNotNull { coord ->
                if (coord.size >= 2) {
                    val easting = coord[0]
                    val northing = coord[1]
                    val wgs84 = CoordinateUtil.nztmToWgs84(easting, northing)
                    GeoPoint(wgs84.y, wgs84.x)
                } else null
            }
        } ?: emptyList()
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            AndroidView(
                factory = {
                    MapView(it).apply {
                        Configuration.getInstance()
                            .load(it, it.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
                        setTileSource(TileSourceFactory.MAPNIK)
                    }
                },
                update = { mapView ->
                    mapView.overlayManager.clear()
                    if (geoPoints.isNotEmpty()) {
                        val polyline = Polyline().apply {
                            setPoints(geoPoints)
                            outlinePaint.color = android.graphics.Color.RED
                            outlinePaint.strokeWidth = 8f
                        }
                        mapView.controller.setZoom(14.0)
                        mapView.controller.setCenter(geoPoints.first())
                        mapView.overlayManager.add(polyline)
                    } else {
                        mapView.controller.setZoom(5.0)
                        mapView.controller.setCenter(GeoPoint(-41.28664, 174.77557))
                    }
                    mapView.invalidate()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
        }
    }
}

@Composable
fun RowScope.InfoCard(icon: ImageVector, label: String, value: String) {
    Card(
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(name = "Track information cards", showBackground = true, widthDp = 390)
@Composable
private fun TrackInfoCardsPreview() {
    MaterialTheme {
        Row(modifier = Modifier.padding(12.dp)) {
            InfoCard(Icons.Default.Hiking, "Distance", "19.4 km")
            InfoCard(Icons.Default.Schedule, "Duration", "7–8 hr")
            InfoCard(Icons.Default.Terrain, "Difficulty", "Advanced")
        }
    }
}
