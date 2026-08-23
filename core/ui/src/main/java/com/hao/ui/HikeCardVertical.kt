package com.hao.ui


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hao.data.model.LocalTrack

@Composable
fun HikeCardVertical(
    hike: LocalTrack,
    onToggleFavorite: (LocalTrack) -> Unit,
    onClick: (LocalTrack) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
            .height(220.dp)
            .clickable { onClick(hike) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.18f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box {
                AsyncImage(
                    model = hike.imageRes,
                    contentDescription = hike.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(110.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                )
                IconButton(
                    onClick = { onToggleFavorite(hike) },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (hike.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = stringResource(R.string.favorite),
                        tint = if (hike.isFavorite) Color.Red else Color.White
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
                Text(
                    stringResource(R.string.hike_difficulty, hike.difficulty),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    stringResource(R.string.hike_distance, hike.distanceKm),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    stringResource(R.string.hike_duration, hike.duration),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(name = "Track card", showBackground = true, widthDp = 200)
@Composable
private fun HikeCardVerticalPreview() {
    MaterialTheme {
        HikeCardVertical(
            hike = LocalTrack(
                assetId = "milford-track",
                name = "Milford Track",
                location = "Fiordland",
                distanceKm = 53.5,
                duration = "4 days",
                difficulty = "Intermediate",
                imageRes = R.drawable.milford,
                isFavorite = true
            ),
            onToggleFavorite = {},
            onClick = {}
        )
    }
}
