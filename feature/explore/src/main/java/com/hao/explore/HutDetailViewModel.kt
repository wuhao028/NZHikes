package com.hao.explore

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hao.data.model.Hut
import com.hao.data.remote.HutDetailsResponse
import com.hao.data.repository.HutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HutDetailUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val hut: Hut? = null,
    val details: HutDetails? = null
)

data class HutDetails(
    val assetId: String,
    val name: String?,
    val locationString: String?,
    val numberOfBunks: Int?,
    val facilities: List<String>?,
    val hutCategory: String?,
    val proximityToRoadEnd: String?,
    val bookable: Boolean?,
    val introduction: String?,
    val introductionThumbnail: String?,
    val staticLink: String?,
    val region: String?,
    val place: String?,
    val status: String?,
    val latitude: Double?,
    val longitude: Double?
)

@HiltViewModel
class HutDetailViewModel @Inject constructor(
    private val hutRepository: HutRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(HutDetailUiState(isLoading = true))
    val uiState: StateFlow<HutDetailUiState> = _uiState.asStateFlow()

    private val assetId: String = checkNotNull(savedStateHandle["assetId"])

    init {
        loadHutDetails()
    }

    fun loadHutDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Get the basic hut info from the local DB
                val localHut = hutRepository.getAllHuts()
                    .firstOrNull()?.firstOrNull { it.assetId == assetId }

                // Then fetch the detailed info from the API
                val result = hutRepository.getHutDetails(assetId)
                result.onSuccess { response ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            // Use local data if available, otherwise create a basic Hut from the response
                            hut = (localHut ?: response.toLocalHut()) as Hut?,
                            details = response.toHutDetails()
                        )
                    }
                }.onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to load hut details: ${exception.message}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "An error occurred: ${e.message}"
                    )
                }
            }
        }
    }

    private fun HutDetailsResponse.toLocalHut(): Hut {
        return Hut(
            assetId = assetId,
            name = name,
            status = status ?: "",
            region = region ?: "",
            y = y ?: 0.0,
            x = x ?: 0.0
        )
    }

    private fun HutDetailsResponse.toHutDetails(): HutDetails {
        return HutDetails(
            assetId = assetId,
            name = name,
            locationString = locationString,
            numberOfBunks = numberOfBunks,
            facilities = facilities ?: emptyList(),
            hutCategory = hutCategory,
            proximityToRoadEnd = proximityToRoadEnd,
            bookable = bookable,
            introduction = introduction,
            introductionThumbnail = introductionThumbnail,
            staticLink = staticLink,
            region = region,
            place = place,
            status = status,
            latitude = y,
            longitude = x
        )
    }
}
