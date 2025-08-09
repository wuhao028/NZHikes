package com.hao.explore

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

data class Hike(
    val name: String,
    val location: String,
    val distanceKm: Double,
    val duration: String,
    val difficulty: String,
    val imageRes: Int
)

val dayHikes = listOf(
    Hike("Hooker Valley Track", "Aoraki/Mt Cook", 10.0, "3h", "Easy", R.drawable.alexknob),
    Hike("Lake Marian", "Fiordland", 8.2, "3h", "Hard", R.drawable.tewharatrack),
    Hike("Taranaki Falls", "Tongariro", 6.0, "2h", "Easy", R.drawable.tongariroalpinecrossing),
    Hike("Rob Roy Glacier", "Mt Aspiring", 10.0, "4h", "Moderate", R.drawable.royspeak)
)

val greatWalks = listOf(
    Hike("Milford Track", "Fiordland", 53.5, "4 days", "Intermediate", R.drawable.milford),
    Hike(
        "Routeburn Track",
        "Fiordland/Mt Aspiring",
        32.0,
        "2–4 days",
        "Intermediate",
        R.drawable.routeburn
    ),
    Hike("Kepler Track", "Fiordland", 60.0, "3–4 days", "Intermediate", R.drawable.kepler),
    Hike("Rakiura Track", "Stewart Island", 32.0, "3 days", "Intermediate", R.drawable.rakiura),
    Hike("Heaphy Track", "Kahurangi", 78.4, "4–6 days", "Intermediate", R.drawable.heaphy),
    Hike(
        "Tongariro Northern Circuit",
        "Central Plateau",
        43.1,
        "3–4 days",
        "Advanced",
        R.drawable.tongariro
    ),
    Hike(
        "Abel Tasman Coast Track",
        "Abel Tasman",
        60.0,
        "3–5 days",
        "Easy",
        R.drawable.abel_tasman
    ),
    Hike(
        "Lake Waikaremoana Track",
        "Te Urewera",
        46.0,
        "3–4 days",
        "Intermediate",
        R.drawable.waikaremoana
    ),
    Hike("Whanganui Journey", "Whanganui River", 145.0, "3–5 days", "Easy", R.drawable.whanganui),
    Hike("Paparoa Track", "West Coast", 55.0, "2–3 days", "Intermediate", R.drawable.paparoa),
    Hike(
        name = "Hump Ridge Track",
        location = "Fiordland National Park, South Island",
        distanceKm = 61.0,
        duration = "3 days",
        difficulty = "Intermediate–Advanced",
        imageRes = R.drawable.humpridge
    )
)

@Composable
fun HikingHomePage(listState: LazyListState) {
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

        // Day Hikes 两列布局
        items(dayHikes.chunked(2)) { pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                for (hike in pair) {
                    HikeCardVertical(
                        hike,
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

        // Great Walks 两列布局
        items(greatWalks.chunked(2)) { pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                for (walk in pair) {
                    HikeCardVertical(
                        walk,
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

@Composable
fun HikeCardVertical(
    hike: Hike,
    modifier: Modifier = Modifier
) {
    var isFavorite by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box {
                Image(
                    painter = rememberAsyncImagePainter(hike.imageRes),
                    contentDescription = hike.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(110.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                )
                IconButton(
                    onClick = { isFavorite = !isFavorite },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.White
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = hike.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(Modifier.height(4.dp))

            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Difficulty: ${hike.difficulty}", style = MaterialTheme.typography.bodySmall)
                Text("Distance: ${hike.distanceKm} km", style = MaterialTheme.typography.bodySmall)
                Text("Duration: ${hike.duration}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

