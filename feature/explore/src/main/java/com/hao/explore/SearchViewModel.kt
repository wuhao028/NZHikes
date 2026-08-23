package com.hao.explore

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hao.data.repository.CampsiteRepository
import com.hao.data.repository.HutRepository
import com.hao.data.repository.TrackRepository
import com.hao.explore.model.SearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@HiltViewModel
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchViewModel @Inject constructor(
    private val trackRepository: TrackRepository,
    private val campsiteRepository: CampsiteRepository,
    private val hutRepository: HutRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults: StateFlow<List<SearchResult>> = _searchResults

    init {
        viewModelScope.launch {
            when (savedStateHandle.get<Int>("searchType") ?: 0) {
                0 -> trackRepository.loadTracksFromAssets()
                1 -> campsiteRepository.loadCampsitesFromAssets()
                2 -> hutRepository.loadHutsFromAssets()
            }
        }

        viewModelScope.launch {
            searchQuery
                .debounce(300)
                .map(String::trim)
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    if (query.isBlank()) {
                        emptyFlow()
                    } else {
                        val searchType = savedStateHandle.get<Int>("searchType") ?: 0
                        when (searchType) {
                            0 -> trackRepository.searchTracks(query)
                                .map { tracks ->
                                    tracks.map { SearchResult.TrackResult(it) }
                                }

                            1 -> campsiteRepository.searchCampsites(query)
                                .map { campsites ->
                                    campsites.map { SearchResult.CampsiteResult(it) }
                                }

                            2 -> hutRepository.searchHuts(query)
                                .map { huts ->
                                    huts.map { SearchResult.HutResult(it) }
                                }

                            else -> emptyFlow()
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
