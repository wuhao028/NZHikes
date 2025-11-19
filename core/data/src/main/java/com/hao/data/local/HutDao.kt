package com.hao.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hao.data.model.Hut
import kotlinx.coroutines.flow.Flow

@Dao
interface HutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(huts: List<Hut>)

    @Query("SELECT * FROM huts ORDER BY name ASC")
    fun getAllHuts(): Flow<List<Hut>>

    @Query("SELECT COUNT(*) FROM huts")
    suspend fun getCount(): Int

    @Query("SELECT * FROM huts WHERE name LIKE :query ORDER BY name ASC LIMIT 50")
    fun searchHuts(query: String): Flow<List<Hut>>
}
