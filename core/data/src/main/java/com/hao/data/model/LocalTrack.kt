package com.hao.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hikes")
data class LocalTrack(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val assetId: String,
    val name: String,
    val location: String,
    val distanceKm: Double,
    val duration: String,
    val difficulty: String,
    val imageRes: Int,
    var isFavorite: Boolean = false,
    var isDone: Boolean = false
) {

    fun validate(): Boolean {
        return assetId.isNotBlank() &&
                name.isNotBlank() &&
                location.isNotBlank() &&
                distanceKm >= 0 &&
                duration.isNotBlank() &&
                difficulty.isNotBlank()
    }

    fun copyWithFavorite(isFavorite: Boolean): LocalTrack {
        return copy(isFavorite = isFavorite)
    }

    fun copyWithDone(isDone: Boolean): LocalTrack {
        return copy(isDone = isDone)
    }

    fun getFormattedDistance(): String {
        return when {
            distanceKm < 1 -> "${(distanceKm * 1000).toInt()}m"
            distanceKm < 10 -> "%.1fkm".format(distanceKm)
            else -> "${distanceKm.toInt()}km"
        }
    }

    fun getDifficultyColor(): String {
        return when (difficulty.lowercase()) {
            "easy" -> "#4CAF50"
            "medium" -> "#FF9800"
            "hard" -> "#F44336"
            else -> "#9E9E9E"
        }
    }

    companion object {
        fun createDefault(
            assetId: String,
            name: String,
            location: String,
            distanceKm: Double,
            duration: String,
            difficulty: String,
            imageRes: Int
        ): LocalTrack {
            return LocalTrack(
                assetId = assetId,
                name = name,
                location = location,
                distanceKm = distanceKm.coerceAtLeast(0.0),
                duration = duration,
                difficulty = difficulty,
                imageRes = imageRes
            )
        }

        object Difficulty {
            const val EASY = "Easy"
            const val MEDIUM = "Medium"
            const val HARD = "Hard"
        }
    }
}
