package com.hao.explore

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hao.data.model.Campsite
import com.hao.data.remote.CampsiteDetailsResponse
import com.hao.data.repository.CampsiteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CampsiteDetailUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val campsite: Campsite? = null,
    val details: CampsiteDetails? = null
)

data class CampsiteDetails(
    val assetId: String,
    val name: String?,
    val locationString: String?,
    val introduction: String?,
    val introductionThumbnail: String?,
    val landscape: List<String>?,
    val category: String?,
    val access: List<String>?,
    val facilities: List<String>?,
    val activities: List<String>?,
    val dogsAllowed: String?,
    val poweredSites: Int?,
    val unpoweredSites: Int?,
    val isBookable: Boolean?,
    val staticLink: String?,
    val region: String?,
    val place: String?,
    val status: String?,
    val latitude: Double?,
    val longitude: Double?
)

@HiltViewModel
class CampsiteDetailViewModel @Inject constructor(
    private val campsiteRepository: CampsiteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(CampsiteDetailUiState(isLoading = true))
    val uiState: StateFlow<CampsiteDetailUiState> = _uiState.asStateFlow()

    private val assetId: String = checkNotNull(savedStateHandle["assetId"])

    init {
        loadCampsiteDetails()
    }

    fun loadCampsiteDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Get the basic campsite info from the local DB
                val localCampsite = campsiteRepository.getAllCampsites()
                    .firstOrNull()?.firstOrNull() { it.assetId == assetId }

                // Then fetch the detailed info from the API
                val result = campsiteRepository.getCampsiteDetails(assetId)
                result.onSuccess { response ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            // Use local data if available, otherwise create a basic Campsite from the response
                            campsite = (localCampsite ?: response.toLocalCampsite()) as Campsite?,
                            details = response.toCampsiteDetails()
                        )
                    }
                }.onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to load campsite details: ${exception.message}"
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

    private fun CampsiteDetailsResponse.toLocalCampsite(): Campsite {
        return Campsite(
            assetId = assetId,
            name = name,
            status = status ?: "",
            region = region ?: "",
            y = y ?: 0.0,
            x = x ?: 0.0
        )
    }

    private fun CampsiteDetailsResponse.toCampsiteDetails(): CampsiteDetails {
        return CampsiteDetails(
            assetId = assetId,
            name = name,
            locationString = locationString,
            introduction = introduction,
            introductionThumbnail = introductionThumbnail,
            landscape = landscape,
            category = category,
            access = access,
            facilities = facilities ?: emptyList(),
            activities = activities ?: emptyList(),
            dogsAllowed = dogsAllowed,
            poweredSites = poweredSites,
            unpoweredSites = unpoweredSites,
            isBookable = isBookable,
            staticLink = staticLink,
            region = region,
            place = place,
            status = status,
            latitude = y,
            longitude = x
        )
    }
}
