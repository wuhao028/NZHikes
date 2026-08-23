package com.hao.me

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.hao.data.repository.HikeRepository
import com.hao.ui.theme.ThemeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private object MeScreenConstants {
    val CARD_ELEVATION = 4.dp
    val CARD_CORNER_RADIUS = 12.dp
    val CARD_PADDING = 16.dp
    val SPACING_BETWEEN_CARDS = 16.dp
    val SPACING_BETWEEN_ELEMENTS = 12.dp
    val ICON_SIZE = 24.dp
    val LARGE_ICON_SIZE = 48.dp
    val SMALL_ICON_SIZE = 20.dp
    val SMALL_SPACING = 8.dp
    val MEDIUM_SPACING = 12.dp
    
}

@HiltViewModel
class MeViewModel @Inject constructor(
    private val hikeRepository: HikeRepository,
    private val themeManager: ThemeManager
) : ViewModel() {

    val favoriteCount: StateFlow<Int> = hikeRepository.getFavoriteHikes()
        .map { it.size }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val doneCount: StateFlow<Int> = hikeRepository.getDoneHikes()
        .map { it.size }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val isDarkMode: StateFlow<Boolean> = themeManager.darkModeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun updateDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            try {
                themeManager.updateDarkMode(enabled)
            } catch (exception: Exception) {
                // Handle theme update error
            }
        }
    }
}

@Composable
fun MeScreen() {
    val scrollState = rememberScrollState()
    val viewModel: MeViewModel = hiltViewModel()
    val favoriteCount by viewModel.favoriteCount.collectAsStateWithLifecycle()
    val doneCount by viewModel.doneCount.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(MeScreenConstants.CARD_PADDING),
        verticalArrangement = Arrangement.spacedBy(MeScreenConstants.SPACING_BETWEEN_CARDS)
    ) {
        ProfileCard(isDarkMode = isDarkMode)
        StatsCard(
            favoriteCount = favoriteCount,
            doneCount = doneCount,
            isDarkMode = isDarkMode
        )
        SettingsCard(
            isDarkMode = isDarkMode,
            onDarkModeChange = viewModel::updateDarkMode
        )
    }
}

@Composable
private fun ProfileCard(isDarkMode: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = MeScreenConstants.CARD_ELEVATION),
        shape = RoundedCornerShape(MeScreenConstants.CARD_CORNER_RADIUS),
        colors = CardDefaults.cardColors(
            containerColor = getCardBackgroundColor()
        )
    ) {
        Column(
            modifier = Modifier.padding(MeScreenConstants.CARD_PADDING),
            verticalArrangement = Arrangement.spacedBy(MeScreenConstants.SPACING_BETWEEN_ELEMENTS)
        ) {
            ProfileHeader()
            HorizontalDivider()
            ProfileContent()
        }
    }
}

@Composable
private fun ProfileHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(MeScreenConstants.ICON_SIZE)
        )
        Spacer(modifier = Modifier.width(MeScreenConstants.MEDIUM_SPACING))
        Text(
            text = stringResource(R.string.profile_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ProfileContent() {
    Column {
        Text(
            text = stringResource(R.string.user_name),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = stringResource(R.string.user_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingsCard(
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = MeScreenConstants.CARD_ELEVATION),
        shape = RoundedCornerShape(MeScreenConstants.CARD_CORNER_RADIUS),
        colors = CardDefaults.cardColors(
            containerColor = getCardBackgroundColor()
        )
    ) {
        Column(
            modifier = Modifier.padding(MeScreenConstants.CARD_PADDING),
            verticalArrangement = Arrangement.spacedBy(MeScreenConstants.SPACING_BETWEEN_ELEMENTS)
        ) {
            SettingsHeader()
            HorizontalDivider()
            DarkModeToggle(
                isDarkMode = isDarkMode,
                onDarkModeChange = onDarkModeChange
            )
            Spacer(modifier = Modifier.height(MeScreenConstants.SMALL_SPACING))
            AppInfoSection()
        }
    }
}

@Composable
private fun SettingsHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(MeScreenConstants.ICON_SIZE)
        )
        Spacer(modifier = Modifier.width(MeScreenConstants.MEDIUM_SPACING))
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DarkModeToggle(
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        DarkModeInfo(isDarkMode = isDarkMode)
        Switch(
            checked = isDarkMode,
            onCheckedChange = onDarkModeChange
        )
    }
}

@Composable
private fun DarkModeInfo(isDarkMode: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(MeScreenConstants.SMALL_ICON_SIZE)
        )
        Spacer(modifier = Modifier.width(MeScreenConstants.MEDIUM_SPACING))
        Column {
            Text(
                text = stringResource(R.string.dark_mode_label),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = stringResource(
                    if (isDarkMode) R.string.dark_mode_enabled else R.string.dark_mode_disabled
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AppInfoSection() {
    Column(
        verticalArrangement = Arrangement.spacedBy(MeScreenConstants.SMALL_SPACING)
    ) {
        Text(
            text = stringResource(R.string.app_info_title),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = stringResource(R.string.app_display_name),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        
        Text(
            text = stringResource(R.string.app_version),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = stringResource(R.string.app_description),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatsCard(
    favoriteCount: Int,
    doneCount: Int,
    isDarkMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = MeScreenConstants.CARD_ELEVATION),
        shape = RoundedCornerShape(MeScreenConstants.CARD_CORNER_RADIUS),
        colors = CardDefaults.cardColors(
            containerColor = getCardBackgroundColor()
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MeScreenConstants.CARD_PADDING),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(MeScreenConstants.LARGE_ICON_SIZE)
            )
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = pluralStringResource(
                        R.plurals.favourites_count,
                        favoriteCount,
                        favoriteCount
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(MeScreenConstants.SMALL_SPACING))
                Text(
                    text = pluralStringResource(
                        R.plurals.completed_count,
                        doneCount,
                        doneCount
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun getCardBackgroundColor(): Color {
    return MaterialTheme.colorScheme.surface
}

@Preview(name = "Profile and settings", showBackground = true, widthDp = 390, heightDp = 760)
@Composable
private fun MeScreenContentPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileCard(isDarkMode = false)
            StatsCard(favoriteCount = 6, doneCount = 3, isDarkMode = false)
            SettingsCard(isDarkMode = false, onDarkModeChange = {})
        }
    }
}
