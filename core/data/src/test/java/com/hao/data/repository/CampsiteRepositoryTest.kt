package com.hao.data.repository

import android.content.Context
import com.hao.data.local.CampsiteDao
import com.hao.data.model.Campsite
import com.hao.data.remote.ApiService
import com.hao.data.remote.CampsiteDetailsResponse
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class CampsiteRepositoryTest {

    private lateinit var campsiteRepository: CampsiteRepository
    private lateinit var mockCampsiteDao: CampsiteDao
    private lateinit var mockApiService: ApiService
    private lateinit var mockContext: Context
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockCampsiteDao = mockk<CampsiteDao>(relaxed = true)
        mockApiService = mockk<ApiService>(relaxed = true)
        mockContext = mockk<Context>(relaxed = true)
        
        mockkStatic(android.util.Log::class)
        every { android.util.Log.d(any(), any()) } returns 0
        every { android.util.Log.e(any(), any(), any()) } returns 0
        every { android.util.Log.e(any(), any()) } returns 0
        
        campsiteRepository = CampsiteRepository(mockCampsiteDao, mockApiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(android.util.Log::class)
    }

    @Test
    fun `getAllCampsites should return flow from dao`() = runTest {
        // Given
        val testCampsites = listOf(
            Campsite(assetId = "1", name = "Campsite 1", region = "Region 1", y = -41.0, x = 174.0),
            Campsite(assetId = "2", name = "Campsite 2", region = "Region 2", y = -40.0, x = 175.0)
        )
        coEvery { mockCampsiteDao.getAllCampsites() } returns flowOf(testCampsites)

        // When
        val result = campsiteRepository.getAllCampsites()
        val collected = mutableListOf<List<Campsite>>()
        val job = launch {
            result.collect { collected.add(it) }
        }
        advanceUntilIdle()
        job.cancel()

        // Then
        assertTrue(collected.isNotEmpty())
        assertEquals(testCampsites, collected.last())
        coVerify { mockCampsiteDao.getAllCampsites() }
    }

    @Test
    fun `searchCampsites should return flow from dao`() = runTest {
        // Given
        val testCampsites = listOf(
            Campsite(assetId = "1", name = "Mountain Campsite", region = "Region 1", y = -41.0, x = 174.0)
        )
        coEvery { mockCampsiteDao.searchCampsites(any()) } returns flowOf(testCampsites)

        // When
        val result = campsiteRepository.searchCampsites("Mountain")
        val collected = mutableListOf<List<Campsite>>()
        val job = launch {
            result.collect { collected.add(it) }
        }
        advanceUntilIdle()
        job.cancel()

        // Then
        assertTrue(collected.isNotEmpty())
        assertEquals(testCampsites, collected.last())
        coVerify { mockCampsiteDao.searchCampsites("%Mountain%") }
    }

    @Test
    fun `getCampsiteDetails should return success result`() = runTest {
        // Given
        val testDetails = CampsiteDetailsResponse(
            assetId = "1",
            name = "Test Campsite",
            locationString = "Test Location",
            introduction = "Test Introduction",
            introductionThumbnail = null,
            landscape = emptyList(),
            category = "Standard",
            access = emptyList(),
            facilities = emptyList(),
            activities = emptyList(),
            dogsAllowed = "Yes",
            poweredSites = 10,
            unpoweredSites = 20,
            isBookable = true,
            staticLink = "https://test.com",
            region = "Test Region",
            place = "Test Place",
            status = "Open",
            y = -41.0,
            x = 174.0
        )
        coEvery { mockApiService.getCampsiteDetails("1") } returns testDetails

        // When
        val result = campsiteRepository.getCampsiteDetails("1")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(testDetails, result.getOrNull())
        coVerify { mockApiService.getCampsiteDetails("1") }
    }

    @Test
    fun `getCampsiteDetails should return failure result on HttpException`() = runTest {
        // Given
        val httpException = mockk<HttpException>(relaxed = true)
        coEvery { mockApiService.getCampsiteDetails("1") } throws httpException

        // When
        val result = campsiteRepository.getCampsiteDetails("1")

        // Then
        assertTrue(result.isFailure)
        assertEquals(httpException, result.exceptionOrNull())
    }

    @Test
    fun `getCampsiteDetails should return failure result on IOException`() = runTest {
        // Given
        val ioException = IOException("Network error")
        coEvery { mockApiService.getCampsiteDetails("1") } throws ioException

        // When
        val result = campsiteRepository.getCampsiteDetails("1")

        // Then
        assertTrue(result.isFailure)
        assertEquals(ioException, result.exceptionOrNull())
    }

    @Test
    fun `loadCampsitesFromAssets should return true when data already loaded`() = runTest {
        // Given
        coEvery { mockCampsiteDao.getCount() } returns 10
        val mockAssets = mockk<android.content.res.AssetManager>(relaxed = true)
        every { mockContext.assets } returns mockAssets

        // When
        val result = campsiteRepository.loadCampsitesFromAssets(mockContext)

        // Then
        assertTrue(result)
        coVerify { mockCampsiteDao.getCount() }
        coVerify(exactly = 0) { mockCampsiteDao.insertAll(any()) }
    }

    @Test
    fun `loadCampsitesFromAssets should return false on IOException`() = runTest {
        // Given
        coEvery { mockCampsiteDao.getCount() } returns 0
        val mockAssets = mockk<android.content.res.AssetManager>(relaxed = true)
        every { mockContext.assets } returns mockAssets
        every { mockAssets.open("allCampsites.json") } throws IOException("File not found")

        // When
        val result = campsiteRepository.loadCampsitesFromAssets(mockContext)

        // Then
        assertFalse(result)
        coVerify { mockCampsiteDao.getCount() }
        coVerify(exactly = 0) { mockCampsiteDao.insertAll(any()) }
    }
}

