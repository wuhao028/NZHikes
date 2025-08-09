package com.hao.data.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrackDetailsResponse(
    val assetId: String?,
    val name: String?,
    // Some endpoints may return description/introduction; keep both optional for compatibility
    val description: String?,
    val introduction: String?,
    // Single hero image or a list of images; keep both optional
    val imageUrl: String?,
    val images: List<String>?
)
