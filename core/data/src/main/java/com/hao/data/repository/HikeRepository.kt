package com.hao.data.repository

import com.hao.data.local.HikeDao
import com.hao.data.model.Hike
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HikeRepository @Inject constructor(
    private val hikeDao: HikeDao
) {
    fun getAllHikes(): Flow<List<Hike>> = hikeDao.getAllHikes()

    fun getFavoriteHikes(): Flow<List<Hike>> = hikeDao.getFavoriteHikes()

    suspend fun updateHike(hike: Hike) {
        hikeDao.updateHike(hike)
    }

    suspend fun insertAll(hikes: List<Hike>) {
        hikeDao.insertAll(hikes)
    }
}
