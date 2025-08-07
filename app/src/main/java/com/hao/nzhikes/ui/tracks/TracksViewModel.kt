package com.hao.nzhikes.ui.tracks

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hao.nzhikes.data.local.TrackDatabase
import com.hao.nzhikes.data.model.Track
import com.hao.nzhikes.data.repository.TrackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * ViewModel for the tracks screen
 */
class TracksViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TrackRepository.getInstance(
        TrackDatabase.getDatabase(application)
    )

    private val _uiState = MutableStateFlow<TracksUiState>(TracksUiState.Loading)
    val uiState: StateFlow<TracksUiState> = _uiState

    init {
        loadTracks()
    }

    private fun loadTracks() {
        viewModelScope.launch {
            try {
                _uiState.value = TracksUiState.Loading
                
                // Load tracks from assets to database if needed
                val loadStartTime = System.currentTimeMillis()
                repository.loadTracksFromAssets(getApplication())
                
                // Observe tracks from database
                repository.getTracks().collectLatest { tracks ->
                    val loadTime = System.currentTimeMillis() - loadStartTime
                    Log.d(TAG, "Tracks loaded in ${loadTime}ms")
                    _uiState.value = TracksUiState.Success(tracks, loadTime)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading tracks", e)
                _uiState.value = TracksUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    companion object {
        private const val TAG = "TracksViewModel"
    }
}

/**
 * UI state for the tracks screen
 */
sealed class TracksUiState {
    object Loading : TracksUiState()
    data class Success(val tracks: List<Track>, val loadTime: Long) : TracksUiState()
    data class Error(val message: String) : TracksUiState()
}
