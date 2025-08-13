package com.hao.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hao.data.model.Campsite
import kotlinx.coroutines.flow.Flow

@Dao
interface CampsiteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(campsites: List<Campsite>)

    @Query("SELECT * FROM campsites ORDER BY name ASC")
    fun getAllCampsites(): Flow<List<Campsite>>

    @Query("SELECT * FROM campsites WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchCampsites(query: String): Flow<List<Campsite>>
}
