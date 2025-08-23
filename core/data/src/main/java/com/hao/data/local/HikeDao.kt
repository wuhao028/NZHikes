package com.hao.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction
import com.hao.data.model.LocalTrack
import kotlinx.coroutines.flow.Flow

@Dao
interface HikeDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(hikes: List<LocalTrack>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(hike: LocalTrack)
    
    @Update
    suspend fun updateHike(hike: LocalTrack)
    
    @Query("SELECT * FROM hikes ORDER BY name ASC")
    fun getAllHikes(): Flow<List<LocalTrack>>
    
    @Query("SELECT * FROM hikes WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteHikes(): Flow<List<LocalTrack>>
    
    @Query("SELECT * FROM hikes WHERE isDone = 1 ORDER BY name ASC")
    fun getDoneHikes(): Flow<List<LocalTrack>>
    
    @Query("SELECT * FROM hikes WHERE id = :hikeId")
    fun getHikeById(hikeId: Int): Flow<LocalTrack?>
    
    @Query("SELECT * FROM hikes WHERE assetId = :assetId")
    suspend fun getHikeByAssetId(assetId: String): LocalTrack?
    
    @Query("SELECT * FROM hikes WHERE name LIKE '%' || :name || '%' ORDER BY name ASC")
    fun searchHikesByName(name: String): Flow<List<LocalTrack>>
    
    @Query("SELECT * FROM hikes WHERE location LIKE '%' || :location || '%' ORDER BY name ASC")
    fun searchHikesByLocation(location: String): Flow<List<LocalTrack>>
    
    @Query("SELECT * FROM hikes WHERE difficulty = :difficulty ORDER BY name ASC")
    fun getHikesByDifficulty(difficulty: String): Flow<List<LocalTrack>>
    
    @Transaction
    @Query("SELECT COUNT(*) as totalHikes, " +
           "SUM(CASE WHEN isFavorite = 1 THEN 1 ELSE 0 END) as favoriteHikes, " +
           "SUM(CASE WHEN isDone = 1 THEN 1 ELSE 0 END) as doneHikes, " +
           "SUM(distanceKm) as totalDistance " +
           "FROM hikes")
    fun getHikeStats(): Flow<HikeStatsResult>
    
    @Query("DELETE FROM hikes WHERE id = :hikeId")
    suspend fun deleteHike(hikeId: Int)
    
    @Query("DELETE FROM hikes")
    suspend fun deleteAllHikes()
    
    @Query("UPDATE hikes SET isFavorite = :isFavorite WHERE assetId = :assetId")
    suspend fun updateFavoriteStatus(assetId: String, isFavorite: Boolean)
    
    @Query("UPDATE hikes SET isDone = :isDone WHERE assetId = :assetId")
    suspend fun updateDoneStatus(assetId: String, isDone: Boolean)
    
    @Query("SELECT * FROM hikes ORDER BY id DESC LIMIT :limit")
    fun getRecentHikes(limit: Int): Flow<List<LocalTrack>>
    
    @Query("SELECT * FROM hikes WHERE distanceKm BETWEEN :minDistance AND :maxDistance ORDER BY distanceKm ASC")
    fun getHikesByDistanceRange(minDistance: Double, maxDistance: Double): Flow<List<LocalTrack>>
}

data class HikeStatsResult(
    val totalHikes: Int,
    val favoriteHikes: Int,
    val doneHikes: Int,
    val totalDistance: Double
)
