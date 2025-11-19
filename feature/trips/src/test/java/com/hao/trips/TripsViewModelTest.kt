package com.hao.trips

import com.hao.data.model.LocalTrack
import com.hao.data.repository.HikeRepository
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
class TripsViewModelTest {

    private lateinit var tripsViewModel: TripsViewModel
    private lateinit var mockHikeRepository: HikeRepository
    private val testDispatcher = StandardTestDispatcher()
    private val testScheduler = testDispatcher.scheduler

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockHikeRepository = mockk<HikeRepository>(relaxed = true)
        
        coEvery { mockHikeRepository.getFavoriteHikes() } returns flowOf(emptyList())
        coEvery { mockHikeRepository.getDoneHikes() } returns flowOf(emptyList())
        coEvery { mockHikeRepository.updateHike(any()) } just Runs
        
        tripsViewModel = TripsViewModel(mockHikeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial favoriteHikes should be empty`() = runTest {
        // When
        val favoriteHikes = tripsViewModel.favoriteHikes.value

        // Then
        assertTrue(favoriteHikes.isEmpty())
    }

    @Test
    fun `initial doneHikes should be empty`() = runTest {
        // When
        val doneHikes = tripsViewModel.doneHikes.value

        // Then
        assertTrue(doneHikes.isEmpty())
    }

    @Test
    fun `favoriteHikes should be initialized from repository`() = runTest {
        // Given
        val testHikes = listOf(
            LocalTrack(
                assetId = "hike1",
                name = "Favorite Hike 1",
                location = "Location 1",
                distanceKm = 5.0,
                duration = "2h",
                difficulty = "Easy",
                imageRes = 0,
                isFavorite = true
            ),
            LocalTrack(
                assetId = "hike2",
                name = "Favorite Hike 2",
                location = "Location 2",
                distanceKm = 10.0,
                duration = "4h",
                difficulty = "Medium",
                imageRes = 0,
                isFavorite = true
            )
        )
        coEvery { mockHikeRepository.getFavoriteHikes() } returns flowOf(testHikes)

        // When
        tripsViewModel = TripsViewModel(mockHikeRepository)
        // Collect from the flow to trigger subscription
        val collectedHikes = mutableListOf<List<LocalTrack>>()
        val job = launch {
            tripsViewModel.favoriteHikes.collect { collectedHikes.add(it) }
        }
        advanceUntilIdle()
        testScheduler.advanceTimeBy(6000) // Wait for WhileSubscribed timeout
        advanceUntilIdle()
        job.cancel()

        // Then
        assertTrue(collectedHikes.isNotEmpty())
        val favoriteHikes = collectedHikes.last()
        assertEquals(2, favoriteHikes.size)
        assertEquals("Favorite Hike 1", favoriteHikes[0].name)
        assertEquals("Favorite Hike 2", favoriteHikes[1].name)
        assertTrue(favoriteHikes.all { it.isFavorite })
    }

    @Test
    fun `doneHikes should be initialized from repository`() = runTest {
        // Given
        val testHikes = listOf(
            LocalTrack(
                assetId = "hike1",
                name = "Done Hike 1",
                location = "Location 1",
                distanceKm = 5.0,
                duration = "2h",
                difficulty = "Easy",
                imageRes = 0,
                isDone = true
            ),
            LocalTrack(
                assetId = "hike2",
                name = "Done Hike 2",
                location = "Location 2",
                distanceKm = 10.0,
                duration = "4h",
                difficulty = "Medium",
                imageRes = 0,
                isDone = true
            )
        )
        coEvery { mockHikeRepository.getDoneHikes() } returns flowOf(testHikes)

        // When
        tripsViewModel = TripsViewModel(mockHikeRepository)
        // Collect from the flow (SharingStarted.Lazily starts immediately on first collect)
        val collectedHikes = mutableListOf<List<LocalTrack>>()
        val job = launch {
            tripsViewModel.doneHikes.collect { collectedHikes.add(it) }
        }
        advanceUntilIdle()
        job.cancel()

        // Then
        assertTrue(collectedHikes.isNotEmpty())
        val doneHikes = collectedHikes.last()
        assertEquals(2, doneHikes.size)
        assertEquals("Done Hike 1", doneHikes[0].name)
        assertEquals("Done Hike 2", doneHikes[1].name)
        assertTrue(doneHikes.all { it.isDone })
    }

    @Test
    fun `toggleFavorite should update hike favorite status`() = runTest {
        // Given
        val testHike = LocalTrack(
            assetId = "hike1",
            name = "Test Hike",
            location = "Location 1",
            distanceKm = 5.0,
            duration = "2h",
            difficulty = "Easy",
            imageRes = 0,
            isFavorite = false
        )
        coEvery { mockHikeRepository.updateHike(any()) } just Runs

        // When
        tripsViewModel.toggleFavorite(testHike)
        advanceUntilIdle()

        // Then
        coVerify { mockHikeRepository.updateHike(match { it.assetId == testHike.assetId && it.isFavorite == true }) }
    }

    @Test
    fun `toggleFavorite should toggle from favorite to not favorite`() = runTest {
        // Given
        val testHike = LocalTrack(
            assetId = "hike1",
            name = "Test Hike",
            location = "Location 1",
            distanceKm = 5.0,
            duration = "2h",
            difficulty = "Easy",
            imageRes = 0,
            isFavorite = true
        )
        coEvery { mockHikeRepository.updateHike(any()) } just Runs

        // When
        tripsViewModel.toggleFavorite(testHike)
        advanceUntilIdle()

        // Then
        coVerify { mockHikeRepository.updateHike(match { it.assetId == testHike.assetId && it.isFavorite == false }) }
    }

    @Test
    fun `favoriteHikes should handle empty list`() = runTest {
        // Given
        coEvery { mockHikeRepository.getFavoriteHikes() } returns flowOf(emptyList())

        // When
        tripsViewModel = TripsViewModel(mockHikeRepository)
        advanceUntilIdle()

        // Then
        val favoriteHikes = tripsViewModel.favoriteHikes.value
        assertTrue(favoriteHikes.isEmpty())
    }

    @Test
    fun `doneHikes should handle empty list`() = runTest {
        // Given
        coEvery { mockHikeRepository.getDoneHikes() } returns flowOf(emptyList())

        // When
        tripsViewModel = TripsViewModel(mockHikeRepository)
        advanceUntilIdle()

        // Then
        val doneHikes = tripsViewModel.doneHikes.value
        assertTrue(doneHikes.isEmpty())
    }
}

