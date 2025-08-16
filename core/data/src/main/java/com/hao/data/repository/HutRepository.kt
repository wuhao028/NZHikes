package com.hao.data.repository

import android.content.Context
import android.util.Log
import com.hao.data.local.HutDao
import com.hao.data.model.Hut
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HutRepository @Inject constructor(
    private val hutDao: HutDao
) {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val hutListType = Types.newParameterizedType(List::class.java, Hut::class.java)
    private val hutListAdapter: JsonAdapter<List<Hut>> = moshi.adapter(hutListType)

    suspend fun loadHutsFromAssets(context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Check if data is already loaded
                if (hutDao.getCount() > 0) {
                    Log.d(TAG, "Huts already loaded in database")
                    return@withContext true
                }

                // Load JSON from assets
                val jsonString = context.assets.open("allHuts.json")
                    .bufferedReader()
                    .use { it.readText() }

                // Parse JSON to list of huts
                val huts = hutListAdapter.fromJson(jsonString) ?: emptyList()

                // Insert huts into database
                hutDao.insertAll(huts)

                val loadTime = System.currentTimeMillis()
                Log.d(TAG, "Loaded ${huts.size} huts in ${loadTime}ms")

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

    companion object {
        private const val TAG = "HutRepository"
    }
}
