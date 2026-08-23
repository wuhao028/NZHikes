package com.hao.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hao.data.model.LocalTrack
import com.hao.data.repository.HikeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripsViewModel @Inject constructor(
    private val hikeRepository: HikeRepository
) : ViewModel() {

    val favoriteHikes: StateFlow<List<LocalTrack>> = hikeRepository.getFavoriteHikes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val doneHikes: StateFlow<List<LocalTrack>> = hikeRepository.getDoneHikes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun toggleFavorite(hike: LocalTrack) {
        viewModelScope.launch {
            val updatedHike = hike.copy(isFavorite = !hike.isFavorite)
            hikeRepository.updateHike(updatedHike)
        }
    }
}
