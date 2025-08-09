package com.hao.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.SavedStateHandle
import com.hao.data.data.repository.TrackRepository
import com.hao.data.remote.TrackDetailsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class TrackDetailViewModel @Inject constructor(
    private val repository: TrackRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    data class UiState(
        val loading: Boolean = true,
        val data: TrackDetailsResponse? = null,
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

    private fun fetchDetails(assetId: String) {
        _uiState.value = UiState(loading = true)
        viewModelScope.launch {
            val result = repository.getTrackDetails(assetId)
            _uiState.value = result.fold(
                onSuccess = { UiState(loading = false, data = it) },
                onFailure = { UiState(loading = false, error = it.message ?: "Unknown error") }
            )
        }
    }
}
