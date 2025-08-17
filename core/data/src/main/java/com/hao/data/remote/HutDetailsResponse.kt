package com.hao.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HutDetailsResponse(
    @Json(name = "assetId") val assetId: String,
    @Json(name = "name") val name: String?,
    @Json(name = "locationString") val locationString: String?,
    @Json(name = "numberOfBunks") val numberOfBunks: Int?,
    @Json(name = "facilities") val facilities: List<String>?,
    @Json(name = "hutCategory") val hutCategory: String?,
    @Json(name = "proximityToRoadEnd") val proximityToRoadEnd: String?,
    @Json(name = "bookable") val bookable: Boolean?,
    @Json(name = "introduction") val introduction: String?,
    @Json(name = "introductionThumbnail") val introductionThumbnail: String?,
    @Json(name = "staticLink") val staticLink: String?,
    @Json(name = "region") val region: String?,
    @Json(name = "place") val place: String?,
    @Json(name = "status") val status: String?,
    @Json(name = "x") val x: Double?,
    @Json(name = "y") val y: Double?
)
