package com.hao.explore

import androidx.lifecycle.SavedStateHandle
import com.hao.data.model.Campsite
import com.hao.data.remote.CampsiteDetailsResponse
import com.hao.data.repository.CampsiteRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CampsiteDetailViewModelTest {

    private lateinit var campsiteDetailViewModel: CampsiteDetailViewModel
    private lateinit var mockCampsiteRepository: CampsiteRepository
    private lateinit var mockSavedStateHandle: SavedStateHandle
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockCampsiteRepository = mockk<CampsiteRepository>(relaxed = true)
        mockSavedStateHandle = mockk<SavedStateHandle>(relaxed = true)
        every { mockSavedStateHandle.get<String>("campsiteId") } returns "test-campsite-id"
        
        campsiteDetailViewModel = CampsiteDetailViewModel(mockCampsiteRepository, mockSavedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be loading`() = runTest {
        // When
        val initialState = campsiteDetailViewModel.uiState.value

        // Then
        assertTrue(initialState.isLoading)
        assertNull(initialState.error)
        assertNull(initialState.campsite)
        assertNull(initialState.details)
    }

    @Test
    fun `loadCampsiteDetails should update state with campsite and details`() = runTest {
        // Given
        val testCampsite = Campsite(
            assetId = "test-campsite-id",
            name = "Test Campsite",
            region = "Test Region",
            y = -41.0,
            x = 174.0
        )
        val testDetails = CampsiteDetailsResponse(
            assetId = "test-campsite-id",
            name = "Test Campsite",
            locationString = "Test Location",
            introduction = "Test Introduction",
            introductionThumbnail = "test-thumbnail.jpg",
            landscape = listOf("Mountain", "Forest"),
            category = "Standard",
            access = listOf("Road", "Walking"),
            facilities = listOf("Toilets", "Water"),
            activities = listOf("Hiking", "Fishing"),
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

        coEvery { mockCampsiteRepository.getCampsiteById("test-campsite-id") } returns testCampsite
        coEvery { mockCampsiteRepository.getCampsiteDetails("test-campsite-id") } returns testDetails

        // When
        campsiteDetailViewModel.loadCampsiteDetails()

        // Then
        advanceUntilIdle()
        val state = campsiteDetailViewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(testCampsite, state.campsite)
        assertNotNull(state.details)
        assertEquals("Test Campsite", state.details?.name)
        assertEquals("Test Location", state.details?.locationString)
        assertEquals("Test Introduction", state.details?.introduction)
    }

    @Test
    fun `loadCampsiteDetails should handle repository errors`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { mockCampsiteRepository.getCampsiteById("test-campsite-id") } throws Exception(errorMessage)

        // When
        campsiteDetailViewModel.loadCampsiteDetails()

        // Then
        advanceUntilIdle()
        val state = campsiteDetailViewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.error)
        assertNull(state.campsite)
        assertNull(state.details)
    }

    @Test
    fun `loadCampsiteDetails should handle null campsite`() = runTest {
        // Given
        coEvery { mockCampsiteRepository.getCampsiteById("test-campsite-id") } returns null

        // When
        campsiteDetailViewModel.loadCampsiteDetails()

        // Then
        advanceUntilIdle()
        val state = campsiteDetailViewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
        assertNull(state.campsite)
        assertNull(state.details)
    }

    @Test
    fun `loadCampsiteDetails should handle details API error gracefully`() = runTest {
        // Given
        val testCampsite = Campsite(
            assetId = "test-campsite-id",
            name = "Test Campsite",
            region = "Test Region",
            y = -41.0,
            x = 174.0
        )
        coEvery { mockCampsiteRepository.getCampsiteById("test-campsite-id") } returns testCampsite
        coEvery { mockCampsiteRepository.getCampsiteDetails("test-campsite-id") } throws Exception("API Error")

        // When
        campsiteDetailViewModel.loadCampsiteDetails()

        // Then
        advanceUntilIdle()
        val state = campsiteDetailViewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error) // Error should be handled gracefully
        assertEquals(testCampsite, state.campsite)
        assertNull(state.details) // Details should be null when API fails
    }

    @Test
    fun `loadCampsiteDetails should handle null details response`() = runTest {
        // Given
        val testCampsite = Campsite(
            assetId = "test-campsite-id",
            name = "Test Campsite",
            region = "Test Region",
            y = -41.0,
            x = 174.0
        )
        coEvery { mockCampsiteRepository.getCampsiteById("test-campsite-id") } returns testCampsite
        coEvery { mockCampsiteRepository.getCampsiteDetails("test-campsite-id") } returns null

        // When
        campsiteDetailViewModel.loadCampsiteDetails()

        // Then
        advanceUntilIdle()
        val state = campsiteDetailViewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(testCampsite, state.campsite)
        assertNull(state.details)
    }

    @Test
    fun `loadCampsiteDetails should handle missing campsiteId parameter`() = runTest {
        // Given
        every { mockSavedStateHandle.get<String>("campsiteId") } returns null
        val viewModel = CampsiteDetailViewModel(mockCampsiteRepository, mockSavedStateHandle)

        // When
        viewModel.loadCampsiteDetails()

        // Then
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
        assertNull(state.campsite)
        assertNull(state.details)
    }

    @Test
    fun `loadCampsiteDetails should map CampsiteDetailsResponse to CampsiteDetails correctly`() = runTest {
        // Given
        val testCampsite = Campsite(
            assetId = "test-campsite-id",
            name = "Test Campsite",
            region = "Test Region",
            y = -41.0,
            x = 174.0
        )
        val testDetailsResponse = CampsiteDetailsResponse(
            assetId = "test-campsite-id",
            name = "Test Campsite",
            locationString = "Test Location",
            introduction = "Test Introduction",
            introductionThumbnail = "test-thumbnail.jpg",
            landscape = listOf("Mountain", "Forest"),
            category = "Standard",
            access = listOf("Road", "Walking"),
            facilities = listOf("Toilets", "Water"),
            activities = listOf("Hiking", "Fishing"),
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

        coEvery { mockCampsiteRepository.getCampsiteById("test-campsite-id") } returns testCampsite
        coEvery { mockCampsiteRepository.getCampsiteDetails("test-campsite-id") } returns testDetailsResponse

        // When
        campsiteDetailViewModel.loadCampsiteDetails()

        // Then
        advanceUntilIdle()
        val state = campsiteDetailViewModel.uiState.value
        val details = state.details
        
        assertNotNull(details)
        assertEquals("test-campsite-id", details?.assetId)
        assertEquals("Test Campsite", details?.name)
        assertEquals("Test Location", details?.locationString)
        assertEquals("Test Introduction", details?.introduction)
        assertEquals("test-thumbnail.jpg", details?.introductionThumbnail)
        assertEquals(listOf("Mountain", "Forest"), details?.landscape)
        assertEquals("Standard", details?.category)
        assertEquals(listOf("Road", "Walking"), details?.access)
        assertEquals(listOf("Toilets", "Water"), details?.facilities)
        assertEquals(listOf("Hiking", "Fishing"), details?.activities)
        assertEquals("Yes", details?.dogsAllowed)
        assertEquals(10, details?.poweredSites)
        assertEquals(20, details?.unpoweredSites)
        assertEquals(true, details?.isBookable)
        assertEquals("https://test.com", details?.staticLink)
        assertEquals("Test Region", details?.region)
        assertEquals("Test Place", details?.place)
        assertEquals("Open", details?.status)
        assertEquals(-41.0, details?.latitude)
        assertEquals(174.0, details?.longitude)
    }
}
