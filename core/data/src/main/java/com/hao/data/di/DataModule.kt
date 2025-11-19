package com.hao.data.di

import android.content.Context
import com.hao.data.local.AppDatabase
import com.hao.data.local.CampsiteDao
import com.hao.data.local.HikeDao
import com.hao.data.local.HutDao
import com.hao.data.local.TrackDao
import com.hao.data.remote.ApiService
import com.hao.data.repository.CampsiteRepository
import com.hao.data.repository.HikeRepository
import com.hao.data.repository.HutRepository
import com.hao.data.repository.TrackRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideHikeDao(appDatabase: AppDatabase): HikeDao {
        return appDatabase.hikeDao()
    }

    @Provides
    fun provideCampsiteDao(appDatabase: AppDatabase): CampsiteDao {
        return appDatabase.campsiteDao()
    }

    @Provides
    fun provideHutDao(appDatabase: AppDatabase): HutDao {
        return appDatabase.hutDao()
    }

    @Provides
    fun provideTrackDao(appDatabase: AppDatabase): TrackDao {
        return appDatabase.trackDao()
    }

    @Provides
    @Singleton
    fun provideHikeRepository(hikeDao: HikeDao): HikeRepository {
        return HikeRepository(hikeDao)
    }
}
