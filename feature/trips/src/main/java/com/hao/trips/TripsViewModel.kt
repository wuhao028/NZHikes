package com.hao.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hao.data.model.Hike
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

    val favoriteHikes: StateFlow<List<Hike>> = hikeRepository.getFavoriteHikes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val doneHikes: StateFlow<List<Hike>> = hikeRepository.getDoneHikes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun toggleFavorite(hike: Hike) {
        viewModelScope.launch {
            val updatedHike = hike.copy(isFavorite = !hike.isFavorite)
            hikeRepository.updateHike(updatedHike)
        }
    }
}
