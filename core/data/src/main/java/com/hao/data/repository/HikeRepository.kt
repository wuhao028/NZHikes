package com.hao.data.repository

import com.hao.data.local.HikeDao
import com.hao.data.model.LocalTrack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HikeRepository @Inject constructor(
    private val hikeDao: HikeDao
) {

    fun getAllHikes(): Flow<List<LocalTrack>> = hikeDao.getAllHikes()
        .catch { exception ->
            throw HikeRepositoryException("Failed to get all hikes", exception)
        }

    fun getFavoriteHikes(): Flow<List<LocalTrack>> = hikeDao.getFavoriteHikes()
        .catch { exception ->
            throw HikeRepositoryException("Failed to get favorite hikes", exception)
        }

    suspend fun getHikeByAssetId(assetId: String): LocalTrack? {
        return try {
            hikeDao.getHikeByAssetId(assetId)
        } catch (exception: Exception) {
            throw HikeRepositoryException("Failed to get hike by asset ID: $assetId", exception)
        }
    }

    fun getHikeById(hikeId: Int): Flow<LocalTrack?> = hikeDao.getHikeById(hikeId)
        .catch { exception ->
            throw HikeRepositoryException("Failed to get hike by ID: $hikeId", exception)
        }

    fun getDoneHikes(): Flow<List<LocalTrack>> = hikeDao.getDoneHikes()
        .catch { exception ->
            throw HikeRepositoryException("Failed to get done hikes", exception)
        }

    suspend fun updateHike(hike: LocalTrack) {
        try {
            if (!hike.validate()) {
                throw IllegalArgumentException("Invalid hike data: ${hike.name}")
            }
            hikeDao.updateHike(hike)
        } catch (exception: Exception) {
            throw HikeRepositoryException("Failed to update hike: ${hike.name}", exception)
        }
    }

    suspend fun insertAll(hikes: List<LocalTrack>) {
        try {
            val invalidHikes = hikes.filterNot { it.validate() }
            if (invalidHikes.isNotEmpty()) {
                throw IllegalArgumentException("Invalid hikes found: ${invalidHikes.map { it.name }}")
            }
            hikeDao.insertAll(hikes)
        } catch (exception: Exception) {
            throw HikeRepositoryException("Failed to insert hikes", exception)
        }
    }

    suspend fun toggleFavorite(assetId: String, isFavorite: Boolean) {
        try {
            val hike = getHikeByAssetId(assetId)
            hike?.let {
                val updatedHike = it.copyWithFavorite(isFavorite)
                updateHike(updatedHike)
            }
        } catch (exception: Exception) {
            throw HikeRepositoryException("Failed to toggle favorite for hike: $assetId", exception)
        }
    }

    suspend fun markAsDone(assetId: String, isDone: Boolean) {
        try {
            val hike = getHikeByAssetId(assetId)
            hike?.let {
                val updatedHike = it.copyWithDone(isDone)
                updateHike(updatedHike)
            }
        } catch (exception: Exception) {
            throw HikeRepositoryException("Failed to mark hike as done: $assetId", exception)
        }
    }

    fun searchHikes(query: String): Flow<List<LocalTrack>> {
        return getAllHikes().map { hikes ->
            if (query.isBlank()) {
                hikes
            } else {
                hikes.filter { hike ->
                    hike.name.contains(query, ignoreCase = true) ||
                            hike.location.contains(query, ignoreCase = true) ||
                            hike.difficulty.contains(query, ignoreCase = true)
                }
            }
        }
    }

    fun getHikeStats(): Flow<HikeStats> {
        return getAllHikes().map { hikes ->
            HikeStats(
                totalHikes = hikes.size,
                favoriteHikes = hikes.count { it.isFavorite },
                doneHikes = hikes.count { it.isDone },
                totalDistance = hikes.sumOf { it.distanceKm }
            )
        }
    }
}

data class HikeStats(
    val totalHikes: Int,
    val favoriteHikes: Int,
    val doneHikes: Int,
    val totalDistance: Double
)

class HikeRepositoryException(message: String, cause: Throwable? = null) : Exception(message, cause)
