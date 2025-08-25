package com.hao.explore.model

import com.hao.data.data.model.RemoteTrack
import com.hao.data.model.Campsite
import com.hao.data.model.Hut
import org.junit.Assert.*
import org.junit.Test

class SearchResultTest {

    @Test
    fun `TrackResult should have correct name from track`() {
        // Given
        val track = RemoteTrack(
            assetId = "track1",
            name = "Mountain Track",
            region = listOf("Region 1"),
            x = 174.0,
            y = -41.0,
            line = listOf(listOf(listOf(174.0, -41.0)))
        )

        // When
        val trackResult = SearchResult.TrackResult(track)

        // Then
        assertEquals("Mountain Track", trackResult.name)
        assertEquals(track, trackResult.track)
    }

    @Test
    fun `CampsiteResult should have correct name from campsite`() {
        // Given
        val campsite = Campsite(
            assetId = "campsite1",
            name = "Mountain Campsite",
            region = "Region 1",
            y = -41.0,
            x = 174.0
        )

        // When
        val campsiteResult = SearchResult.CampsiteResult(campsite)

        // Then
        assertEquals("Mountain Campsite", campsiteResult.name)
        assertEquals(campsite, campsiteResult.campsite)
    }

    @Test
    fun `CampsiteResult should handle null name`() {
        // Given
        val campsite = Campsite(
            assetId = "campsite1",
            name = null,
            region = "Region 1",
            y = -41.0,
            x = 174.0
        )

        // When
        val campsiteResult = SearchResult.CampsiteResult(campsite)

        // Then
        assertEquals("", campsiteResult.name)
        assertEquals(campsite, campsiteResult.campsite)
    }

    @Test
    fun `HutResult should have correct name from hut`() {
        // Given
        val hut = Hut(
            assetId = "hut1",
            name = "Mountain Hut",
            region = "Region 1",
            y = -41.0,
            x = 174.0
        )

        // When
        val hutResult = SearchResult.HutResult(hut)

        // Then
        assertEquals("Mountain Hut", hutResult.name)
        assertEquals(hut, hutResult.hut)
    }

    @Test
    fun `HutResult should handle null name`() {
        // Given
        val hut = Hut(
            assetId = "hut1",
            name = null,
            region = "Region 1",
            y = -41.0,
            x = 174.0
        )

        // When
        val hutResult = SearchResult.HutResult(hut)

        // Then
        assertEquals("", hutResult.name)
        assertEquals(hut, hutResult.hut)
    }

    @Test
    fun `SearchResult should be sealed class with correct subtypes`() {
        // Given
        val track = RemoteTrack(
            assetId = "track1",
            name = "Test Track",
            region = listOf("Region 1"),
            x = 174.0,
            y = -41.0,
            line = listOf(listOf(listOf(174.0, -41.0)))
        )
        val campsite = Campsite(
            assetId = "campsite1",
            name = "Test Campsite",
            region = "Region 1",
            y = -41.0,
            x = 174.0
        )
        val hut = Hut(
            assetId = "hut1",
            name = "Test Hut",
            region = "Region 1",
            y = -41.0,
            x = 174.0
        )

        // When
        val trackResult = SearchResult.TrackResult(track)
        val campsiteResult = SearchResult.CampsiteResult(campsite)
        val hutResult = SearchResult.HutResult(hut)

        // Then
        assertTrue(trackResult is SearchResult)
        assertTrue(campsiteResult is SearchResult)
        assertTrue(hutResult is SearchResult)
    }

    @Test
    fun `SearchResult subtypes should have abstract name property`() {
        // Given
        val track = RemoteTrack(
            assetId = "track1",
            name = "Test Track",
            region = listOf("Region 1"),
            x = 174.0,
            y = -41.0,
            line = listOf(listOf(listOf(174.0, -41.0)))
        )
        val campsite = Campsite(
            assetId = "campsite1",
            name = "Test Campsite",
            region = "Region 1",
            y = -41.0,
            x = 174.0
        )
        val hut = Hut(
            assetId = "hut1",
            name = "Test Hut",
            region = "Region 1",
            y = -41.0,
            x = 174.0
        )

        // When
        val trackResult = SearchResult.TrackResult(track)
        val campsiteResult = SearchResult.CampsiteResult(campsite)
        val hutResult = SearchResult.HutResult(hut)

        // Then
        assertEquals("Test Track", trackResult.name)
        assertEquals("Test Campsite", campsiteResult.name)
        assertEquals("Test Hut", hutResult.name)
    }

    @Test
    fun `SearchResult should handle empty names`() {
        // Given
        val track = RemoteTrack(
            assetId = "track1",
            name = "",
            region = listOf("Region 1"),
            x = 174.0,
            y = -41.0,
            line = listOf(listOf(listOf(174.0, -41.0)))
        )
        val campsite = Campsite(
            assetId = "campsite1",
            name = "",
            region = "Region 1",
            y = -41.0,
            x = 174.0
        )
        val hut = Hut(
            assetId = "hut1",
            name = "",
            region = "Region 1",
            y = -41.0,
            x = 174.0
        )

        // When
        val trackResult = SearchResult.TrackResult(track)
        val campsiteResult = SearchResult.CampsiteResult(campsite)
        val hutResult = SearchResult.HutResult(hut)

        // Then
        assertEquals("", trackResult.name)
        assertEquals("", campsiteResult.name)
        assertEquals("", hutResult.name)
    }

    @Test
    fun `SearchResult should handle whitespace names`() {
        // Given
        val track = RemoteTrack(
            assetId = "track1",
            name = "   ",
            region = listOf("Region 1"),
            x = 174.0,
            y = -41.0,
            line = listOf(listOf(listOf(174.0, -41.0)))
        )
        val campsite = Campsite(
            assetId = "campsite1",
            name = "   ",
            region = "Region 1",
            y = -41.0,
            x = 174.0
        )
        val hut = Hut(
            assetId = "hut1",
            name = "   ",
            region = "Region 1",
            y = -41.0,
            x = 174.0
        )

        // When
        val trackResult = SearchResult.TrackResult(track)
        val campsiteResult = SearchResult.CampsiteResult(campsite)
        val hutResult = SearchResult.HutResult(hut)

        // Then
        assertEquals("   ", trackResult.name)
        assertEquals("   ", campsiteResult.name)
        assertEquals("   ", hutResult.name)
    }
}
