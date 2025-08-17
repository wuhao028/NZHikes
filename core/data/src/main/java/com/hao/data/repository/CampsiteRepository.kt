package com.hao.data.repository

import android.content.Context
import android.util.Log
import com.hao.data.local.CampsiteDao
import com.hao.data.model.Campsite
import com.hao.data.remote.ApiService
import com.hao.data.remote.CampsiteDetailsResponse
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CampsiteRepository @Inject constructor(
    private val campsiteDao: CampsiteDao,
    private val apiService: ApiService
) {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val campsiteListType =
        Types.newParameterizedType(List::class.java, Campsite::class.java)
    private val campsiteListAdapter: JsonAdapter<List<Campsite>> = moshi.adapter(campsiteListType)

    suspend fun loadCampsitesFromAssets(context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Check if data is already loaded
                if (campsiteDao.getCount() > 0) {
                    Log.d(TAG, "Campsites already loaded in database")
                    return@withContext true
                }

                // Load JSON from assets
                val jsonString = context.assets.open("allCampsites.json")
                    .bufferedReader()
                    .use { it.readText() }

                // Parse JSON to list of campsites
                val campsites = campsiteListAdapter.fromJson(jsonString) ?: emptyList()

                // Insert campsites into database
                campsiteDao.insertAll(campsites)

                val loadTime = System.currentTimeMillis()
                Log.d(TAG, "Loaded ${campsites.size} campsites in ${loadTime}ms")

                true
            } catch (e: IOException) {
                Log.e(TAG, "Error loading campsites from assets", e)
                false
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error loading campsites", e)
                false
            }
        }
    }

    fun searchCampsites(query: String): Flow<List<Campsite>> {
        return campsiteDao.searchCampsites("%$query%")
    }

    fun getAllCampsites(): Flow<List<Campsite>> {
        return campsiteDao.getAllCampsites()
    }

    suspend fun getCampsiteDetails(assetId: String): Result<CampsiteDetailsResponse> {
        return try {
            val response = apiService.getCampsiteDetails(assetId)
            Result.success(response)
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error getting campsite details", e)
            Result.failure(e)
        } catch (e: IOException) {
            Log.e(TAG, "Network error getting campsite details", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error getting campsite details", e)
            Result.failure(e)
        }
    }

    companion object {
        private const val TAG = "CampsiteRepository"
    }
}
