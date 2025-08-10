package com.hao.data.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrackDetailsResponse(
    val assetId: String?,
    val name: String?,
    val introduction: String?,
    val introductionThumbnail: String?, // Main image for the track
    val distance: String?,
    val walkDuration: String?,
    val walkTrackCategory: List<String>?,
    val locationString: String?,
    val region: List<String>?,
    val line: List<List<List<Double>>>? // Coordinates for the map path
)
