package com.hao.nzhikes.di

import com.hao.nzhikes.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Named("doc_api_key")
    fun provideDocApiKey(): String {
        return BuildConfig.DOC_API_KEY
    }
}
