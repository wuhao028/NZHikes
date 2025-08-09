package com.hao.data.remote

import com.squareup.moshi.Json

data class TrackDetailsResponse(
    val assetId: String,
    val name: String,
    val introduction: String,
    val x: Double,
    val y: Double,
    val distance: String,
    val walkDuration: String,
    val dogsAllowed: String,
    val publicTransport: String,
    @Json(name = "staticLink") val imageUrl: String?
)
