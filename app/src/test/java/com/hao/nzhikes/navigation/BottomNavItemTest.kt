package com.hao.nzhikes.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import org.junit.Assert.*
import org.junit.Test
import com.hao.nzhikes.R

class BottomNavItemTest {

    @Test
    fun `Home should have correct properties`() {
        // When
        val home = BottomNavItem.Home

        // Then
        assertEquals("home", home.route)
        assertEquals(Icons.Default.Home, home.icon)
        assertEquals(R.string.nav_home, home.titleRes)
    }

    @Test
    fun `Trips should have correct properties`() {
        // When
        val trips = BottomNavItem.Trips

        // Then
        assertEquals("trips", trips.route)
        assertEquals(Icons.Default.Place, trips.icon)
        assertEquals(R.string.nav_trips, trips.titleRes)
    }

    @Test
    fun `Me should have correct properties`() {
        // When
        val me = BottomNavItem.Me

        // Then
        assertEquals("me", me.route)
        assertEquals(Icons.Default.Person, me.icon)
        assertEquals(R.string.nav_me, me.titleRes)
    }

    @Test
    fun `Search should have correct properties`() {
        // When
        val search = BottomNavItem.Search

        // Then
        assertEquals("search", search.route)
        assertEquals(Icons.Default.Search, search.icon)
        assertEquals(R.string.nav_search, search.titleRes)
    }

    @Test
    fun `all navigation items should be unique`() {
        // Given
        val items = listOf(
            BottomNavItem.Home,
            BottomNavItem.Trips,
            BottomNavItem.Me,
            BottomNavItem.Search
        )

        // When
        val routes = items.map { it.route }
        val titles = items.map { it.titleRes }

        // Then
        assertEquals(routes.size, routes.distinct().size)
        assertEquals(titles.size, titles.distinct().size)
    }

    @Test
    fun `all navigation items should have non-empty properties`() {
        // Given
        val items = listOf(
            BottomNavItem.Home,
            BottomNavItem.Trips,
            BottomNavItem.Me,
            BottomNavItem.Search
        )

        // Then
        items.forEach { item ->
            assertTrue("Route should not be empty", item.route.isNotEmpty())
            assertTrue("Title resource should be valid", item.titleRes != 0)
            assertNotNull("Icon should not be null", item.icon)
        }
    }

    @Test
    fun `navigation items should be sealed class instances`() {
        // When
        val home = BottomNavItem.Home
        val trips = BottomNavItem.Trips
        val me = BottomNavItem.Me
        val search = BottomNavItem.Search

        // Then
        assertTrue(home is BottomNavItem)
        assertTrue(trips is BottomNavItem)
        assertTrue(me is BottomNavItem)
        assertTrue(search is BottomNavItem)
    }

    @Test
    fun `navigation items should have correct route patterns`() {
        // Given
        val items = listOf(
            BottomNavItem.Home,
            BottomNavItem.Trips,
            BottomNavItem.Me,
            BottomNavItem.Search
        )

        // Then
        items.forEach { item ->
            assertTrue("Route should be lowercase", item.route.all { it.isLowerCase() || it.isDigit() })
            assertFalse("Route should not contain spaces", item.route.contains(" "))
            assertFalse("Route should not contain special characters", item.route.any { !it.isLetterOrDigit() && it != '_' && it != '-' })
        }
    }

    @Test
    fun `navigation items should have valid title resources`() {
        // Given
        val items = listOf(
            BottomNavItem.Home,
            BottomNavItem.Trips,
            BottomNavItem.Me,
            BottomNavItem.Search
        )

        // Then
        items.forEach { item ->
            assertTrue("Title resource should be valid", item.titleRes != 0)
        }
    }

    @Test
    fun `navigation items should have appropriate icons`() {
        // Then
        assertEquals("Home should have home icon", Icons.Default.Home, BottomNavItem.Home.icon)
        assertEquals("Trips should have place icon", Icons.Default.Place, BottomNavItem.Trips.icon)
        assertEquals("Me should have person icon", Icons.Default.Person, BottomNavItem.Me.icon)
        assertEquals("Search should have search icon", Icons.Default.Search, BottomNavItem.Search.icon)
    }
}
