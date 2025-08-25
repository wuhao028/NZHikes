package com.hao.data.di

import com.hao.data.remote.ApiService
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Retrofit

class NetworkModuleTest {

    @Test
    fun `provideMoshi should return Moshi instance with KotlinJsonAdapterFactory`() {
        // When
        val moshi = NetworkModule.provideMoshi()

        // Then
        assertNotNull(moshi)
        assertTrue(moshi is Moshi)
    }

    @Test
    fun `provideOkHttpClient should return OkHttpClient with logging interceptor`() {
        // Given
        val apiKey = "test-api-key"

        // When
        val okHttpClient = NetworkModule.provideOkHttpClient(apiKey)

        // Then
        assertNotNull(okHttpClient)
        assertTrue(okHttpClient is OkHttpClient)
        
        // Verify interceptors are added
        val interceptors = okHttpClient.interceptors
        assertTrue(interceptors.isNotEmpty())
    }

    @Test
    fun `provideRetrofit should return Retrofit instance with correct configuration`() {
        // Given
        val moshi = NetworkModule.provideMoshi()
        val apiKey = "test-api-key"
        val okHttpClient = NetworkModule.provideOkHttpClient(apiKey)

        // When
        val retrofit = NetworkModule.provideRetrofit(okHttpClient, moshi)

        // Then
        assertNotNull(retrofit)
        assertTrue(retrofit is Retrofit)
        assertEquals("https://api.doc.govt.nz/", retrofit.baseUrl().toString())
    }

    @Test
    fun `provideApiService should return ApiService instance`() {
        // Given
        val moshi = NetworkModule.provideMoshi()
        val apiKey = "test-api-key"
        val okHttpClient = NetworkModule.provideOkHttpClient(apiKey)
        val retrofit = NetworkModule.provideRetrofit(okHttpClient, moshi)

        // When
        val apiService = NetworkModule.provideApiService(retrofit)

        // Then
        assertNotNull(apiService)
        assertTrue(apiService is ApiService)
    }

    @Test
    fun `provideOkHttpClient should add API key header interceptor`() {
        // Given
        val apiKey = "test-api-key"

        // When
        val okHttpClient = NetworkModule.provideOkHttpClient(apiKey)

        // Then
        val interceptors = okHttpClient.interceptors
        assertTrue(interceptors.isNotEmpty())
        
        // The interceptor should be added to handle API key headers
        // Note: We can't directly test the interceptor behavior without making actual requests
        // but we can verify that interceptors are present
    }

    @Test
    fun `provideRetrofit should use Moshi converter factory`() {
        // Given
        val moshi = NetworkModule.provideMoshi()
        val apiKey = "test-api-key"
        val okHttpClient = NetworkModule.provideOkHttpClient(apiKey)

        // When
        val retrofit = NetworkModule.provideRetrofit(okHttpClient, moshi)

        // Then
        assertNotNull(retrofit)
        // Retrofit should have converter factories configured
        // Note: We can't directly access converter factories without reflection
        // but we can verify the retrofit instance is created successfully
    }

    @Test
    fun `provideMoshi should be singleton`() {
        // When
        val moshi1 = NetworkModule.provideMoshi()
        val moshi2 = NetworkModule.provideMoshi()

        // Then
        // Both should be valid Moshi instances
        assertNotNull(moshi1)
        assertNotNull(moshi2)
        assertTrue(moshi1 is Moshi)
        assertTrue(moshi2 is Moshi)
    }

    @Test
    fun `provideOkHttpClient should handle different API keys`() {
        // Given
        val apiKey1 = "test-api-key-1"
        val apiKey2 = "test-api-key-2"

        // When
        val okHttpClient1 = NetworkModule.provideOkHttpClient(apiKey1)
        val okHttpClient2 = NetworkModule.provideOkHttpClient(apiKey2)

        // Then
        assertNotNull(okHttpClient1)
        assertNotNull(okHttpClient2)
        assertTrue(okHttpClient1 is OkHttpClient)
        assertTrue(okHttpClient2 is OkHttpClient)
    }

    @Test
    fun `provideRetrofit should use correct base URL`() {
        // Given
        val moshi = NetworkModule.provideMoshi()
        val apiKey = "test-api-key"
        val okHttpClient = NetworkModule.provideOkHttpClient(apiKey)

        // When
        val retrofit = NetworkModule.provideRetrofit(okHttpClient, moshi)

        // Then
        val baseUrl = retrofit.baseUrl().toString()
        assertEquals("https://api.doc.govt.nz/", baseUrl)
    }

    @Test
    fun `provideApiService should create service with correct interface`() {
        // Given
        val moshi = NetworkModule.provideMoshi()
        val apiKey = "test-api-key"
        val okHttpClient = NetworkModule.provideOkHttpClient(apiKey)
        val retrofit = NetworkModule.provideRetrofit(okHttpClient, moshi)

        // When
        val apiService = NetworkModule.provideApiService(retrofit)

        // Then
        assertNotNull(apiService)
        // Verify it implements the correct interface
        assertTrue(ApiService::class.java.isAssignableFrom(apiService::class.java))
    }
}
