package com.hao.explore

import com.hao.data.data.repository.TrackRepository
import com.hao.data.model.Campsite
import com.hao.data.model.Hut
import com.hao.data.data.model.RemoteTrack
import com.hao.explore.model.SearchResult
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
class SearchViewModelTest {

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var mockTrackRepository: TrackRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockTrackRepository = mockk<TrackRepository>(relaxed = true)
        searchViewModel = SearchViewModel(mockTrackRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be empty`() = runTest {
        // When
        val initialState = searchViewModel.searchResults.value

        // Then
        assertTrue(initialState.isEmpty())
    }

    @Test
    fun `search should return track results`() = runTest {
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

        // When
        searchViewModel.search("Mountain")

        // Then
        advanceUntilIdle()
        val results = searchViewModel.searchResults.value
        assertEquals(1, results.size)
        assertTrue(results[0] is SearchResult.TrackResult)
        assertEquals("Mountain Track", results[0].name)
    }

    @Test
    fun `search should return campsite results`() = runTest {
        // Given
        val testCampsites = listOf(
            Campsite(assetId = "campsite1", name = "Mountain Campsite", region = "Region 1", y = -41.0, x = 174.0)
        )
        coEvery { mockTrackRepository.getAllTracks() } returns flowOf(emptyList())
        coEvery { mockTrackRepository.getAllCampsites() } returns flowOf(testCampsites)
        coEvery { mockTrackRepository.getAllHuts() } returns flowOf(emptyList())

        // When
        searchViewModel.search("Mountain")

        // Then
        advanceUntilIdle()
        val results = searchViewModel.searchResults.value
        assertEquals(1, results.size)
        assertTrue(results[0] is SearchResult.CampsiteResult)
        assertEquals("Mountain Campsite", results[0].name)
    }

    @Test
    fun `search should return hut results`() = runTest {
        // Given
        val testHuts = listOf(
            Hut(assetId = "hut1", name = "Mountain Hut", region = "Region 1", y = -41.0, x = 174.0)
        )
        coEvery { mockTrackRepository.getAllTracks() } returns flowOf(emptyList())
        coEvery { mockTrackRepository.getAllCampsites() } returns flowOf(emptyList())
        coEvery { mockTrackRepository.getAllHuts() } returns flowOf(testHuts)

        // When
        searchViewModel.search("Mountain")

        // Then
        advanceUntilIdle()
        val results = searchViewModel.searchResults.value
        assertEquals(1, results.size)
        assertTrue(results[0] is SearchResult.HutResult)
        assertEquals("Mountain Hut", results[0].name)
    }

    @Test
    fun `search should return mixed results from all sources`() = runTest {
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
        val testCampsites = listOf(
            Campsite(assetId = "campsite1", name = "Mountain Campsite", region = "Region 1", y = -41.0, x = 174.0)
        )
        val testHuts = listOf(
            Hut(assetId = "hut1", name = "Mountain Hut", region = "Region 1", y = -41.0, x = 174.0)
        )

        coEvery { mockTrackRepository.getAllTracks() } returns flowOf(testTracks)
        coEvery { mockTrackRepository.getAllCampsites() } returns flowOf(testCampsites)
        coEvery { mockTrackRepository.getAllHuts() } returns flowOf(testHuts)

        // When
        searchViewModel.search("Mountain")

        // Then
        advanceUntilIdle()
        val results = searchViewModel.searchResults.value
        assertEquals(3, results.size)
        
        val trackResults = results.filterIsInstance<SearchResult.TrackResult>()
        val campsiteResults = results.filterIsInstance<SearchResult.CampsiteResult>()
        val hutResults = results.filterIsInstance<SearchResult.HutResult>()
        
        assertEquals(1, trackResults.size)
        assertEquals(1, campsiteResults.size)
        assertEquals(1, hutResults.size)
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

        // When
        searchViewModel.search("mountain")

        // Then
        advanceUntilIdle()
        val results = searchViewModel.searchResults.value
        assertEquals(1, results.size)
        assertEquals("Mountain Track", results[0].name)
    }

    @Test
    fun `search should return empty results for no matches`() = runTest {
        // Given
        val testTracks = listOf(
            RemoteTrack(
                assetId = "track1",
                name = "Beach Track",
                region = listOf("Region 1"),
                x = 174.0,
                y = -41.0,
                line = listOf(listOf(listOf(174.0, -41.0)))
            )
        )
        coEvery { mockTrackRepository.getAllTracks() } returns flowOf(testTracks)
        coEvery { mockTrackRepository.getAllCampsites() } returns flowOf(emptyList())
        coEvery { mockTrackRepository.getAllHuts() } returns flowOf(emptyList())

        // When
        searchViewModel.search("Mountain")

        // Then
        advanceUntilIdle()
        val results = searchViewModel.searchResults.value
        assertTrue(results.isEmpty())
    }

    @Test
    fun `search should handle empty query`() = runTest {
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

        // When
        searchViewModel.search("")

        // Then
        advanceUntilIdle()
        val results = searchViewModel.searchResults.value
        assertTrue(results.isEmpty())
    }

    @Test
    fun `search should handle null name values`() = runTest {
        // Given
        val testCampsites = listOf(
            Campsite(assetId = "campsite1", name = null, region = "Region 1", y = -41.0, x = 174.0)
        )
        coEvery { mockTrackRepository.getAllTracks() } returns flowOf(emptyList())
        coEvery { mockTrackRepository.getAllCampsites() } returns flowOf(testCampsites)
        coEvery { mockTrackRepository.getAllHuts() } returns flowOf(emptyList())

        // When
        searchViewModel.search("Mountain")

        // Then
        advanceUntilIdle()
        val results = searchViewModel.searchResults.value
        assertEquals(1, results.size)
        assertEquals("", results[0].name)
    }

    @Test
    fun `clearSearch should reset results`() = runTest {
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

        searchViewModel.search("Mountain")
        advanceUntilIdle()

        // When
        searchViewModel.clearSearch()

        // Then
        val results = searchViewModel.searchResults.value
        assertTrue(results.isEmpty())
    }
}
