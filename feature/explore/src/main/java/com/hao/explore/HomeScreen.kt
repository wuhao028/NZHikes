package com.hao.explore

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hao.data.data.model.RemoteTrack
import com.hao.data.model.Campsite
import com.hao.data.model.Hut
import com.hao.data.model.LocalTrack

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onSearchClick: (Int) -> Unit,
    onHikeClick: (LocalTrack) -> Unit,
    onCampsiteClick: (Campsite) -> Unit = {},
    onHutClick: (Hut) -> Unit = {},
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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

    val iconContainerSize by animateDpAsState(
        targetValue = (62 * (1f - animationProgress.value)).dp,
        label = "iconContainerSize"
    )

    val tabContainerHeight by animateDpAsState(
        targetValue = 108.dp - (56.dp * animationProgress.value),
        label = "tabContainerHeight"
    )

    Column {
        Spacer(modifier = Modifier.height(24.dp))
        Column {
            // Search bar
            val searchPlaceholder = stringResource(
                when (selectedTabIndex) {
                    0 -> R.string.search_track_hint
                    1 -> R.string.search_campsite_hint
                    2 -> R.string.search_hut_hint
                    else -> R.string.search
                }
            )
            SearchBar(
                onSearchClick = { onSearchClick(selectedTabIndex) },
                placeholder = searchPlaceholder,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
        HomeCategoryTabs(
            selectedTabIndex = selectedTabIndex,
            iconContainerSize = iconContainerSize,
            onTabSelected = { selectedTabIndex = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(tabContainerHeight)
        )
        when (selectedTabIndex) {
            0 -> TracksList(
                uiState = uiState,
                listState = listState,
                onHikeClick = onHikeClick,
                viewModel = viewModel
            )

            1 -> CampsitesList(
                campsites = uiState.campsites,
                listState = listState,
                onCampsiteClick = onCampsiteClick
            )

            2 -> HutsList(
                huts = uiState.huts,
                listState = listState,
                onHutClick = onHutClick
            )
        }
    }
}

@Composable
private fun HomeCategoryTabs(
    selectedTabIndex: Int,
    iconContainerSize: androidx.compose.ui.unit.Dp,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        stringResource(R.string.tracks),
        stringResource(R.string.campsite),
        stringResource(R.string.hut)
    )
    val icons = listOf(Icons.Default.Terrain, Icons.Default.Forest, Icons.Default.Cabin)
    val iconColors = listOf(
        Color(0xFF2E7D32),
        Color(0xFF00838F),
        Color(0xFFE65100)
    )

    Row(
        modifier = modifier.padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEachIndexed { index, title ->
            HomeCategoryTab(
                title = title,
                icon = icons[index],
                accentColor = iconColors[index],
                selected = selectedTabIndex == index,
                iconContainerSize = iconContainerSize,
                onClick = { onTabSelected(index) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            )
        }
    }
}

@Composable
private fun HomeCategoryTab(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    selected: Boolean,
    iconContainerSize: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        targetValue = if (selected) {
            accentColor.copy(alpha = 0.22f)
        } else {
            accentColor.copy(alpha = 0.12f)
        },
        label = "categoryContainerColor"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) {
            accentColor
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        label = "categoryContentColor"
    )
    val shape = RoundedCornerShape(20.dp)

    Column(
        modifier = modifier
            .clip(shape)
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(iconContainerSize)
                .clip(RoundedCornerShape(18.dp))
                .background(containerColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(iconContainerSize * 0.56f)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            color = contentColor,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(24.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(50))
                .background(if (selected) accentColor else Color.Transparent)
        )
    }
}

@Preview(
    name = "Home category tabs",
    showBackground = true,
    backgroundColor = 0xFFF5F7F2,
    widthDp = 390,
    heightDp = 120
)
@Composable
private fun HomeCategoryTabsPreview() {
    MaterialTheme {
        Surface(color = Color(0xFFF5F7F2)) {
            HomeCategoryTabs(
                selectedTabIndex = 0,
                iconContainerSize = 62.dp,
                onTabSelected = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(108.dp)
            )
        }
    }
}

@Preview(name = "Home search and list items", showBackground = true, widthDp = 390, heightDp = 360)
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun HomeListItemsPreview() {
    MaterialTheme {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SearchBar(
                onSearchClick = {},
                placeholder = stringResource(R.string.search_campsite_hint)
            )
            CampsiteItem(
                campsite = Campsite("camp-preview", "Lake Rotoiti Campsite", "Open", "Nelson Lakes"),
                onClick = {}
            )
            HutItem(
                hut = Hut("hut-preview", "Mueller Hut", "Open", "Aoraki / Mt Cook"),
                onClick = {}
            )
        }
    }
}

@Composable
private fun TracksList(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    listState: LazyListState,
    onHikeClick: (LocalTrack) -> Unit,
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
private fun CampsitesList(
    campsites: List<Campsite>,
    listState: LazyListState,
    onCampsiteClick: (Campsite) -> Unit
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(campsites, key = { it.assetId }) { campsite ->
            CampsiteItem(campsite = campsite, onClick = { onCampsiteClick(campsite) })
        }
    }
}

@Composable
private fun HutsList(
    huts: List<Hut>,
    listState: LazyListState,
    onHutClick: (Hut) -> Unit
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(huts, key = { it.assetId }) { hut ->
            HutItem(hut = hut, onClick = { onHutClick(hut) })
        }
    }
}

@Composable
private fun TrackItem(track: RemoteTrack, onClick: () -> Unit) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.18f))
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
private fun CampsiteItem(campsite: Campsite, onClick: () -> Unit) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.22f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = campsite.name ?: "",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            campsite.region?.let { region ->
                if (region.isNotBlank()) {
                    Text(
                        text = region,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun HutItem(hut: Hut, onClick: () -> Unit) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.22f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = hut.name ?: "",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            hut.region?.let { region ->
                if (region.isNotBlank()) {
                    Text(
                        text = region,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SearchBar(
    onSearchClick: () -> Unit,
    placeholder: String? = null,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    modifier: Modifier = Modifier
) {
    val resolvedPlaceholder = placeholder ?: stringResource(R.string.search_track_hint)
    val sharedBoundsModifier = if (
        sharedTransitionScope != null && animatedVisibilityScope != null
    ) {
        with(sharedTransitionScope) {
            Modifier.sharedBounds(
                sharedContentState = rememberSharedContentState(key = "home-search-field"),
                animatedVisibilityScope = animatedVisibilityScope
            )
        }
    } else {
        Modifier
    }
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
                Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search_icon))
            },
            placeholder = {
                Text(text = resolvedPlaceholder)
            },
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.65f),
                disabledContainerColor = MaterialTheme.colorScheme.surface,
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLeadingIconColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .then(sharedBoundsModifier)
        )
    }
}
