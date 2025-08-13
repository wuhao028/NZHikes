package com.hao.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hao.data.local.CampsiteDao
import com.hao.data.local.HutDao
import com.hao.data.model.Campsite
import com.hao.data.model.Hut
import com.hao.data.model.LocalTrack
import com.hao.data.repository.HikeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val hikeRepository: HikeRepository,
    private val campsiteDao: CampsiteDao,
    private val hutDao: HutDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _campsiteSearchQuery = MutableStateFlow("")
    val campsiteSearchQuery: StateFlow<String> = _campsiteSearchQuery.asStateFlow()

    private val _hutSearchQuery = MutableStateFlow("")
    val hutSearchQuery: StateFlow<String> = _hutSearchQuery.asStateFlow()

    init {
        viewModelScope.launch {
            prepopulateDatabaseIfNeeded()

            val hikesFlow = hikeRepository.getAllHikes()

            val campsitesFlow = _campsiteSearchQuery.flatMapLatest { query ->
                if (query.isBlank()) {
                    campsiteDao.getAllCampsites()
                } else {
                    campsiteDao.searchCampsites(query)
                }
            }

            val hutsFlow = _hutSearchQuery.flatMapLatest { query ->
                if (query.isBlank()) {
                    hutDao.getAllHuts()
                } else {
                    hutDao.searchHuts(query)
                }
            }

            combine(hikesFlow, campsitesFlow, hutsFlow) { hikes, campsites, huts ->
                val greatWalks = hikes.filter { it.duration.contains("days", ignoreCase = true) }
                val dayHikes = hikes.filterNot { it.duration.contains("days", ignoreCase = true) }
                _uiState.value = _uiState.value.copy(
                    dayHikes = dayHikes,
                    greatWalks = greatWalks,
                    campsites = campsites,
                    huts = huts
                )
            }.collect {}
        }
    }


    fun onCampsiteSearchQueryChanged(query: String) {
        _campsiteSearchQuery.value = query
    }

    fun onHutSearchQueryChanged(query: String) {
        _hutSearchQuery.value = query
    }

    fun toggleFavorite(hike: LocalTrack) {
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
    val dayHikes: List<LocalTrack> = emptyList(),
    val greatWalks: List<LocalTrack> = emptyList(),
    val campsites: List<Campsite> = emptyList(),
    val huts: List<Hut> = emptyList(),
    val campsiteSearchQuery: String = "",
    val hutSearchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

private val initialHikes = listOf(
    LocalTrack(
        assetId = "45055658-49a3-4560-80a5-86c2e67a7d41",
        name = "Hooker Valley Track",
        location = "Aoraki/Mt Cook",
        distanceKm = 10.0,
        duration = "3h",
        difficulty = "Easy",
        imageRes = com.hao.ui.R.drawable.alexknob
    ),
    LocalTrack(
        assetId = "517e49c7-bb4c-4fa2-943c-685bf47c3283",
        name = "Lake Marian",
        location = "Fiordland",
        distanceKm = 8.2,
        duration = "3h",
        difficulty = "Hard",
        imageRes = com.hao.ui.R.drawable.tewharatrack
    ),
    LocalTrack(
        assetId = "cf8245d1-3f6d-4867-9a0c-1cb702f66e77",
        name = "Taranaki Falls",
        location = "Tongariro",
        distanceKm = 6.0,
        duration = "2h",
        difficulty = "Easy",
        imageRes = com.hao.ui.R.drawable.tongariroalpinecrossing
    ),
    LocalTrack(
        assetId = "93ed7d16-981f-4cf4-b015-2c3532b2a42e",
        name = "Rob Roy Glacier",
        location = "Mt Aspiring",
        distanceKm = 10.0,
        duration = "4h",
        difficulty = "Moderate",
        imageRes = com.hao.ui.R.drawable.royspeak
    ),
    LocalTrack(
        assetId = "26f7e543-e3bf-48a3-a174-618bf790f6df",
        name = "Milford Track",
        location = "Fiordland",
        distanceKm = 53.5,
        duration = "4 days",
        difficulty = "Intermediate",
        imageRes = com.hao.ui.R.drawable.milford
    ),
    LocalTrack(
        assetId = "69bc21c3-4b8b-4fb2-ac98-2ac21d38cc25",
        name = "Routeburn Track",
        location = "Fiordland/Mt Aspiring",
        distanceKm = 32.0,
        duration = "2–4 days",
        difficulty = "Intermediate",
        imageRes = com.hao.ui.R.drawable.routeburn
    ),
    LocalTrack(
        assetId = "7b58932a-9f97-4092-b0fc-7a1c0777d28c",
        name = "Kepler Track",
        location = "Fiordland",
        distanceKm = 60.0,
        duration = "3–4 days",
        difficulty = "Intermediate",
        imageRes = com.hao.ui.R.drawable.kepler
    ),
    LocalTrack(
        assetId = "3df63c1e-5917-46f1-9516-dfdb75b58f42",
        name = "Rakiura Track",
        location = "Stewart Island",
        distanceKm = 32.0,
        duration = "3 days",
        difficulty = "Intermediate",
        imageRes = com.hao.ui.R.drawable.rakiura
    ),
    LocalTrack(
        assetId = "9943e482-ca33-4736-8b7a-a0199a2a4a0c",
        name = "Heaphy Track",
        location = "Kahurangi",
        distanceKm = 78.4,
        duration = "4–6 days",
        difficulty = "Intermediate",
        imageRes = com.hao.ui.R.drawable.heaphy
    ),
    LocalTrack(
        assetId = "a144cba2-2bea-411e-a4b8-023c828689d2",
        name = "Tongariro Northern Circuit",
        location = "Central Plateau",
        distanceKm = 43.1,
        duration = "3–4 days",
        difficulty = "Advanced",
        imageRes = com.hao.ui.R.drawable.tongariro
    ),
    LocalTrack(
        assetId = "114ff80d-12f4-4f0b-8384-103f0c8e6efc",
        name = "Abel Tasman Coast Track",
        location = "Abel Tasman",
        distanceKm = 60.0,
        duration = "3–5 days",
        difficulty = "Easy",
        imageRes = com.hao.ui.R.drawable.abel_tasman
    ),
    LocalTrack(
        assetId = "515b197a-9138-40e7-b099-03b2cd121941",
        name = "Lake Waikaremoana Track",
        location = "Te Urewera",
        distanceKm = 46.0,
        duration = "3–4 days",
        difficulty = "Intermediate",
        imageRes = com.hao.ui.R.drawable.waikaremoana
    ),
    LocalTrack(
        assetId = "ebcd19a9-125f-42fa-9c72-5ec8e19de8e3",
        name = "Whanganui Journey",
        location = "Whanganui River",
        distanceKm = 145.0,
        duration = "3–5 days",
        difficulty = "Easy",
        imageRes = com.hao.ui.R.drawable.whanganui
    ),
    LocalTrack(
        assetId = "c53005d1-c842-4e24-bade-48f61c977f31",
        name = "Paparoa Track",
        location = "West Coast",
        distanceKm = 55.0,
        duration = "2–3 days",
        difficulty = "Intermediate",
        imageRes = com.hao.ui.R.drawable.paparoa
    ),
    LocalTrack(
        assetId = "e089385c-a8c2-4cc7-bf40-cc94f52b4d48",
        name = "Hump Ridge Track",
        location = "Fiordland National Park, South Island",
        distanceKm = 61.0,
        duration = "3 days",
        difficulty = "Intermediate–Advanced",
        imageRes = com.hao.ui.R.drawable.humpridge
    )
)
