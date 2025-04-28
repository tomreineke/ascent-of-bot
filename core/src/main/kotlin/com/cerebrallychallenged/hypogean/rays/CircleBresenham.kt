package com.cerebrallychallenged.hypogean.rays

import com.cerebrallychallenged.hypogean.Heading
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.vec

fun circleBresenham(xc: Int, yc: Int, r: Int): Sequence<Vec2i> {

    fun getCirclePoints(xc: Int, yc: Int, x: Int, y: Int ): MutableSet<Vec2i> = mutableSetOf(
        vec(xc+x, yc+y),
        vec(xc-x, yc+y),
        vec(xc+x, yc-y),
        vec(xc-x, yc-y),
        vec(xc+y, yc+x),
        vec(xc-y, yc+x),
        vec(xc+y, yc-x),
        vec(xc-y, yc-x)
    )

    var x = 0
    var y = r
    var d = 3 - 2 * r
    val result = getCirclePoints(xc, yc, x, y)

    while (y >= x) {
        x++
        d = if (d > 0) {
            y--
            d + 4 * (x - y) + 10
        } else {
            d + 4 * x + 6
        }
        result.addAll(getCirclePoints(xc, yc, x, y))
    }
    return result.asSequence()
}

private fun baseCircleBresenham(radius: Int, fromHeading: Heading, toHeading: Heading): Sequence<Vec2i> = sequence {
    val segment = buildList {
        var x = 0
        var y = radius
        var d = 3 - 2 * radius
        while (y >= x) {
            add(vec(x, y))
            x++
            d += if (d > 0) {
                y--
                4 * (x - y) + 10
            } else {
                4 * x + 6
            }
        }
    }
    val allHeadings = Heading.entries.toTypedArray()
    var currentHeading = fromHeading
    do {
        var dropCount = 0
        yieldAll(segment.asSequence().drop(1).map { point ->
            dropCount = if (point.x == point.y) 1 else 0
            currentHeading.transform(point.yx)
        })
        yieldAll(segment.asReversed().asSequence().drop(dropCount).map(currentHeading::transform))
        currentHeading = allHeadings[(currentHeading.ordinal + 1) % 4]
    } while (currentHeading != toHeading)
}

fun circleBresenham(center: Vec2i, radius: Int, fromHeading: Heading, toHeading: Heading, clockwise: Boolean): Sequence<Vec2i> {
    return if (clockwise) {
        baseCircleBresenham(radius, fromHeading, toHeading).map { it + center }
    } else {
        baseCircleBresenham(radius, fromHeading.flipX(), toHeading.flipX()).map { vec(it.x, -it.y) + center }
    }
}

fun circleSegmentBresenham(radius: Int): List<Vec2i> = buildList {
    var x = 0
    var y = radius
    var d = 3 - 2 * radius
    while (y >= x) {
        x++
        d += if (d > 0) {
            y--
            4 * (x - y) + 10
        } else {
            4 * x + 6
        }
        add(vec(x, y))
    }
}