package com.hao.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hao.data.model.Campsite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampsiteDetailScreen(
    onBackClick: () -> Unit,
    viewModel: CampsiteDetailViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Campsite Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = ""
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
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
                ErrorScreen(
                    message = uiState.error ?: "",
                    onRetry = { viewModel.loadCampsiteDetails() },
                    modifier = Modifier.padding(padding)
                )
            }

            uiState.details != null -> {
                CampsiteDetailContent(
                    campsite = uiState.campsite,
                    details = uiState.details!!,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
private fun CampsiteDetailContent(
    campsite: Campsite?,
    details: CampsiteDetails,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Basic Info Section
        Text(
            text = details.introduction ?: "",
            style = MaterialTheme.typography.bodyLarge
        )

        // Facilities
        if (details.facilities?.isNotEmpty() == true) {
            DetailSection(
                title = "Facilities",
                items = details.facilities
            )
        }

        // Access
        details.access?.let { access ->
            DetailItem(
                title = "Access",
                value = access.joinToString(" ")
            )
        }

        // Fees
//        details.fees?.let { fees ->
//            DetailItem(
//                title = "Fees",
//                value = fees
//            )
//        }
//
//        // Amenities
//        val amenities = listOfNotNull(
//            details.waterSupply?.let { "Water: $it" },
//            details.toiletType?.let { "Toilets: $it" },
//            details.fireCooking?.let { "Fire/Cooking: $it" },
//            details.wheelchairAccess?.let { "Wheelchair Access: $it" },
//            details.dogsAllowed?.let { "Dogs Allowed: ${if (it) "Yes" else "No"}" }
//        )
//
//        if (amenities.isNotEmpty()) {
//            DetailSection(
//                title = "Amenities",
//                items = amenities
//            )
//        }
//
//        // Contact & Additional Info
//        details.contactInfo?.let { contact ->
//            DetailItem(
//                title = "Contact",
//                value = contact
//            )
//        }
//
//        details.additionalInfo?.let { info ->
//            DetailItem(
//                title = "Additional Information",
//                value = info
//            )
//        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    items: List<String>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items.forEach { item ->
                Text(
                    text = "• $item",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun DetailItem(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}
