package com.hao.data.repository

import android.content.Context
import com.hao.data.local.HutDao
import com.hao.data.model.Hut
import com.hao.data.remote.ApiService
import com.hao.data.remote.HutDetailsResponse
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
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class HutRepositoryTest {

    private lateinit var hutRepository: HutRepository
    private lateinit var mockHutDao: HutDao
    private lateinit var mockApiService: ApiService
    private lateinit var mockContext: Context
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockHutDao = mockk<HutDao>(relaxed = true)
        mockApiService = mockk<ApiService>(relaxed = true)
        mockContext = mockk<Context>(relaxed = true)
        
        mockkStatic(android.util.Log::class)
        every { android.util.Log.d(any(), any()) } returns 0
        every { android.util.Log.e(any(), any(), any()) } returns 0
        every { android.util.Log.e(any(), any()) } returns 0
        
        hutRepository = HutRepository(mockHutDao, mockApiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(android.util.Log::class)
    }

    @Test
    fun `getAllHuts should return flow from dao`() = runTest {
        // Given
        val testHuts = listOf(
            Hut(assetId = "1", name = "Hut 1", region = "Region 1", y = -41.0, x = 174.0),
            Hut(assetId = "2", name = "Hut 2", region = "Region 2", y = -40.0, x = 175.0)
        )
        coEvery { mockHutDao.getAllHuts() } returns flowOf(testHuts)

        // When
        val result = hutRepository.getAllHuts()
        val collected = mutableListOf<List<Hut>>()
        val job = launch {
            result.collect { collected.add(it) }
        }
        advanceUntilIdle()
        job.cancel()

        // Then
        assertTrue(collected.isNotEmpty())
        assertEquals(testHuts, collected.last())
        coVerify { mockHutDao.getAllHuts() }
    }

    @Test
    fun `searchHuts should return flow from dao`() = runTest {
        // Given
        val testHuts = listOf(
            Hut(assetId = "1", name = "Mountain Hut", region = "Region 1", y = -41.0, x = 174.0)
        )
        coEvery { mockHutDao.searchHuts(any()) } returns flowOf(testHuts)

        // When
        val result = hutRepository.searchHuts("Mountain")
        val collected = mutableListOf<List<Hut>>()
        val job = launch {
            result.collect { collected.add(it) }
        }
        advanceUntilIdle()
        job.cancel()

        // Then
        assertTrue(collected.isNotEmpty())
        assertEquals(testHuts, collected.last())
        coVerify { mockHutDao.searchHuts("%Mountain%") }
    }

    @Test
    fun `getHutDetails should return success result`() = runTest {
        // Given
        val testDetails = HutDetailsResponse(
            assetId = "1",
            name = "Test Hut",
            locationString = "Test Location",
            numberOfBunks = 20,
            facilities = emptyList(),
            hutCategory = "Standard",
            proximityToRoadEnd = "Close",
            bookable = true,
            introduction = "Test Introduction",
            introductionThumbnail = null,
            staticLink = "https://test.com",
            region = "Test Region",
            place = "Test Place",
            status = "Open",
            x = 174.0,
            y = -41.0
        )
        coEvery { mockApiService.getHutDetails("1") } returns testDetails

        // When
        val result = hutRepository.getHutDetails("1")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(testDetails, result.getOrNull())
        coVerify { mockApiService.getHutDetails("1") }
    }

    @Test
    fun `getHutDetails should return failure result on exception`() = runTest {
        // Given
        val exception = Exception("Network error")
        coEvery { mockApiService.getHutDetails("1") } throws exception

        // When
        val result = hutRepository.getHutDetails("1")

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `loadHutsFromAssets should return true when data already loaded`() = runTest {
        // Given
        coEvery { mockHutDao.getCount() } returns 10
        val mockAssets = mockk<android.content.res.AssetManager>(relaxed = true)
        every { mockContext.assets } returns mockAssets

        // When
        val result = hutRepository.loadHutsFromAssets(mockContext)

        // Then
        assertTrue(result)
        coVerify { mockHutDao.getCount() }
        coVerify(exactly = 0) { mockHutDao.insertAll(any()) }
    }

    @Test
    fun `loadHutsFromAssets should return false on IOException`() = runTest {
        // Given
        coEvery { mockHutDao.getCount() } returns 0
        val mockAssets = mockk<android.content.res.AssetManager>(relaxed = true)
        every { mockContext.assets } returns mockAssets
        every { mockAssets.open("allHuts.json") } throws IOException("File not found")

        // When
        val result = hutRepository.loadHutsFromAssets(mockContext)

        // Then
        assertFalse(result)
        coVerify { mockHutDao.getCount() }
        coVerify(exactly = 0) { mockHutDao.insertAll(any()) }
    }
}

