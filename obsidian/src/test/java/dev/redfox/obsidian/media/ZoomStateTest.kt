package dev.redfox.obsidian.media

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import org.junit.Assert.assertEquals
import org.junit.Test

class ZoomStateTest {

    private fun state() = ZoomState().apply { size = Size(1000f, 2000f) }

    /** Where content point [p] (relative to the centre, unscaled) lands on screen. */
    private fun ZoomState.screen(p: Offset) = Offset(500f, 1000f) + offset + p * scale

    @Test
    fun `pinching at the centre zooms without moving`() {
        val z = state()
        z.transform(zoom = 2f, pan = Offset.Zero, centroid = Offset(500f, 1000f))
        assertEquals(2f, z.scale, 0.001f)
        assertEquals(Offset.Zero, z.offset)
    }

    @Test
    fun `the point under the fingers stays under them`() {
        val z = state()
        val fingers = Offset(700f, 1300f)
        val under = (fingers - Offset(500f, 1000f)) / 1f
        z.transform(zoom = 2f, pan = Offset.Zero, centroid = fingers)
        val landed = z.screen(under)
        assertEquals(fingers.x, landed.x, 0.5f)
        assertEquals(fingers.y, landed.y, 0.5f)
    }

    @Test
    fun `zoom is held between 1x and the maximum`() {
        val z = state()
        z.transform(zoom = 0.5f, pan = Offset.Zero, centroid = Offset(500f, 1000f))
        assertEquals(1f, z.scale, 0.001f)
        z.transform(zoom = 20f, pan = Offset.Zero, centroid = Offset(500f, 1000f))
        assertEquals(5f, z.scale, 0.001f)
    }

    @Test
    fun `panning stops at the content's edges`() {
        val z = state()
        z.transform(zoom = 2f, pan = Offset.Zero, centroid = Offset(500f, 1000f))
        z.transform(zoom = 1f, pan = Offset(5000f, -5000f), centroid = Offset(500f, 1000f))
        // At 2x the content overhangs by half the view on each side.
        assertEquals(500f, z.offset.x, 0.5f)
        assertEquals(-1000f, z.offset.y, 0.5f)
    }

    @Test
    fun `at 1x nothing pans`() {
        val z = state()
        z.transform(zoom = 1f, pan = Offset(300f, 300f), centroid = Offset(500f, 1000f))
        assertEquals(Offset.Zero, z.offset)
    }
}
