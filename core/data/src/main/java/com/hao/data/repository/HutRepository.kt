package com.hao.data.repository

import android.content.Context
import android.util.Log
import com.hao.data.local.HutDao
import com.hao.data.model.Hut
import com.hao.data.remote.ApiService
import com.hao.data.remote.HutDetailsResponse
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HutRepository @Inject constructor(
    private val hutDao: HutDao,
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val hutListType = Types.newParameterizedType(List::class.java, Hut::class.java)
    private val hutListAdapter: JsonAdapter<List<Hut>> = moshi.adapter(hutListType)

    suspend fun loadHutsFromAssets(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (hutDao.getCount() > 0) {
                    Log.d(TAG, "Huts already loaded in database")
                    return@withContext true
                }

                val jsonString = context.assets.open("allHuts.json")
                    .bufferedReader()
                    .use { it.readText() }

                val huts = hutListAdapter.fromJson(jsonString) ?: emptyList()
                hutDao.insertAll(huts)

                Log.d(TAG, "Loaded ${huts.size} huts")
                true
            } catch (e: IOException) {
                Log.e(TAG, "Error loading huts from assets", e)
                false
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error loading huts", e)
                false
            }
        }
    }

    fun searchHuts(query: String): Flow<List<Hut>> {
        return hutDao.searchHuts("%$query%")
    }

    fun getAllHuts(): Flow<List<Hut>> {
        return hutDao.getAllHuts()
    }

    suspend fun getHutDetails(assetId: String): Result<HutDetailsResponse> {
        return try {
            val response = apiService.getHutDetails(assetId)
            Result.success(response)
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error getting hut details", e)
            Result.failure(e)
        } catch (e: IOException) {
            Log.e(TAG, "Network error getting hut details", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error getting hut details", e)
            Result.failure(e)
        }
    }

    companion object {
        private const val TAG = "HutRepository"
    }
}
