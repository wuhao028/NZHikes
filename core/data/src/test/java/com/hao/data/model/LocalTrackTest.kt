package com.hao.data.model

import org.junit.Assert.*
import org.junit.Test

class LocalTrackTest {

    @Test
    fun `validate should return true for valid track`() {
        // Given
        val validTrack = LocalTrack(
            assetId = "test-asset-id",
            name = "Test Track",
            location = "Test Location",
            distanceKm = 5.0,
            duration = "2 hours",
            difficulty = "Easy"
        )

        // When
        val isValid = validTrack.validate()

        // Then
        assertTrue(isValid)
    }

    @Test
    fun `validate should return false for invalid track with empty assetId`() {
        // Given
        val invalidTrack = LocalTrack(
            assetId = "",
            name = "Test Track",
            location = "Test Location",
            distanceKm = 5.0,
            duration = "2 hours",
            difficulty = "Easy"
        )

        // When
        val isValid = invalidTrack.validate()

        // Then
        assertFalse(isValid)
    }

    @Test
    fun `validate should return false for invalid track with negative distance`() {
        // Given
        val invalidTrack = LocalTrack(
            assetId = "test-asset-id",
            name = "Test Track",
            location = "Test Location",
            distanceKm = -1.0,
            duration = "2 hours",
            difficulty = "Easy"
        )

        // When
        val isValid = invalidTrack.validate()

        // Then
        assertFalse(isValid)
    }

    @Test
    fun `copyWithFavorite should create new track with updated favorite status`() {
        // Given
        val originalTrack = LocalTrack(
            assetId = "test-asset-id",
            name = "Test Track",
            location = "Test Location",
            distanceKm = 5.0,
            duration = "2 hours",
            difficulty = "Easy",
            isFavorite = false
        )

        // When
        val updatedTrack = originalTrack.copyWithFavorite(true)

        // Then
        assertTrue(updatedTrack.isFavorite)
        assertEquals(originalTrack.assetId, updatedTrack.assetId)
        assertEquals(originalTrack.name, updatedTrack.name)
        assertFalse(originalTrack.isFavorite) // Original should remain unchanged
    }

    @Test
    fun `copyWithDone should create new track with updated done status`() {
        // Given
        val originalTrack = LocalTrack(
            assetId = "test-asset-id",
            name = "Test Track",
            location = "Test Location",
            distanceKm = 5.0,
            duration = "2 hours",
            difficulty = "Easy",
            isDone = false
        )

        // When
        val updatedTrack = originalTrack.copyWithDone(true)

        // Then
        assertTrue(updatedTrack.isDone)
        assertEquals(originalTrack.assetId, updatedTrack.assetId)
        assertEquals(originalTrack.name, updatedTrack.name)
        assertFalse(originalTrack.isDone) // Original should remain unchanged
    }

    @Test
    fun `getFormattedDistance should return meters for distance less than 1km`() {
        // Given
        val track = LocalTrack(
            assetId = "test-asset-id",
            name = "Test Track",
            location = "Test Location",
            distanceKm = 0.5,
            duration = "2 hours",
            difficulty = "Easy"
        )

        // When
        val formattedDistance = track.getFormattedDistance()

        // Then
        assertEquals("500m", formattedDistance)
    }

    @Test
    fun `getFormattedDistance should return decimal km for distance between 1 and 10km`() {
        // Given
        val track = LocalTrack(
            assetId = "test-asset-id",
            name = "Test Track",
            location = "Test Location",
            distanceKm = 5.5,
            duration = "2 hours",
            difficulty = "Easy"
        )

        // When
        val formattedDistance = track.getFormattedDistance()

        // Then
        assertEquals("5.5km", formattedDistance)
    }

    @Test
    fun `getFormattedDistance should return integer km for distance 10km or more`() {
        // Given
        val track = LocalTrack(
            assetId = "test-asset-id",
            name = "Test Track",
            location = "Test Location",
            distanceKm = 15.7,
            duration = "2 hours",
            difficulty = "Easy"
        )

        // When
        val formattedDistance = track.getFormattedDistance()

        // Then
        assertEquals("15km", formattedDistance)
    }

    @Test
    fun `getDifficultyColor should return correct color for easy difficulty`() {
        // Given
        val track = LocalTrack(
            assetId = "test-asset-id",
            name = "Test Track",
            location = "Test Location",
            distanceKm = 5.0,
            duration = "2 hours",
            difficulty = "Easy"
        )

        // When
        val color = track.getDifficultyColor()

        // Then
        assertEquals("#4CAF50", color)
    }

    @Test
    fun `getDifficultyColor should return correct color for medium difficulty`() {
        // Given
        val track = LocalTrack(
            assetId = "test-asset-id",
            name = "Test Track",
            location = "Test Location",
            distanceKm = 5.0,
            duration = "2 hours",
            difficulty = "Medium"
        )

        // When
        val color = track.getDifficultyColor()

        // Then
        assertEquals("#FF9800", color)
    }

    @Test
    fun `getDifficultyColor should return correct color for hard difficulty`() {
        // Given
        val track = LocalTrack(
            assetId = "test-asset-id",
            name = "Test Track",
            location = "Test Location",
            distanceKm = 5.0,
            duration = "2 hours",
            difficulty = "Hard"
        )

        // When
        val color = track.getDifficultyColor()

        // Then
        assertEquals("#F44336", color)
    }

    @Test
    fun `getDifficultyColor should return default color for unknown difficulty`() {
        // Given
        val track = LocalTrack(
            assetId = "test-asset-id",
            name = "Test Track",
            location = "Test Location",
            distanceKm = 5.0,
            duration = "2 hours",
            difficulty = "Unknown"
        )

        // When
        val color = track.getDifficultyColor()

        // Then
        assertEquals("#9E9E9E", color)
    }

    @Test
    fun `createDefault should create track with default values`() {
        // Given
        val assetId = "test-asset-id"
        val name = "Test Track"

        // When
        val track = LocalTrack.createDefault(assetId, name)

        // Then
        assertEquals(assetId, track.assetId)
        assertEquals(name, track.name)
        assertEquals("", track.location)
        assertEquals(0.0, track.distanceKm, 0.01)
        assertEquals("", track.duration)
        assertEquals("", track.difficulty)
        assertEquals(0, track.imageRes)
        assertFalse(track.isFavorite)
        assertFalse(track.isDone)
    }
}
