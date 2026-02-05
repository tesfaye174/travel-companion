package com.travelcompanion.ui.home

import org.junit.Assert.*
import org.junit.Test

class SuggestedDestinationsTest {

    @Test
    fun suggestedDestinations_haveUniqueIds_andNonEmptyNames() {
        val destinations = SuggestedDestinations.destinations
        val ids = destinations.map { it.id }
        val uniqueIds = ids.toSet()
        assertEquals("IDs must be unique", ids.size, uniqueIds.size)

        for (d in destinations) {
            assertTrue("City must not be empty for id=${d.id}", d.city.isNotBlank())
            assertTrue("Country must not be empty for id=${d.id}", d.country.isNotBlank())
        }
    }
}
