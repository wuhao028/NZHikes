package com.hao.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hao.data.model.Hike
import com.hao.data.repository.HikeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val hikeRepository: HikeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            prepopulateDatabaseIfNeeded()
            hikeRepository.getAllHikes().collect { allHikes ->
                val greatWalks = allHikes.filter { it.duration.contains("days", ignoreCase = true) }
                val dayHikes = allHikes.filterNot { it.duration.contains("days", ignoreCase = true) }
                _uiState.value = HomeUiState(
                    dayHikes = dayHikes,
                    greatWalks = greatWalks
                )
            }
        }
    }

    fun toggleFavorite(hike: Hike) {
        viewModelScope.launch {
            val updatedHike = hike.copy(isFavorite = !hike.isFavorite)
            hikeRepository.updateHike(updatedHike)

            // Manually update the UI state to reflect the change immediately
            val currentDayHikes = _uiState.value.dayHikes.toMutableList()
            val dayHikeIndex = currentDayHikes.indexOfFirst { it.id == hike.id }
            if (dayHikeIndex != -1) {
                currentDayHikes[dayHikeIndex] = updatedHike
            }

            val currentGreatWalks = _uiState.value.greatWalks.toMutableList()
            val greatWalkIndex = currentGreatWalks.indexOfFirst { it.id == hike.id }
            if (greatWalkIndex != -1) {
                currentGreatWalks[greatWalkIndex] = updatedHike
            }

            _uiState.value = _uiState.value.copy(
                dayHikes = currentDayHikes,
                greatWalks = currentGreatWalks
            )
        }
    }

    private suspend fun prepopulateDatabaseIfNeeded() {
        val hikes = hikeRepository.getAllHikes().first()
        if (hikes.isEmpty()) {
            hikeRepository.insertAll(initialHikes)
        }
    }
}

data class HomeUiState(
    val dayHikes: List<Hike> = emptyList(),
    val greatWalks: List<Hike> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

private val initialHikes = listOf(
    Hike(
        name = "Hooker Valley Track",
        location = "Aoraki/Mt Cook",
        distanceKm = 10.0,
        duration = "3h",
        difficulty = "Easy",
        imageRes = com.hao.ui.R.drawable.alexknob
    ),
    Hike(
        name = "Lake Marian",
        location = "Fiordland",
        distanceKm = 8.2,
        duration = "3h",
        difficulty = "Hard",
        imageRes = com.hao.ui.R.drawable.tewharatrack
    ),
    Hike(
        name = "Taranaki Falls",
        location = "Tongariro",
        distanceKm = 6.0,
        duration = "2h",
        difficulty = "Easy",
        imageRes = com.hao.ui.R.drawable.tongariroalpinecrossing
    ),
    Hike(
        name = "Rob Roy Glacier",
        location = "Mt Aspiring",
        distanceKm = 10.0,
        duration = "4h",
        difficulty = "Moderate",
        imageRes = com.hao.ui.R.drawable.royspeak
    ),
    Hike(
        name = "Milford Track",
        location = "Fiordland",
        distanceKm = 53.5,
        duration = "4 days",
        difficulty = "Intermediate",
        imageRes = com.hao.ui.R.drawable.milford
    ),
    Hike(
        name = "Routeburn Track",
        location = "Fiordland/Mt Aspiring",
        distanceKm = 32.0,
        duration = "2–4 days",
        difficulty = "Intermediate",
        imageRes = com.hao.ui.R.drawable.routeburn
    ),
    Hike(
        name = "Kepler Track",
        location = "Fiordland",
        distanceKm = 60.0,
        duration = "3–4 days",
        difficulty = "Intermediate",
        imageRes = com.hao.ui.R.drawable.kepler
    ),
    Hike(
        name = "Rakiura Track",
        location = "Stewart Island",
        distanceKm = 32.0,
        duration = "3 days",
        difficulty = "Intermediate",
        imageRes = com.hao.ui.R.drawable.rakiura
    ),
    Hike(
        name = "Heaphy Track",
        location = "Kahurangi",
        distanceKm = 78.4,
        duration = "4–6 days",
        difficulty = "Intermediate",
        imageRes = com.hao.ui.R.drawable.heaphy
    ),
    Hike(
        name = "Tongariro Northern Circuit",
        location = "Central Plateau",
        distanceKm = 43.1,
        duration = "3–4 days",
        difficulty = "Advanced",
        imageRes = com.hao.ui.R.drawable.tongariro
    ),
    Hike(
        name = "Abel Tasman Coast Track",
        location = "Abel Tasman",
        distanceKm = 60.0,
        duration = "3–5 days",
        difficulty = "Easy",
        imageRes = com.hao.ui.R.drawable.abel_tasman
    ),
    Hike(
        name = "Lake Waikaremoana Track",
        location = "Te Urewera",
        distanceKm = 46.0,
        duration = "3–4 days",
        difficulty = "Intermediate",
        imageRes = com.hao.ui.R.drawable.waikaremoana
    ),
    Hike(
        name = "Whanganui Journey",
        location = "Whanganui River",
        distanceKm = 145.0,
        duration = "3–5 days",
        difficulty = "Easy",
        imageRes = com.hao.ui.R.drawable.whanganui
    ),
    Hike(
        name = "Paparoa Track",
        location = "West Coast",
        distanceKm = 55.0,
        duration = "2–3 days",
        difficulty = "Intermediate",
        imageRes = com.hao.ui.R.drawable.paparoa
    ),
    Hike(
        name = "Hump Ridge Track",
        location = "Fiordland National Park, South Island",
        distanceKm = 61.0,
        duration = "3 days",
        difficulty = "Intermediate–Advanced",
        imageRes = com.hao.ui.R.drawable.humpridge
    )
)
