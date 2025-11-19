package com.hao.data.repository

import com.hao.data.local.HikeDao
import com.hao.data.model.LocalTrack
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

@OptIn(ExperimentalCoroutinesApi::class)
class HikeRepositoryTest {

    private lateinit var hikeRepository: HikeRepository
    private lateinit var mockHikeDao: HikeDao
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockHikeDao = mockk<HikeDao>(relaxed = true)
        hikeRepository = HikeRepository(mockHikeDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getAllHikes should return flow of hikes from dao`() = runTest {
        // Given
        val testHikes = listOf(
            LocalTrack(assetId = "1", name = "Hike 1", location = "Location 1", distanceKm = 5.0, duration = "2h", difficulty = "Easy", imageRes = 0),
            LocalTrack(assetId = "2", name = "Hike 2", location = "Location 2", distanceKm = 10.0, duration = "4h", difficulty = "Medium", imageRes = 0)
        )
        coEvery { mockHikeDao.getAllHikes() } returns flowOf(testHikes)

        // When
        val result = hikeRepository.getAllHikes()

        // Then
        result.collect { hikes ->
            assertEquals(testHikes, hikes)
        }
        coVerify { mockHikeDao.getAllHikes() }
    }

    @Test
    fun `getFavoriteHikes should return flow of favorite hikes from dao`() = runTest {
        // Given
        val testHikes = listOf(
            LocalTrack(assetId = "1", name = "Hike 1", location = "Location 1", distanceKm = 5.0, duration = "2h", difficulty = "Easy", imageRes = 0, isFavorite = true)
        )
        coEvery { mockHikeDao.getFavoriteHikes() } returns flowOf(testHikes)

        // When
        val result = hikeRepository.getFavoriteHikes()

        // Then
        result.collect { hikes ->
            assertEquals(testHikes, hikes)
        }
        coVerify { mockHikeDao.getFavoriteHikes() }
    }

    @Test
    fun `getDoneHikes should return flow of done hikes from dao`() = runTest {
        // Given
        val testHikes = listOf(
            LocalTrack(assetId = "1", name = "Hike 1", location = "Location 1", distanceKm = 5.0, duration = "2h", difficulty = "Easy", imageRes = 0, isDone = true)
        )
        coEvery { mockHikeDao.getDoneHikes() } returns flowOf(testHikes)

        // When
        val result = hikeRepository.getDoneHikes()

        // Then
        result.collect { hikes ->
            assertEquals(testHikes, hikes)
        }
        coVerify { mockHikeDao.getDoneHikes() }
    }

    @Test
    fun `getHikeByAssetId should return hike from dao`() = runTest {
        // Given
        val testHike = LocalTrack(assetId = "1", name = "Hike 1", location = "Location 1", distanceKm = 5.0, duration = "2h", difficulty = "Easy", imageRes = 0)
        coEvery { mockHikeDao.getHikeByAssetId("1") } returns testHike

        // When
        val result = hikeRepository.getHikeByAssetId("1")

        // Then
        assertEquals(testHike, result)
        coVerify { mockHikeDao.getHikeByAssetId("1") }
    }

    @Test
    fun `insertAll should call dao insertAll method`() = runTest {
        // Given
        val testHikes = listOf(
            LocalTrack(assetId = "1", name = "Hike 1", location = "Location 1", distanceKm = 5.0, duration = "2h", difficulty = "Easy", imageRes = 0)
        )
        coEvery { mockHikeDao.insertAll(testHikes) } just Runs

        // When
        hikeRepository.insertAll(testHikes)

        // Then
        coVerify { mockHikeDao.insertAll(testHikes) }
    }

    @Test
    fun `updateHike should call dao update method`() = runTest {
        // Given
        val testHike = LocalTrack(assetId = "1", name = "Hike 1", location = "Location 1", distanceKm = 5.0, duration = "2h", difficulty = "Easy", imageRes = 0)
        coEvery { mockHikeDao.updateHike(testHike) } just Runs

        // When
        hikeRepository.updateHike(testHike)

        // Then
        coVerify { mockHikeDao.updateHike(testHike) }
    }

    @Test
    fun `insertAll should throw exception for invalid hikes`() = runTest {
        // Given
        val invalidHikes = listOf(
            LocalTrack(assetId = "", name = "Invalid Hike", location = "Location 1", distanceKm = 5.0, duration = "2h", difficulty = "Easy", imageRes = 0)
        )

        // When & Then
        try {
            hikeRepository.insertAll(invalidHikes)
            fail("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            // Expected
        }
        coVerify(exactly = 0) { mockHikeDao.insertAll(any()) }
    }

    @Test
    fun `toggleFavorite should update hike favorite status`() = runTest {
        // Given
        val testHike = LocalTrack(assetId = "1", name = "Hike 1", location = "Location 1", distanceKm = 5.0, duration = "2h", difficulty = "Easy", imageRes = 0, isFavorite = false)
        coEvery { mockHikeDao.getHikeByAssetId("1") } returns testHike
        coEvery { mockHikeDao.updateHike(any()) } just Runs

        // When
        hikeRepository.toggleFavorite("1", true)

        // Then
        coVerify { 
            mockHikeDao.getHikeByAssetId("1")
            mockHikeDao.updateHike(match { it.isFavorite == true })
        }
    }

    @Test
    fun `markAsDone should update hike done status`() = runTest {
        // Given
        val testHike = LocalTrack(assetId = "1", name = "Hike 1", location = "Location 1", distanceKm = 5.0, duration = "2h", difficulty = "Easy", imageRes = 0, isDone = false)
        coEvery { mockHikeDao.getHikeByAssetId("1") } returns testHike
        coEvery { mockHikeDao.updateHike(any()) } just Runs

        // When
        hikeRepository.markAsDone("1", true)

        // Then
        coVerify { 
            mockHikeDao.getHikeByAssetId("1")
            mockHikeDao.updateHike(match { it.isDone == true })
        }
    }

    @Test
    fun `toggleFavorite should handle null hike gracefully`() = runTest {
        // Given
        coEvery { mockHikeDao.getHikeByAssetId("1") } returns null

        // When
        hikeRepository.toggleFavorite("1", true)

        // Then
        coVerify { mockHikeDao.getHikeByAssetId("1") }
        coVerify(exactly = 0) { mockHikeDao.updateHike(any()) }
    }

    @Test
    fun `markAsDone should handle null hike gracefully`() = runTest {
        // Given
        coEvery { mockHikeDao.getHikeByAssetId("1") } returns null

        // When
        hikeRepository.markAsDone("1", true)

        // Then
        coVerify { mockHikeDao.getHikeByAssetId("1") }
        coVerify(exactly = 0) { mockHikeDao.updateHike(any()) }
    }

    @Test
    fun `searchHikes should filter hikes by query`() = runTest {
        // Given
        val testHikes = listOf(
            LocalTrack(assetId = "1", name = "Mountain Hike", location = "Location 1", distanceKm = 5.0, duration = "2h", difficulty = "Easy", imageRes = 0),
            LocalTrack(assetId = "2", name = "Beach Walk", location = "Location 2", distanceKm = 10.0, duration = "4h", difficulty = "Medium", imageRes = 0)
        )
        coEvery { mockHikeDao.getAllHikes() } returns flowOf(testHikes)

        // When
        val result = hikeRepository.searchHikes("Mountain")
        val collected = mutableListOf<List<LocalTrack>>()
        val job = launch {
            result.collect { collected.add(it) }
        }
        advanceUntilIdle()
        job.cancel()

        // Then
        assertTrue(collected.isNotEmpty())
        val filtered = collected.last()
        assertEquals(1, filtered.size)
        assertEquals("Mountain Hike", filtered[0].name)
    }

    @Test
    fun `getHikeStats should return correct statistics`() = runTest {
        // Given
        val testHikes = listOf(
            LocalTrack(assetId = "1", name = "Hike 1", location = "Location 1", distanceKm = 5.0, duration = "2h", difficulty = "Easy", imageRes = 0, isFavorite = true),
            LocalTrack(assetId = "2", name = "Hike 2", location = "Location 2", distanceKm = 10.0, duration = "4h", difficulty = "Medium", imageRes = 0, isDone = true)
        )
        coEvery { mockHikeDao.getAllHikes() } returns flowOf(testHikes)

        // When
        val result = hikeRepository.getHikeStats()
        val collected = mutableListOf<HikeStats>()
        val job = launch {
            result.collect { collected.add(it) }
        }
        advanceUntilIdle()
        job.cancel()

        // Then
        assertTrue(collected.isNotEmpty())
        val stats = collected.last()
        assertEquals(2, stats.totalHikes)
        assertEquals(1, stats.favoriteHikes)
        assertEquals(1, stats.doneHikes)
        assertEquals(15.0, stats.totalDistance, 0.01)
    }
}
