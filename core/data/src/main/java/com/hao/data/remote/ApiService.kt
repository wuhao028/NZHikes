package com.hao.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("v1/tracks/{assetId}/detail")
    suspend fun getTrackDetails(
        @Path("assetId") assetId: String
    ): TrackDetailsResponse

}


