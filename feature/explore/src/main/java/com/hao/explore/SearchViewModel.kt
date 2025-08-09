package com.hao.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hao.data.data.model.Track
import com.hao.data.data.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val trackRepository: TrackRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResults = MutableStateFlow<List<Track>>(emptyList())
    val searchResults: StateFlow<List<Track>> = _searchResults

    init {
        viewModelScope.launch {
            searchQuery
                .debounce(300) // Add a debounce to avoid too many queries
                .flatMapLatest { query ->
                    if (query.isBlank()) {
                        MutableStateFlow(emptyList())
                    } else {
                        trackRepository.searchTracks(query)
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
