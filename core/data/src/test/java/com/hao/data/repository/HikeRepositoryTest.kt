package com.hao.data.repository

import com.hao.data.local.HikeDao
import com.hao.data.model.LocalTrack
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
            LocalTrack(assetId = "1", name = "Hike 1", location = "Location 1", distanceKm = 5.0, duration = "2h", difficulty = "Easy"),
            LocalTrack(assetId = "2", name = "Hike 2", location = "Location 2", distanceKm = 10.0, duration = "4h", difficulty = "Medium")
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
            LocalTrack(assetId = "1", name = "Hike 1", location = "Location 1", distanceKm = 5.0, duration = "2h", difficulty = "Easy", isFavorite = true)
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
            LocalTrack(assetId = "1", name = "Hike 1", location = "Location 1", distanceKm = 5.0, duration = "2h", difficulty = "Easy", isDone = true)
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
    fun `getHikeById should return hike from dao`() = runTest {
        // Given
        val testHike = LocalTrack(assetId = "1", name = "Hike 1", location = "Location 1", distanceKm = 5.0, duration = "2h", difficulty = "Easy")
        coEvery { mockHikeDao.getHikeById("1") } returns testHike

        // When
        val result = hikeRepository.getHikeById("1")

        // Then
        assertEquals(testHike, result)
        coVerify { mockHikeDao.getHikeById("1") }
    }

    @Test
    fun `insertHike should call dao insert method`() = runTest {
        // Given
        val testHike = LocalTrack(assetId = "1", name = "Hike 1", location = "Location 1", distanceKm = 5.0, duration = "2h", difficulty = "Easy")
        coEvery { mockHikeDao.insertHike(testHike) } just Runs

        // When
        hikeRepository.insertHike(testHike)

        // Then
        coVerify { mockHikeDao.insertHike(testHike) }
    }

    @Test
    fun `updateHike should call dao update method`() = runTest {
        // Given
        val testHike = LocalTrack(assetId = "1", name = "Hike 1", location = "Location 1", distanceKm = 5.0, duration = "2h", difficulty = "Easy")
        coEvery { mockHikeDao.updateHike(testHike) } just Runs

        // When
        hikeRepository.updateHike(testHike)

        // Then
        coVerify { mockHikeDao.updateHike(testHike) }
    }

    @Test
    fun `deleteHike should call dao delete method`() = runTest {
        // Given
        val testHike = LocalTrack(assetId = "1", name = "Hike 1", location = "Location 1", distanceKm = 5.0, duration = "2h", difficulty = "Easy")
        coEvery { mockHikeDao.deleteHike(testHike) } just Runs

        // When
        hikeRepository.deleteHike(testHike)

        // Then
        coVerify { mockHikeDao.deleteHike(testHike) }
    }

    @Test
    fun `toggleFavorite should update hike favorite status`() = runTest {
        // Given
        val testHike = LocalTrack(assetId = "1", name = "Hike 1", location = "Location 1", distanceKm = 5.0, duration = "2h", difficulty = "Easy", isFavorite = false)
        coEvery { mockHikeDao.getHikeById("1") } returns testHike
        coEvery { mockHikeDao.updateHike(any()) } just Runs

        // When
        hikeRepository.toggleFavorite("1")

        // Then
        coVerify { 
            mockHikeDao.getHikeById("1")
            mockHikeDao.updateHike(testHike.copy(isFavorite = true))
        }
    }

    @Test
    fun `toggleDone should update hike done status`() = runTest {
        // Given
        val testHike = LocalTrack(assetId = "1", name = "Hike 1", location = "Location 1", distanceKm = 5.0, duration = "2h", difficulty = "Easy", isDone = false)
        coEvery { mockHikeDao.getHikeById("1") } returns testHike
        coEvery { mockHikeDao.updateHike(any()) } just Runs

        // When
        hikeRepository.toggleDone("1")

        // Then
        coVerify { 
            mockHikeDao.getHikeById("1")
            mockHikeDao.updateHike(testHike.copy(isDone = true))
        }
    }

    @Test
    fun `toggleFavorite should handle null hike gracefully`() = runTest {
        // Given
        coEvery { mockHikeDao.getHikeById("1") } returns null

        // When & Then
        assertThrows(IllegalArgumentException::class.java) {
            hikeRepository.toggleFavorite("1")
        }
        coVerify { mockHikeDao.getHikeById("1") }
        coVerify(exactly = 0) { mockHikeDao.updateHike(any()) }
    }

    @Test
    fun `toggleDone should handle null hike gracefully`() = runTest {
        // Given
        coEvery { mockHikeDao.getHikeById("1") } returns null

        // When & Then
        assertThrows(IllegalArgumentException::class.java) {
            hikeRepository.toggleDone("1")
        }
        coVerify { mockHikeDao.getHikeById("1") }
        coVerify(exactly = 0) { mockHikeDao.updateHike(any()) }
    }
}
