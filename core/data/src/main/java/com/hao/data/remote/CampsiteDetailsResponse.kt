package com.hao.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CampsiteDetailsResponse(
    @Json(name = "assetId") val assetId: String,
    @Json(name = "name") val name: String?,
    @Json(name = "locationString") val locationString: String?,
    @Json(name = "introduction") val introduction: String?,
    @Json(name = "introductionThumbnail") val introductionThumbnail: String?,
    @Json(name = "landscape") val landscape: List<String>?,
    @Json(name = "campsiteCategory") val category: String?,
    @Json(name = "access") val access: List<String>?,
    @Json(name = "facilities") val facilities: List<String>?,
    @Json(name = "activities") val activities: List<String>?,
    @Json(name = "dogsAllowed") val dogsAllowed: String?,
    @Json(name = "numberOfPoweredSites") val poweredSites: Int?,
    @Json(name = "numberOfUnpoweredSites") val unpoweredSites: Int?,
    @Json(name = "bookable") val isBookable: Boolean?,
    @Json(name = "staticLink") val staticLink: String?,
    @Json(name = "region") val region: String?,
    @Json(name = "place") val place: String?,
    @Json(name = "status") val status: String?,
    @Json(name = "y") val y: Double?,
    @Json(name = "x") val x: Double?
)
