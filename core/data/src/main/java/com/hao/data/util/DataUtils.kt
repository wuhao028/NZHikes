package com.hao.data.util

import com.hao.data.model.LocalTrack
import kotlin.math.roundToInt

object DataUtils {
    
    fun formatDistance(distanceKm: Double): String {
        return when {
            distanceKm < 1 -> "${(distanceKm * 1000).roundToInt()}m"
            distanceKm < 10 -> "%.1fkm".format(distanceKm)
            else -> "${distanceKm.roundToInt()}km"
        }
    }
    
    fun formatDuration(duration: String): String {
        return duration.trim()
    }
    
    fun getDifficultyColor(difficulty: String): String {
        return when (difficulty.lowercase()) {
            "easy" -> "#4CAF50"
            "medium" -> "#FF9800"
            "hard" -> "#F44336"
            else -> "#9E9E9E"
        }
    }
    
    fun getDifficultyChineseName(difficulty: String): String {
        return when (difficulty.lowercase()) {
            "easy" -> "Easy"
            "medium" -> "Medium"
            "hard" -> "Hard"
            else -> "Unknown"
        }
    }
    
    fun validateTrack(track: LocalTrack): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (track.assetId.isBlank()) {
            errors.add("Asset ID cannot be empty")
        }
        
        if (track.name.isBlank()) {
            errors.add("Name cannot be empty")
        }
        
        if (track.location.isBlank()) {
            errors.add("Location cannot be empty")
        }
        
        if (track.distanceKm < 0) {
            errors.add("Distance cannot be negative")
        }
        
        if (track.duration.isBlank()) {
            errors.add("Duration cannot be empty")
        }
        
        if (track.difficulty.isBlank()) {
            errors.add("Difficulty cannot be empty")
        }
        
        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }
    
    fun filterTracks(
        tracks: List<LocalTrack>,
        query: String = "",
        difficulty: String? = null,
        minDistance: Double? = null,
        maxDistance: Double? = null
    ): List<LocalTrack> {
        return tracks.filter { track ->
            val matchesQuery = query.isBlank() || 
                track.name.contains(query, ignoreCase = true) ||
                track.location.contains(query, ignoreCase = true)
            
            val matchesDifficulty = difficulty == null || 
                track.difficulty.equals(difficulty, ignoreCase = true)
            
            val matchesDistance = (minDistance == null || track.distanceKm >= minDistance) &&
                (maxDistance == null || track.distanceKm <= maxDistance)
            
            matchesQuery && matchesDifficulty && matchesDistance
        }
    }
    
    fun sortTracks(
        tracks: List<LocalTrack>,
        sortBy: SortOption,
        ascending: Boolean = true
    ): List<LocalTrack> {
        val sorted = when (sortBy) {
            SortOption.NAME -> tracks.sortedBy { it.name }
            SortOption.DISTANCE -> tracks.sortedBy { it.distanceKm }
            SortOption.DIFFICULTY -> tracks.sortedBy { it.difficulty }
            SortOption.LOCATION -> tracks.sortedBy { it.location }
        }
        
        return if (ascending) sorted else sorted.reversed()
    }
    
    fun calculateStats(tracks: List<LocalTrack>): TrackStats {
        return TrackStats(
            totalTracks = tracks.size,
            favoriteTracks = tracks.count { it.isFavorite },
            doneTracks = tracks.count { it.isDone },
            totalDistance = tracks.sumOf { it.distanceKm },
            averageDistance = if (tracks.isNotEmpty()) tracks.sumOf { it.distanceKm } / tracks.size else 0.0,
            difficultyDistribution = tracks.groupBy { it.difficulty }.mapValues { it.value.size }
        )
    }
}

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String>
)

enum class SortOption {
    NAME,
    DISTANCE,
    DIFFICULTY,
    LOCATION
}

data class TrackStats(
    val totalTracks: Int,
    val favoriteTracks: Int,
    val doneTracks: Int,
    val totalDistance: Double,
    val averageDistance: Double,
    val difficultyDistribution: Map<String, Int>
)
