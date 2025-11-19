package com.hao.data.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hao.data.data.model.RemoteTrack
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the tracks table
 */
@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<RemoteTrack>)

    @Query("SELECT * FROM tracks LIMIT :limit")
    fun getTracks(limit: Int = 20): Flow<List<RemoteTrack>>

    @Query("SELECT COUNT(*) FROM tracks")
    suspend fun getTrackCount(): Int

    @Query("SELECT * FROM tracks WHERE name LIKE :query LIMIT 50")
    fun searchTracks(query: String): Flow<List<RemoteTrack>>
}
