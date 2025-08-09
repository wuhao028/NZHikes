package com.hao.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hao.data.model.Hike
import kotlinx.coroutines.flow.Flow

@Dao
interface HikeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(hikes: List<Hike>)

    @Update
    suspend fun updateHike(hike: Hike)

    @Query("SELECT * FROM hikes")
    fun getAllHikes(): Flow<List<Hike>>

    @Query("SELECT * FROM hikes WHERE isFavorite = 1")
    fun getFavoriteHikes(): Flow<List<Hike>>

    @Query("SELECT * FROM hikes WHERE id = :hikeId")
    fun getHikeById(hikeId: Int): Flow<Hike?>
}
