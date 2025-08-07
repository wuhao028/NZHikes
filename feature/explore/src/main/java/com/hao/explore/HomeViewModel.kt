package com.hao.nzhikes.explore

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hao.data.data.local.TrackDatabase
import com.hao.data.data.model.Track
import com.hao.data.data.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val application: Application) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private val repository = TrackRepository.getInstance(
        TrackDatabase.getDatabase(application)
    )

    init {
        loadTracks()
    }

    private fun loadTracks() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                // Load tracks from assets to database if needed
                System.currentTimeMillis()
                repository.loadTracksFromAssets(application)

                // Observe tracks from database
                repository.getTracks().collectLatest { tracks ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        tracks = tracks
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading tracks", e)
                _uiState.value = _uiState.value.copy(error = e.message ?: "Unknown error")
            }
        }
    }

    companion object {
        private const val TAG = "TracksViewModel"
    }
}

data class HomeUiState(
    val tracks: List<Track> = emptyList(),
    val campsites: List<Campsite> = emptyList(),
    val huts: List<Hut> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class Campsite(
    val id: String,
    val name: String,
    val location: String,
    val facilities: List<String>,
    val imageUrl: String? = null
)

data class Hut(
    val id: String,
    val name: String,
    val location: String,
    val beds: Int,
    val facilities: List<String>,
    val imageUrl: String? = null
)

private val sampleCampsites = listOf(
    Campsite(
        "c1", "Mangatepopo Campsite", "Tongariro National Park",
        listOf("Toilets", "Water supply")
    ),
    Campsite(
        "c2", "Whakapapa Holiday Park", "Whakapapa Village",
        listOf("Kitchen", "Showers", "Power sites", "WiFi")
    )
)

private val sampleHuts = listOf(
    Hut(
        "h1", "Lake Mackenzie Hut", "Routeburn Track", 50,
        listOf("Mattresses", "Water supply", "Toilets", "Heating")
    ),
    Hut(
        "h2", "Pouakai Hut", "Egmont National Park", 16,
        listOf("Mattresses", "Water supply", "Toilets", "Heating")
    )
)
