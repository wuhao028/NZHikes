package com.hao.data.repository

import com.hao.data.local.HikeDao
import com.hao.data.model.LocalTrack
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HikeRepository @Inject constructor(
    private val hikeDao: HikeDao
) {
    fun getAllHikes(): Flow<List<LocalTrack>> = hikeDao.getAllHikes()

    fun getFavoriteHikes(): Flow<List<LocalTrack>> = hikeDao.getFavoriteHikes()

    suspend fun getHikeByAssetId(assetId: String): LocalTrack? = hikeDao.getHikeByAssetId(assetId)

    fun getDoneHikes(): Flow<List<LocalTrack>> = hikeDao.getDoneHikes()

    suspend fun updateHike(hike: LocalTrack) {
        hikeDao.updateHike(hike)
    }

    suspend fun insertAll(hikes: List<LocalTrack>) {
        hikeDao.insertAll(hikes)
    }
}
