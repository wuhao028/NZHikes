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
            hikeRepository.getAllHikes().collect {
                _uiState.value = HomeUiState(
                    dayHikes = it,
                    greatWalks = it.filter { it.difficulty.contains("days") })
            }
        }
    }

    fun toggleFavorite(hike: Hike) {
        viewModelScope.launch {
            val updatedHike = hike.copy(isFavorite = !hike.isFavorite)
            hikeRepository.updateHike(updatedHike)
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
        imageRes = R.drawable.alexknob
    ),
    Hike(
        name = "Lake Marian",
        location = "Fiordland",
        distanceKm = 8.2,
        duration = "3h",
        difficulty = "Hard",
        imageRes = R.drawable.tewharatrack
    ),
    Hike(
        name = "Taranaki Falls",
        location = "Tongariro",
        distanceKm = 6.0,
        duration = "2h",
        difficulty = "Easy",
        imageRes = R.drawable.tongariroalpinecrossing
    ),
    Hike(
        name = "Rob Roy Glacier",
        location = "Mt Aspiring",
        distanceKm = 10.0,
        duration = "4h",
        difficulty = "Moderate",
        imageRes = R.drawable.royspeak
    ),
    Hike(
        name = "Milford Track",
        location = "Fiordland",
        distanceKm = 53.5,
        duration = "4 days",
        difficulty = "Intermediate",
        imageRes = R.drawable.milford
    ),
    Hike(
        name = "Routeburn Track",
        location = "Fiordland/Mt Aspiring",
        distanceKm = 32.0,
        duration = "2–4 days",
        difficulty = "Intermediate",
        imageRes = R.drawable.routeburn
    ),
    Hike(
        name = "Kepler Track",
        location = "Fiordland",
        distanceKm = 60.0,
        duration = "3–4 days",
        difficulty = "Intermediate",
        imageRes = R.drawable.kepler
    ),
    Hike(
        name = "Rakiura Track",
        location = "Stewart Island",
        distanceKm = 32.0,
        duration = "3 days",
        difficulty = "Intermediate",
        imageRes = R.drawable.rakiura
    ),
    Hike(
        name = "Heaphy Track",
        location = "Kahurangi",
        distanceKm = 78.4,
        duration = "4–6 days",
        difficulty = "Intermediate",
        imageRes = R.drawable.heaphy
    ),
    Hike(
        name = "Tongariro Northern Circuit",
        location = "Central Plateau",
        distanceKm = 43.1,
        duration = "3–4 days",
        difficulty = "Advanced",
        imageRes = R.drawable.tongariro
    ),
    Hike(
        name = "Abel Tasman Coast Track",
        location = "Abel Tasman",
        distanceKm = 60.0,
        duration = "3–5 days",
        difficulty = "Easy",
        imageRes = R.drawable.abel_tasman
    ),
    Hike(
        name = "Lake Waikaremoana Track",
        location = "Te Urewera",
        distanceKm = 46.0,
        duration = "3–4 days",
        difficulty = "Intermediate",
        imageRes = R.drawable.waikaremoana
    ),
    Hike(
        name = "Whanganui Journey",
        location = "Whanganui River",
        distanceKm = 145.0,
        duration = "3–5 days",
        difficulty = "Easy",
        imageRes = R.drawable.whanganui
    ),
    Hike(
        name = "Paparoa Track",
        location = "West Coast",
        distanceKm = 55.0,
        duration = "2–3 days",
        difficulty = "Intermediate",
        imageRes = R.drawable.paparoa
    ),
    Hike(
        name = "Hump Ridge Track",
        location = "Fiordland National Park, South Island",
        distanceKm = 61.0,
        duration = "3 days",
        difficulty = "Intermediate–Advanced",
        imageRes = R.drawable.humpridge
    )
)
