package com.hao.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hikes")
data class Hike(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val assetId: String,
    val name: String,
    val location: String,
    val distanceKm: Double,
    val duration: String,
    val difficulty: String,
    val imageRes: Int, // For local drawable resource
    var isFavorite: Boolean = false
)
