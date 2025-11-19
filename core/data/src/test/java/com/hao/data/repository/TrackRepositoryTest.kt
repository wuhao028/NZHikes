package com.hao.data.data.repository

import android.content.Context
import com.hao.data.data.model.RemoteTrack
import com.hao.data.local.AppDatabase
import com.hao.data.local.TrackDao
import com.hao.data.remote.ApiService
import com.hao.data.remote.TrackDetailsResponse
import com.hao.data.repository.TrackRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class TrackRepositoryTest {

    private lateinit var trackRepository: TrackRepository
    private lateinit var mockDatabase: AppDatabase
    private lateinit var mockApiService: ApiService
    private lateinit var mockTrackDao: TrackDao
    private lateinit var mockContext: Context
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockDatabase = mockk<AppDatabase>(relaxed = true)
        mockApiService = mockk<ApiService>(relaxed = true)
        mockTrackDao = mockk<TrackDao>(relaxed = true)
        mockContext = mockk<Context>(relaxed = true)

        mockkStatic(android.util.Log::class)
        every { android.util.Log.d(any(), any()) } returns 0
        every { android.util.Log.e(any(), any(), any()) } returns 0
        every { android.util.Log.e(any(), any()) } returns 0

        every { mockDatabase.trackDao() } returns mockTrackDao

        // Reset singleton instance
        val companion = TrackRepository::class.java.getDeclaredField("INSTANCE")
        companion.isAccessible = true
        companion.set(null, null)

        trackRepository = TrackRepository.getInstance(mockDatabase, mockApiService, mockContext)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(android.util.Log::class)
        // Reset singleton instance
        val companion = TrackRepository::class.java.getDeclaredField("INSTANCE")
        companion.isAccessible = true
        companion.set(null, null)
    }

    @Test
    fun `getTracks should return flow from dao`() = runTest {
        // Given
        val testTracks = listOf(
            RemoteTrack(
                assetId = "1",
                name = "Track 1",
                region = listOf("Region 1"),
                x = 174.0,
                y = -41.0,
                line = listOf(listOf(listOf(174.0, -41.0)))
            )
        )
        coEvery { mockTrackDao.getTracks(20) } returns flowOf(testTracks)

        // When
        val result = trackRepository.getTracks(20)
        val collected = mutableListOf<List<RemoteTrack>>()
        val job = launch {
            result.collect { collected.add(it) }
        }
        advanceUntilIdle()
        job.cancel()

        // Then
        assertTrue(collected.isNotEmpty())
        assertEquals(testTracks, collected.last())
        coVerify { mockTrackDao.getTracks(20) }
    }

    @Test
    fun `searchTracks should return flow from dao`() = runTest {
        // Given
        val testTracks = listOf(
            RemoteTrack(
                assetId = "1",
                name = "Mountain Track",
                region = listOf("Region 1"),
                x = 174.0,
                y = -41.0,
                line = listOf(listOf(listOf(174.0, -41.0)))
            )
        )
        coEvery { mockTrackDao.searchTracks(any()) } returns flowOf(testTracks)

        // When
        val result = trackRepository.searchTracks("Mountain")
        val collected = mutableListOf<List<RemoteTrack>>()
        val job = launch {
            result.collect { collected.add(it) }
        }
        advanceUntilIdle()
        job.cancel()

        // Then
        assertTrue(collected.isNotEmpty())
        assertEquals(testTracks, collected.last())
        coVerify { mockTrackDao.searchTracks("%Mountain%") }
    }

    @Test
    fun `getTrackDetails should return success result`() = runTest {
        // Given
        val testDetails = TrackDetailsResponse(
            assetId = "1",
            name = "Test Track",
            introduction = "Test Introduction",
            introductionThumbnail = null,
            distance = "10km",
            walkDuration = "4h",
            walkTrackCategory = listOf("Standard"),
            locationString = "Test Location",
            region = listOf("Test Region"),
            line = listOf(listOf(listOf(174.0, -41.0)))
        )
        coEvery { mockApiService.getTrackDetails("1") } returns testDetails

        // When
        val result = trackRepository.getTrackDetails("1")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(testDetails, result.getOrNull())
        coVerify { mockApiService.getTrackDetails("1") }
    }

    @Test
    fun `getTrackDetails should return failure result on exception`() = runTest {
        // Given
        val exception = Exception("Network error")
        coEvery { mockApiService.getTrackDetails("1") } throws exception

        // When
        val result = trackRepository.getTrackDetails("1")

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `loadTracksFromAssets should return true when data already loaded`() = runTest {
        // Given
        coEvery { mockTrackDao.getTrackCount() } returns 10
        val mockAssets = mockk<android.content.res.AssetManager>(relaxed = true)
        every { mockContext.assets } returns mockAssets

        // When
        val result = trackRepository.loadTracksFromAssets()

        // Then
        assertTrue(result)
        coVerify { mockTrackDao.getTrackCount() }
        coVerify(exactly = 0) { mockTrackDao.insertTracks(any()) }
    }

    @Test
    fun `loadTracksFromAssets should return false on IOException`() = runTest {
        // Given
        coEvery { mockTrackDao.getTrackCount() } returns 0
        val mockAssets = mockk<android.content.res.AssetManager>(relaxed = true)
        every { mockContext.assets } returns mockAssets
        every { mockAssets.open("allTracks.json") } throws IOException("File not found")

        // When
        val result = trackRepository.loadTracksFromAssets()

        // Then
        assertFalse(result)
        coVerify { mockTrackDao.getTrackCount() }
        coVerify(exactly = 0) { mockTrackDao.insertTracks(any()) }
    }
}

