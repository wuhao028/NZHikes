package com.hao.ui.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class ThemeManagerTest {

    private lateinit var themeManager: ThemeManager
    private lateinit var mockContext: Context
    private lateinit var mockDataStore: DataStore<Preferences>
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockContext = mockk<Context>(relaxed = true)
        mockDataStore = mockk<DataStore<Preferences>>(relaxed = true)
        
        // Mock the dataStore property
        every { mockContext.dataStore } returns mockDataStore
        
        themeManager = ThemeManager(mockContext)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `isDarkMode should be false when initialized`() = runTest {
        // Given
        val preferences = mockk<Preferences>()
        every { preferences[booleanPreferencesKey("dark_mode")] } returns null
        every { mockDataStore.data } returns flowOf(preferences)

        // When
        themeManager.initialize()

        // Then
        assertFalse(themeManager.isDarkMode)
    }

    @Test
    fun `isDarkMode should be true when DataStore returns true`() = runTest {
        // Given
        val preferences = mockk<Preferences>()
        every { preferences[booleanPreferencesKey("dark_mode")] } returns true
        every { mockDataStore.data } returns flowOf(preferences)

        // When
        themeManager.initialize()

        // Then
        assertTrue(themeManager.isDarkMode)
    }

    @Test
    fun `isDarkMode should default to false when DataStore throws exception`() = runTest {
        // Given
        every { mockDataStore.data } throws IOException("Test exception")

        // When
        themeManager.initialize()

        // Then
        assertFalse(themeManager.isDarkMode)
    }

    @Test
    fun `updateDarkMode should update DataStore and isDarkMode`() = runTest {
        // Given
        val preferences = mockk<Preferences>()
        coEvery { mockDataStore.edit(any()) } returns preferences

        // When
        themeManager.updateDarkMode(true)

        // Then
        assertTrue(themeManager.isDarkMode)
        coVerify { mockDataStore.edit(any()) }
    }

    @Test
    fun `updateDarkMode should set false correctly`() = runTest {
        // Given
        val preferences = mockk<Preferences>()
        coEvery { mockDataStore.edit(any()) } returns preferences

        // When
        themeManager.updateDarkMode(false)

        // Then
        assertFalse(themeManager.isDarkMode)
        coVerify { mockDataStore.edit(any()) }
    }

    @Test
    fun `updateDarkMode should throw ThemeManagerException when exception occurs`() = runTest {
        // Given
        coEvery { mockDataStore.edit(any()) } throws IOException("Test exception")

        // When & Then
        val exception = assertThrows(ThemeManagerException::class.java) {
            themeManager.updateDarkMode(true)
        }
        assertEquals("Failed to save theme preference", exception.message)
        assertTrue(exception.cause is IOException)
    }

    @Test
    fun `toggleDarkMode should toggle current state`() = runTest {
        // Given
        val preferences = mockk<Preferences>()
        coEvery { mockDataStore.edit(any()) } returns preferences
        themeManager.updateDarkMode(false) // Set initial state to false

        // When
        themeManager.toggleDarkMode()

        // Then
        assertTrue(themeManager.isDarkMode)
    }

    @Test
    fun `toggleDarkMode should change from true to false`() = runTest {
        // Given
        val preferences = mockk<Preferences>()
        coEvery { mockDataStore.edit(any()) } returns preferences
        themeManager.updateDarkMode(true) // Set initial state to true

        // When
        themeManager.toggleDarkMode()

        // Then
        assertFalse(themeManager.isDarkMode)
    }

    @Test
    fun `darkModeFlow should emit correct value`() = runTest {
        // Given
        val preferences = mockk<Preferences>()
        every { preferences[booleanPreferencesKey("dark_mode")] } returns true
        every { mockDataStore.data } returns flowOf(preferences)

        // When & Then
        themeManager.darkModeFlow.test {
            val value = awaitItem()
            assertTrue(value)
            awaitComplete()
        }
    }

    @Test
    fun `darkModeFlow should emit false when value is null`() = runTest {
        // Given
        val preferences = mockk<Preferences>()
        every { preferences[booleanPreferencesKey("dark_mode")] } returns null
        every { mockDataStore.data } returns flowOf(preferences)

        // When & Then
        themeManager.darkModeFlow.test {
            val value = awaitItem()
            assertFalse(value)
            awaitComplete()
        }
    }

    @Test
    fun `darkModeFlow should handle exception when DataStore throws`() = runTest {
        // Given
        every { mockDataStore.data } throws IOException("Test exception")

        // When & Then
        themeManager.darkModeFlow.test {
            // Should not emit any value as exception is handled in catch block
            awaitComplete()
        }
    }

    @Test
    fun `ThemeManagerException should contain correct message and cause`() {
        // Given
        val cause = IOException("Test cause")
        val message = "Test message"

        // When
        val exception = ThemeManagerException(message, cause)

        // Then
        assertEquals(message, exception.message)
        assertEquals(cause, exception.cause)
    }
}
