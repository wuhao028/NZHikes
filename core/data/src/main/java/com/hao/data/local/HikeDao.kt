package com.hao.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hao.data.model.LocalTrack
import kotlinx.coroutines.flow.Flow

@Dao
interface HikeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(hikes: List<LocalTrack>)

    @Update
    suspend fun updateHike(hike: LocalTrack)

    @Query("SELECT * FROM hikes")
    fun getAllHikes(): Flow<List<LocalTrack>>

    @Query("SELECT * FROM hikes WHERE isFavorite = 1")
    fun getFavoriteHikes(): Flow<List<LocalTrack>>

    @Query("SELECT * FROM hikes WHERE id = :hikeId")
    fun getHikeById(hikeId: Int): Flow<LocalTrack?>

    @Query("SELECT * FROM hikes WHERE assetId = :assetId")
    suspend fun getHikeByAssetId(assetId: String): LocalTrack?

    @Query("SELECT * FROM hikes WHERE isDone = 1")
    fun getDoneHikes(): Flow<List<LocalTrack>>
}
