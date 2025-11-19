package com.hao.explore

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.hao.data.data.repository.TrackRepository
import com.hao.data.data.model.RemoteTrack
import com.hao.data.model.Campsite
import com.hao.data.model.Hut
import com.hao.data.repository.CampsiteRepository
import com.hao.data.repository.HutRepository
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
    private lateinit var mockCampsiteRepository: CampsiteRepository
    private lateinit var mockHutRepository: HutRepository
    private lateinit var mockSavedStateHandle: SavedStateHandle
    private lateinit var mockContext: Context
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockTrackRepository = mockk<TrackRepository>(relaxed = true)
        mockCampsiteRepository = mockk<CampsiteRepository>(relaxed = true)
        mockHutRepository = mockk<HutRepository>(relaxed = true)
        mockSavedStateHandle = mockk<SavedStateHandle>(relaxed = true)
        mockContext = mockk<Context>(relaxed = true)
        
        every { mockSavedStateHandle.get<Int>("searchType") } returns 0
        coEvery { mockTrackRepository.loadTracksFromAssets(any()) } returns true
        coEvery { mockCampsiteRepository.loadCampsitesFromAssets(any()) } returns true
        coEvery { mockHutRepository.loadHutsFromAssets(any()) } returns true
        
        searchViewModel = SearchViewModel(
            mockTrackRepository,
            mockCampsiteRepository,
            mockHutRepository,
            mockSavedStateHandle,
            mockContext
        )
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
    fun `onSearchQueryChanged should trigger search for tracks`() = runTest {
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
        every { mockSavedStateHandle.get<Int>("searchType") } returns 0
        coEvery { mockTrackRepository.searchTracks(any()) } returns flowOf(testTracks)

        // When
        searchViewModel.onSearchQueryChanged("Mountain")
        advanceUntilIdle()

        // Then
        val results = searchViewModel.searchResults.value
        assertEquals(1, results.size)
        assertTrue(results[0] is SearchResult.TrackResult)
        assertEquals("Mountain Track", results[0].name)
    }

    @Test
    fun `onSearchQueryChanged should trigger search for campsites`() = runTest {
        // Given
        val testCampsites = listOf(
            Campsite(assetId = "campsite1", name = "Mountain Campsite", region = "Region 1", y = -41.0, x = 174.0)
        )
        every { mockSavedStateHandle.get<Int>("searchType") } returns 1
        coEvery { mockCampsiteRepository.searchCampsites(any()) } returns flowOf(testCampsites)

        // When
        searchViewModel.onSearchQueryChanged("Mountain")
        advanceUntilIdle()

        // Then
        val results = searchViewModel.searchResults.value
        assertEquals(1, results.size)
        assertTrue(results[0] is SearchResult.CampsiteResult)
        assertEquals("Mountain Campsite", results[0].name)
    }

    @Test
    fun `onSearchQueryChanged should trigger search for huts`() = runTest {
        // Given
        val testHuts = listOf(
            Hut(assetId = "hut1", name = "Mountain Hut", region = "Region 1", y = -41.0, x = 174.0)
        )
        every { mockSavedStateHandle.get<Int>("searchType") } returns 2
        coEvery { mockHutRepository.searchHuts(any()) } returns flowOf(testHuts)

        // When
        searchViewModel.onSearchQueryChanged("Mountain")
        advanceUntilIdle()

        // Then
        val results = searchViewModel.searchResults.value
        assertEquals(1, results.size)
        assertTrue(results[0] is SearchResult.HutResult)
        assertEquals("Mountain Hut", results[0].name)
    }

    @Test
    fun `onSearchQueryChanged with empty query should return empty results`() = runTest {
        // When
        searchViewModel.onSearchQueryChanged("")
        advanceUntilIdle()

        // Then
        val results = searchViewModel.searchResults.value
        assertTrue(results.isEmpty())
    }

    @Test
    fun `onSearchQueryChanged should be case insensitive`() = runTest {
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
        every { mockSavedStateHandle.get<Int>("searchType") } returns 0
        coEvery { mockTrackRepository.searchTracks(any()) } returns flowOf(testTracks)

        // When
        searchViewModel.onSearchQueryChanged("mountain")
        advanceUntilIdle()

        // Then
        val results = searchViewModel.searchResults.value
        assertEquals(1, results.size)
        assertEquals("Mountain Track", results[0].name)
    }

    @Test
    fun `searchQuery should update when onSearchQueryChanged is called`() = runTest {
        // When
        searchViewModel.onSearchQueryChanged("Test Query")

        // Then
        assertEquals("Test Query", searchViewModel.searchQuery.value)
    }
}
