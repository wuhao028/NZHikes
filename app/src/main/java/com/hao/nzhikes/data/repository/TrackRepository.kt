package com.hao.nzhikes.data.repository

import android.content.Context
import android.util.Log
import com.hao.nzhikes.data.local.TrackDatabase
import com.hao.nzhikes.data.model.Track
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Repository for handling track data operations
 */
class TrackRepository private constructor(private val database: TrackDatabase) {
    private val trackDao = database.trackDao()
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val trackListType = Types.newParameterizedType(List::class.java, Track::class.java)
    private val trackListAdapter: JsonAdapter<List<Track>> = moshi.adapter(trackListType)

    suspend fun loadTracksFromAssets(context: Context): Boolean {
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

    fun getTracks(limit: Int = 20): Flow<List<Track>> {
        return trackDao.getTracks(limit)
    }

    companion object {
        private const val TAG = "TrackRepository"

        @Volatile
        private var INSTANCE: TrackRepository? = null

        fun getInstance(database: TrackDatabase): TrackRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = TrackRepository(database)
                INSTANCE = instance
                instance
            }
        }
    }
}
