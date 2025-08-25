package com.hao.explore

import com.hao.data.data.repository.TrackRepository
import com.hao.data.model.Campsite
import com.hao.data.model.Hut
import com.hao.data.data.model.RemoteTrack
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
    private lateinit var mockTrackRepository: TrackRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockTrackRepository = mockk<TrackRepository>(relaxed = true)
        homeViewModel = HomeViewModel(mockTrackRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be loading`() = runTest {
        // When
        val initialState = homeViewModel.uiState.value

        // Then
        assertTrue(initialState.isLoading)
        assertNull(initialState.error)
        assertTrue(initialState.tracks.isEmpty())
        assertTrue(initialState.campsites.isEmpty())
        assertTrue(initialState.huts.isEmpty())
    }

    @Test
    fun `loadData should update state with tracks, campsites and huts`() = runTest {
        // Given
        val testTracks = listOf(
            RemoteTrack(
                assetId = "track1",
                name = "Test Track 1",
                region = listOf("Region 1"),
                x = 174.0,
                y = -41.0,
                line = listOf(listOf(listOf(174.0, -41.0)))
            )
        )
        val testCampsites = listOf(
            Campsite(assetId = "campsite1", name = "Test Campsite 1", region = "Region 1", y = -41.0, x = 174.0)
        )
        val testHuts = listOf(
            Hut(assetId = "hut1", name = "Test Hut 1", region = "Region 1", y = -41.0, x = 174.0)
        )

        coEvery { mockTrackRepository.getAllTracks() } returns flowOf(testTracks)
        coEvery { mockTrackRepository.getAllCampsites() } returns flowOf(testCampsites)
        coEvery { mockTrackRepository.getAllHuts() } returns flowOf(testHuts)

        // When
        homeViewModel.loadData()

        // Then
        advanceUntilIdle()
        val state = homeViewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(testTracks, state.tracks)
        assertEquals(testCampsites, state.campsites)
        assertEquals(testHuts, state.huts)
    }

    @Test
    fun `loadData should handle repository errors`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { mockTrackRepository.getAllTracks() } throws Exception(errorMessage)

        // When
        homeViewModel.loadData()

        // Then
        advanceUntilIdle()
        val state = homeViewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `search should filter tracks by name`() = runTest {
        // Given
        val testTracks = listOf(
            RemoteTrack(
                assetId = "track1",
                name = "Mountain Track",
                region = listOf("Region 1"),
                x = 174.0,
                y = -41.0,
                line = listOf(listOf(listOf(174.0, -41.0)))
            ),
            RemoteTrack(
                assetId = "track2",
                name = "Beach Walk",
                region = listOf("Region 2"),
                x = 174.0,
                y = -41.0,
                line = listOf(listOf(listOf(174.0, -41.0)))
            )
        )
        coEvery { mockTrackRepository.getAllTracks() } returns flowOf(testTracks)
        coEvery { mockTrackRepository.getAllCampsites() } returns flowOf(emptyList())
        coEvery { mockTrackRepository.getAllHuts() } returns flowOf(emptyList())

        homeViewModel.loadData()
        advanceUntilIdle()

        // When
        homeViewModel.search("Mountain")

        // Then
        val state = homeViewModel.uiState.value
        assertEquals(1, state.filteredTracks.size)
        assertEquals("Mountain Track", state.filteredTracks[0].name)
    }

    @Test
    fun `search should filter campsites by name`() = runTest {
        // Given
        val testCampsites = listOf(
            Campsite(assetId = "campsite1", name = "Mountain Campsite", region = "Region 1", y = -41.0, x = 174.0),
            Campsite(assetId = "campsite2", name = "Beach Campsite", region = "Region 2", y = -41.0, x = 174.0)
        )
        coEvery { mockTrackRepository.getAllTracks() } returns flowOf(emptyList())
        coEvery { mockTrackRepository.getAllCampsites() } returns flowOf(testCampsites)
        coEvery { mockTrackRepository.getAllHuts() } returns flowOf(emptyList())

        homeViewModel.loadData()
        advanceUntilIdle()

        // When
        homeViewModel.search("Mountain")

        // Then
        val state = homeViewModel.uiState.value
        assertEquals(1, state.filteredCampsites.size)
        assertEquals("Mountain Campsite", state.filteredCampsites[0].name)
    }

    @Test
    fun `search should filter huts by name`() = runTest {
        // Given
        val testHuts = listOf(
            Hut(assetId = "hut1", name = "Mountain Hut", region = "Region 1", y = -41.0, x = 174.0),
            Hut(assetId = "hut2", name = "Beach Hut", region = "Region 2", y = -41.0, x = 174.0)
        )
        coEvery { mockTrackRepository.getAllTracks() } returns flowOf(emptyList())
        coEvery { mockTrackRepository.getAllCampsites() } returns flowOf(emptyList())
        coEvery { mockTrackRepository.getAllHuts() } returns flowOf(testHuts)

        homeViewModel.loadData()
        advanceUntilIdle()

        // When
        homeViewModel.search("Mountain")

        // Then
        val state = homeViewModel.uiState.value
        assertEquals(1, state.filteredHuts.size)
        assertEquals("Mountain Hut", state.filteredHuts[0].name)
    }

    @Test
    fun `search should be case insensitive`() = runTest {
        // Given
        val testTracks = listOf(
            RemoteTrack(
                assetId = "track1",
                name = "Mountain Track",
                region = listOf("Region 1"),
                x = 174.0,
                y = -41.0,
                line = listOf(listOf(listOf(174.0, -41.0)))
            )
        )
        coEvery { mockTrackRepository.getAllTracks() } returns flowOf(testTracks)
        coEvery { mockTrackRepository.getAllCampsites() } returns flowOf(emptyList())
        coEvery { mockTrackRepository.getAllHuts() } returns flowOf(emptyList())

        homeViewModel.loadData()
        advanceUntilIdle()

        // When
        homeViewModel.search("mountain")

        // Then
        val state = homeViewModel.uiState.value
        assertEquals(1, state.filteredTracks.size)
        assertEquals("Mountain Track", state.filteredTracks[0].name)
    }

    @Test
    fun `clearSearch should reset filtered results`() = runTest {
        // Given
        val testTracks = listOf(
            RemoteTrack(
                assetId = "track1",
                name = "Mountain Track",
                region = listOf("Region 1"),
                x = 174.0,
                y = -41.0,
                line = listOf(listOf(listOf(174.0, -41.0)))
            )
        )
        coEvery { mockTrackRepository.getAllTracks() } returns flowOf(testTracks)
        coEvery { mockTrackRepository.getAllCampsites() } returns flowOf(emptyList())
        coEvery { mockTrackRepository.getAllHuts() } returns flowOf(emptyList())

        homeViewModel.loadData()
        advanceUntilIdle()
        homeViewModel.search("Mountain")

        // When
        homeViewModel.clearSearch()

        // Then
        val state = homeViewModel.uiState.value
        assertEquals(testTracks, state.filteredTracks)
        assertTrue(state.filteredCampsites.isEmpty())
        assertTrue(state.filteredHuts.isEmpty())
    }

    @Test
    fun `loadData should handle empty results`() = runTest {
        // Given
        coEvery { mockTrackRepository.getAllTracks() } returns flowOf(emptyList())
        coEvery { mockTrackRepository.getAllCampsites() } returns flowOf(emptyList())
        coEvery { mockTrackRepository.getAllHuts() } returns flowOf(emptyList())

        // When
        homeViewModel.loadData()

        // Then
        advanceUntilIdle()
        val state = homeViewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertTrue(state.tracks.isEmpty())
        assertTrue(state.campsites.isEmpty())
        assertTrue(state.huts.isEmpty())
    }
}
