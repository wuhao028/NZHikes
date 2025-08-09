package com.hao.data.di

import android.content.Context
import com.hao.data.data.local.AppDatabase
import com.hao.data.local.HikeDao
import com.hao.data.repository.HikeRepository
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
    @Singleton
    fun provideHikeRepository(hikeDao: HikeDao): HikeRepository {
        return HikeRepository(hikeDao)
    }
}
