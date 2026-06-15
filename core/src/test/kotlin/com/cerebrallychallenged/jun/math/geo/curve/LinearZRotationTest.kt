package com.cerebrallychallenged.jun.math.geo.curve

import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.degrees
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class LinearZRotationTest {

    @Test
    fun `compare rotation variants 0deg to 350deg`() {
        val from: Angle = 0.0f.degrees
        val to: Angle = 350.0f.degrees

        val shortest = Polyline.linearZRotation(from, to, speed = 1.0f, variant = Polyline.ZRotationInterpolation.SHORTEST)
        val cw = Polyline.linearZRotation(from, to, speed = 1.0f, variant = Polyline.ZRotationInterpolation.CLOCKWISE)
        val ccw = Polyline.linearZRotation(from, to, speed = 1.0f, variant = Polyline.ZRotationInterpolation.COUNTERCLOCKWISE)

        val shortestDeg = Math.toDegrees(shortest.length.toDouble())
        val cwDeg = Math.toDegrees(cw.length.toDouble())
        val ccwDeg = Math.toDegrees(ccw.length.toDouble())

        println("Lengths (deg): shortest=$shortestDeg, clockwise=$cwDeg, counterclockwise=$ccwDeg")

        // Expected: shortest is small (~10°), clockwise is large (~350°), counterclockwise is small (~10° but opposite sign represented as positive distance)
        assertTrue("shortest should be noticeably smaller than clockwise", shortestDeg + 1.0 < cwDeg)
        assertTrue("counterclockwise should be noticeably smaller than clockwise", ccwDeg + 1.0 < cwDeg)

        // sanity: clockwise should be close to ~350°, allow 5° tolerance
        assertTrue("clockwise approx 350deg", abs(cwDeg - 350.0) < 5.0)
    }
}

