package com.hao.explore

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hao.data.data.model.RemoteTrack
import com.hao.data.data.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val trackRepository: TrackRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResults = MutableStateFlow<List<RemoteTrack>>(emptyList())
    val searchResults: StateFlow<List<RemoteTrack>> = _searchResults

    init {
        // Ensure local DB has data loaded from assets at least once
        viewModelScope.launch {
            runCatching { trackRepository.loadTracksFromAssets(appContext) }
        }

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
