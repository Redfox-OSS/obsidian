package dev.redfox.obsidian.components

import org.junit.Assert.assertEquals
import org.junit.Test

class InitialsTest {

    @Test
    fun `first letters of the first two words`() {
        assertEquals("AL", initials("Ada Lovelace"))
        assertEquals("GB", initials("Grace Brewster Hopper"))
        assertEquals("JL", initials("jean-luc"))
    }

    @Test
    fun `one word gives one letter`() = assertEquals("A", initials("Ada"))

    @Test
    fun `nothing usable gives nothing, so the fallback icon shows`() {
        assertEquals("", initials(""))
        assertEquals("", initials("  "))
        assertEquals("", initials("+91 *"))
    }

    @Test
    fun `digits count`() = assertEquals("94", initials("98765 43210"))
}
