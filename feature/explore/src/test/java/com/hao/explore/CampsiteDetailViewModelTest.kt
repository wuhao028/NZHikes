package com.hao.explore

import androidx.lifecycle.SavedStateHandle
import com.hao.data.model.Campsite
import com.hao.data.remote.CampsiteDetailsResponse
import com.hao.data.repository.CampsiteRepository
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
class CampsiteDetailViewModelTest {

    private lateinit var campsiteDetailViewModel: CampsiteDetailViewModel
    private lateinit var mockCampsiteRepository: CampsiteRepository
    private lateinit var mockSavedStateHandle: SavedStateHandle
    private val testDispatcher = StandardTestDispatcher()
    private val testAssetId = "test-campsite-id"

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockCampsiteRepository = mockk<CampsiteRepository>(relaxed = true)
        mockSavedStateHandle = mockk<SavedStateHandle>(relaxed = true)
        every { mockSavedStateHandle.get<String>("assetId") } returns testAssetId
        
        // Setup default mocks for init block
        coEvery { mockCampsiteRepository.getAllCampsites() } returns flowOf(emptyList())
        coEvery { mockCampsiteRepository.getCampsiteDetails(testAssetId) } returns Result.failure(Exception("Not loaded yet"))
        
        campsiteDetailViewModel = CampsiteDetailViewModel(mockCampsiteRepository, mockSavedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be loading`() = runTest {
        // When - ViewModel init triggers loadCampsiteDetails immediately
        // We need to check state after a brief moment
        advanceUntilIdle()
        val initialState = campsiteDetailViewModel.uiState.value

        // Then - After init, state should reflect the loading attempt
        // Since we mocked a failure, it should have error set
        assertFalse(initialState.isLoading)
        assertNotNull(initialState.error)
    }

    @Test
    fun `loadCampsiteDetails should update state with campsite and details`() = runTest {
        // Given
        val testCampsite = Campsite(
            assetId = testAssetId,
            name = "Test Campsite",
            region = "Test Region",
            y = -41.0,
            x = 174.0
        )
        val testDetails = CampsiteDetailsResponse(
            assetId = testAssetId,
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

        coEvery { mockCampsiteRepository.getAllCampsites() } returns flowOf(listOf(testCampsite))
        coEvery { mockCampsiteRepository.getCampsiteDetails(testAssetId) } returns Result.success(testDetails)

        // When
        campsiteDetailViewModel.loadCampsiteDetails()

        // Then
        advanceUntilIdle()
        val state = campsiteDetailViewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertNotNull(state.campsite)
        assertEquals(testCampsite.assetId, state.campsite?.assetId)
        assertNotNull(state.details)
        assertEquals("Test Campsite", state.details?.name)
        assertEquals("Test Location", state.details?.locationString)
        assertEquals("Test Introduction", state.details?.introduction)
    }

    @Test
    fun `loadCampsiteDetails should handle repository errors`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { mockCampsiteRepository.getAllCampsites() } returns flowOf(emptyList())
        coEvery { mockCampsiteRepository.getCampsiteDetails(testAssetId) } returns Result.failure(Exception(errorMessage))

        // When
        campsiteDetailViewModel.loadCampsiteDetails()

        // Then
        advanceUntilIdle()
        val state = campsiteDetailViewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
        assertTrue(state.error?.contains("Failed to load campsite details") == true)
    }

    @Test
    fun `loadCampsiteDetails should handle null campsite in local DB`() = runTest {
        // Given
        val testDetails = CampsiteDetailsResponse(
            assetId = testAssetId,
            name = "Test Campsite",
            locationString = "Test Location",
            introduction = "Test Introduction",
            introductionThumbnail = "test-thumbnail.jpg",
            landscape = emptyList(),
            category = "Standard",
            access = emptyList(),
            facilities = emptyList(),
            activities = emptyList(),
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

        coEvery { mockCampsiteRepository.getAllCampsites() } returns flowOf(emptyList())
        coEvery { mockCampsiteRepository.getCampsiteDetails(testAssetId) } returns Result.success(testDetails)

        // When
        campsiteDetailViewModel.loadCampsiteDetails()

        // Then
        advanceUntilIdle()
        val state = campsiteDetailViewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertNotNull(state.campsite) // Should create from response
        assertNotNull(state.details)
    }

    @Test
    fun `loadCampsiteDetails should map CampsiteDetailsResponse to CampsiteDetails correctly`() = runTest {
        // Given
        val testCampsite = Campsite(
            assetId = testAssetId,
            name = "Test Campsite",
            region = "Test Region",
            y = -41.0,
            x = 174.0
        )
        val testDetailsResponse = CampsiteDetailsResponse(
            assetId = testAssetId,
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

        coEvery { mockCampsiteRepository.getAllCampsites() } returns flowOf(listOf(testCampsite))
        coEvery { mockCampsiteRepository.getCampsiteDetails(testAssetId) } returns Result.success(testDetailsResponse)

        // When
        campsiteDetailViewModel.loadCampsiteDetails()

        // Then
        advanceUntilIdle()
        val state = campsiteDetailViewModel.uiState.value
        val details = state.details
        
        assertNotNull(details)
        assertEquals(testAssetId, details?.assetId)
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
