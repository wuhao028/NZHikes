package com.hao.explore

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hao.data.model.LocalTrack
import com.hao.data.remote.TrackDetailsResponse
import com.hao.data.repository.HikeRepository
import com.hao.data.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackDetailViewModel @Inject constructor(
    private val repository: TrackRepository,
    private val hikeRepository: HikeRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    data class UiState(
        val loading: Boolean = true,
        val data: TrackDetailsResponse? = null,
        val hike: LocalTrack? = null,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    init {
        val assetId: String? = savedStateHandle["assetId"]
        if (assetId.isNullOrBlank()) {
            _uiState.value = UiState(loading = false, error = "Missing assetId")
        } else {
            fetchDetails(assetId)
        }
    }

    fun toggleFavorite(hike: LocalTrack) {
        viewModelScope.launch {
            val updatedHike = hike.copy(isFavorite = !hike.isFavorite)
            hikeRepository.updateHike(updatedHike)
            _uiState.value = _uiState.value.copy(hike = updatedHike)
        }
    }

    fun toggleDone(hike: LocalTrack) {
        viewModelScope.launch {
            val updatedHike = hike.copy(isDone = !hike.isDone)
            hikeRepository.updateHike(updatedHike)
            _uiState.value = _uiState.value.copy(hike = updatedHike)
        }
    }

    private fun fetchDetails(assetId: String) {
        _uiState.value = UiState(loading = true)
        viewModelScope.launch {
            val result = repository.getTrackDetails(assetId)
            _uiState.value = result.fold(
                onSuccess = { response ->
                    val hike = hikeRepository.getHikeByAssetId(assetId)
                    UiState(loading = false, data = response, hike = hike)
                },
                onFailure = { UiState(loading = false, error = it.message ?: "Unknown error") }
            )
        }
    }
}
