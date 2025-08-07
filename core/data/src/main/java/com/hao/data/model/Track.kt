package com.hao.data.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.hao.data.data.converter.CoordinateListConverter
import com.hao.data.data.converter.StringListConverter

/**
 * Data class representing a hiking track
 */
@Entity(tableName = "tracks")
data class Track(
    @PrimaryKey
    val assetId: String,
    val name: String,

    @field:TypeConverters(StringListConverter::class)
    val region: List<String>,

    val x: Double,  // Longitude
    val y: Double,  // Latitude

    @field:TypeConverters(CoordinateListConverter::class)
    val line: List<List<List<Double>>>  // List of coordinate points
)
