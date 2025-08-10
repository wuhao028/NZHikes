package com.hao.explore

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.hao.data.remote.TrackDetailsResponse
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline


@Composable
fun TrackDetailScreen(
    viewModel: TrackDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }
        uiState.error != null -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(text = uiState.error ?: "Error", color = MaterialTheme.colorScheme.error)
            }
        }

        else -> uiState.data?.let { data ->
            TrackDetailContent(data)
        }
    }
}

@Composable
fun TrackDetailContent(data: TrackDetailsResponse) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
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
                    text = data.name ?: "Track Name",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = data.locationString ?: "Location",
                    style = MaterialTheme.typography.bodyLarge
                )
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
                    label = "Distance",
                    value = data.distance ?: "N/A"
                )
                InfoCard(
                    icon = Icons.Default.Schedule,
                    label = "Duration",
                    value = data.walkDuration ?: "N/A"
                )
                InfoCard(
                    icon = Icons.Default.Terrain,
                    label = "Difficulty",
                    value = data.walkTrackCategory?.firstOrNull() ?: "N/A"
                )
            }
        }

        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "About this track",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = data.introduction ?: "No introduction available.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Track Map",
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
                    val lon = coord[0]
                    val lat = coord[1]
                    GeoPoint(lat, lon)
                } else null
            }
        } ?: emptyList()
    }

    val mapView = remember {
        MapView(context).apply {
            Configuration.getInstance()
                .load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
            setTileSource(TileSourceFactory.MAPNIK)
            controller.setZoom(14.0)
            if (geoPoints.isNotEmpty()) {
                controller.setCenter(geoPoints.first())
                val polyline = Polyline().apply {
                    setPoints(geoPoints)
                    outlinePaint.color = android.graphics.Color.RED
                    outlinePaint.strokeWidth = 5f
                }
                overlayManager.add(polyline)
            }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            AndroidView(
                factory = { mapView },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
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
