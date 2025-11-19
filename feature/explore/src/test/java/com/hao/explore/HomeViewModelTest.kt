package com.hao.explore

import com.hao.data.local.CampsiteDao
import com.hao.data.local.HutDao
import com.hao.data.model.Campsite
import com.hao.data.model.Hut
import com.hao.data.model.LocalTrack
import com.hao.data.repository.HikeRepository
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
class HomeViewModelTest {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mockHikeRepository: HikeRepository
    private lateinit var mockCampsiteDao: CampsiteDao
    private lateinit var mockHutDao: HutDao
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockHikeRepository = mockk<HikeRepository>(relaxed = true)
        mockCampsiteDao = mockk<CampsiteDao>(relaxed = true)
        mockHutDao = mockk<HutDao>(relaxed = true)
        
        coEvery { mockHikeRepository.getAllHikes() } returns flowOf(emptyList())
        coEvery { mockCampsiteDao.getAllCampsites() } returns flowOf(emptyList())
        coEvery { mockHutDao.getAllHuts() } returns flowOf(emptyList())
        coEvery { mockHikeRepository.insertAll(any()) } just Runs
        
        homeViewModel = HomeViewModel(
            mockHikeRepository,
            mockCampsiteDao,
            mockHutDao
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have empty lists`() = runTest {
        // When
        advanceUntilIdle()
        val initialState = homeViewModel.uiState.value

        // Then
        assertTrue(initialState.dayHikes.isEmpty())
        assertTrue(initialState.greatWalks.isEmpty())
        assertTrue(initialState.campsites.isEmpty())
        assertTrue(initialState.huts.isEmpty())
        assertFalse(initialState.isLoading)
        assertNull(initialState.error)
    }

    @Test
    fun `should update state with tracks, campsites and huts`() = runTest {
        // Given
        val testHikes = listOf(
            LocalTrack(
                assetId = "hike1",
                name = "Test Hike 1",
                location = "Location 1",
                distanceKm = 5.0,
                duration = "2h",
                difficulty = "Easy",
                imageRes = 0
            ),
            LocalTrack(
                assetId = "hike2",
                name = "Great Walk",
                location = "Location 2",
                distanceKm = 50.0,
                duration = "4 days",
                difficulty = "Intermediate",
                imageRes = 0
            )
        )
        val testCampsites = listOf(
            Campsite(assetId = "campsite1", name = "Test Campsite 1", region = "Region 1", y = -41.0, x = 174.0)
        )
        val testHuts = listOf(
            Hut(assetId = "hut1", name = "Test Hut 1", region = "Region 1", y = -41.0, x = 174.0)
        )

        coEvery { mockHikeRepository.getAllHikes() } returns flowOf(testHikes)
        coEvery { mockCampsiteDao.getAllCampsites() } returns flowOf(testCampsites)
        coEvery { mockHutDao.getAllHuts() } returns flowOf(testHuts)

        // When
        homeViewModel = HomeViewModel(mockHikeRepository, mockCampsiteDao, mockHutDao)
        advanceUntilIdle()

        // Then
        val state = homeViewModel.uiState.value
        assertEquals(1, state.dayHikes.size)
        assertEquals(1, state.greatWalks.size)
        assertEquals(1, state.campsites.size)
        assertEquals(1, state.huts.size)
        assertEquals("Test Hike 1", state.dayHikes[0].name)
        assertEquals("Great Walk", state.greatWalks[0].name)
    }

    @Test
    fun `should separate day hikes and great walks based on duration`() = runTest {
        // Given
        val testHikes = listOf(
            LocalTrack(
                assetId = "hike1",
                name = "Day Hike",
                location = "Location 1",
                distanceKm = 5.0,
                duration = "2h",
                difficulty = "Easy",
                imageRes = 0
            ),
            LocalTrack(
                assetId = "hike2",
                name = "Great Walk 1",
                location = "Location 2",
                distanceKm = 50.0,
                duration = "4 days",
                difficulty = "Intermediate",
                imageRes = 0
            ),
            LocalTrack(
                assetId = "hike3",
                name = "Great Walk 2",
                location = "Location 3",
                distanceKm = 60.0,
                duration = "3–4 days",
                difficulty = "Intermediate",
                imageRes = 0
            )
        )

        coEvery { mockHikeRepository.getAllHikes() } returns flowOf(testHikes)
        coEvery { mockCampsiteDao.getAllCampsites() } returns flowOf(emptyList())
        coEvery { mockHutDao.getAllHuts() } returns flowOf(emptyList())

        // When
        homeViewModel = HomeViewModel(mockHikeRepository, mockCampsiteDao, mockHutDao)
        advanceUntilIdle()

        // Then
        val state = homeViewModel.uiState.value
        assertEquals(1, state.dayHikes.size)
        assertEquals(2, state.greatWalks.size)
        assertEquals("Day Hike", state.dayHikes[0].name)
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
        homeViewModel.toggleFavorite(testHike)
        advanceUntilIdle()

        // Then
        coVerify { mockHikeRepository.updateHike(match { it.assetId == testHike.assetId && it.isFavorite == true }) }
    }

    @Test
    fun `should handle empty results`() = runTest {
        // Given
        coEvery { mockHikeRepository.getAllHikes() } returns flowOf(emptyList())
        coEvery { mockCampsiteDao.getAllCampsites() } returns flowOf(emptyList())
        coEvery { mockHutDao.getAllHuts() } returns flowOf(emptyList())

        // When
        homeViewModel = HomeViewModel(mockHikeRepository, mockCampsiteDao, mockHutDao)
        advanceUntilIdle()

        // Then
        val state = homeViewModel.uiState.value
        assertTrue(state.dayHikes.isEmpty())
        assertTrue(state.greatWalks.isEmpty())
        assertTrue(state.campsites.isEmpty())
        assertTrue(state.huts.isEmpty())
    }
}
