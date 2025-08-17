package com.hao.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("v1/tracks/{assetId}/detail")
    suspend fun getTrackDetails(
        @Path("assetId") assetId: String
    ): TrackDetailsResponse

    @GET("v2/campsites/{assetId}/detail")
    suspend fun getCampsiteDetails(
        @Path("assetId") assetId: String
    ): CampsiteDetailsResponse

    @GET("v2/huts/{assetId}/detail")
    suspend fun getHutDetails(
        @Path("assetId") assetId: String
    ): HutDetailsResponse

}


