package com.hao.data.repository

import android.content.Context
import android.util.Log
import com.hao.data.data.model.RemoteTrack
import com.hao.data.local.AppDatabase
import com.hao.data.remote.ApiService
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for handling track data operations.
 *
 * This repository acts as a single source of truth for track data, managing
 * data from both local database and remote API.
 *
 * @property database The local Room database.
 * @property apiService The remote API service.
 * @property context The application context, used for loading assets.
 */
@Singleton
class TrackRepository @Inject constructor(
    private val database: AppDatabase,
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) {
    private val trackDao = database.trackDao()
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val trackListType =
        Types.newParameterizedType(List::class.java, RemoteTrack::class.java)
    private val trackListAdapter: JsonAdapter<List<RemoteTrack>> = moshi.adapter(trackListType)


    suspend fun loadTracksFromAssets(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val startTime = System.currentTimeMillis()

                // Check if data is already loaded
                if (trackDao.getTrackCount() > 0) {
                    Log.d(TAG, "Tracks already loaded in database")
                    return@withContext true
                }

                // Load JSON from assets
                val jsonString = context.assets.open("allTracks.json")
                    .bufferedReader()
                    .use { it.readText() }

                // Parse JSON to list of tracks
                val tracks = trackListAdapter.fromJson(jsonString) ?: emptyList()

                // Insert tracks into database
                trackDao.insertTracks(tracks)

                val loadTime = System.currentTimeMillis() - startTime
                Log.d(TAG, "Loaded ${tracks.size} tracks in ${loadTime}ms")

                true
            } catch (e: IOException) {
                Log.e(TAG, "Error loading tracks from assets", e)
                false
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error loading tracks", e)
                false
            }
        }
    }

    fun getTracks(limit: Int = 20): Flow<List<RemoteTrack>> {
        return trackDao.getTracks(limit)
    }

    fun searchTracks(query: String): Flow<List<RemoteTrack>> {
        return trackDao.searchTracks("%${query}%")
    }

    suspend fun getTrackDetails(assetId: String): Result<com.hao.data.remote.TrackDetailsResponse> {
        return try {
            val response = apiService.getTrackDetails(assetId)
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching track details", e)
            Result.failure(e)
        }
    }

    companion object {
        private const val TAG = "TrackRepository"

        @Volatile
        private var INSTANCE: TrackRepository? = null

        fun getInstance(
            database: AppDatabase,
            apiService: ApiService,
            context: Context
        ): TrackRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = TrackRepository(database, apiService, context)
                INSTANCE = instance
                instance
            }
        }
    }
}
