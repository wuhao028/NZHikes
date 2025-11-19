package com.hao.explore

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hao.data.data.repository.TrackRepository
import com.hao.data.repository.CampsiteRepository
import com.hao.data.repository.HutRepository
import com.hao.explore.model.SearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val trackRepository: TrackRepository,
    private val campsiteRepository: CampsiteRepository,
    private val hutRepository: HutRepository,
    private val savedStateHandle: SavedStateHandle,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults: StateFlow<List<SearchResult>> = _searchResults

    init {
        // Ensure local DB has data loaded from assets at least once
        viewModelScope.launch {
            // Load data in parallel
            launch { trackRepository.loadTracksFromAssets(appContext) }
            launch { campsiteRepository.loadCampsitesFromAssets(appContext) }
            launch { hutRepository.loadHutsFromAssets(appContext) }
        }

        // Start search flow
        viewModelScope.launch {
            searchQuery
                .debounce(300)
                .flatMapLatest { query ->
                    if (query.isBlank()) {
                        MutableStateFlow(emptyList())
                    } else {
                        val searchType = savedStateHandle.get<Int>("searchType") ?: 0
                        when (searchType) {
                            0 -> trackRepository.searchTracks(query)
                                .map { tracks ->
                                    tracks.map { SearchResult.TrackResult(it) }
                                }
                                .flowOn(Dispatchers.Default)

                            1 -> campsiteRepository.searchCampsites(query)
                                .map { campsites ->
                                    campsites.map { SearchResult.CampsiteResult(it) }
                                }
                                .flowOn(Dispatchers.Default)

                            2 -> hutRepository.searchHuts(query)
                                .map { huts ->
                                    huts.map { SearchResult.HutResult(it) }
                                }
                                .flowOn(Dispatchers.Default)

                            else -> MutableStateFlow(emptyList())
                        }
                    }
                }
                .collect { results ->
                    _searchResults.value = results
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}
