package com.cerebrallychallenged.jun.clipper

import com.cerebrallychallenged.jun.math.geo.*
import com.cerebrallychallenged.jun.unreal.AnyRef
import com.cerebrallychallenged.jun.unreal.TSharedRef
import com.cerebrallychallenged.jun.util.*
import com.cerebrallychallenged.jun.util.buffers.get
import com.cerebrallychallenged.jun.util.buffers.view
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import java.io.File

val colors = listOf(Color.BLACK, Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.PURPLE, Color.CYAN)

fun GraphicsContext.arrow(s: Vec2f, t: Vec2f) {
    val center = s.interpolate(0.5f, t)
    val dir = (t - s).normalized() * 1.5f
    val orthoDir = dir.turnClockwise()
    moveTo(s)
    lineTo(t)
    moveTo(center - dir + 2 * orthoDir)
    lineTo(center + dir)
    moveTo(center - dir - 2 * orthoDir)
    lineTo(center + dir)
}

fun GraphicsContext.drawPaths(paths: AnyRef<Paths>, isClosed: Boolean, scale: Float) {
    for (index in 0 until paths.size) {
        beginPath()
        stroke = colors[index.toInt()]
        val points = paths[index].toBuffer().view().map { it * scale }
        fill = Color.BLACK
        drawDot(points[0], 3.0f, Color.WHITE)
        for ((s, t) in points.asSequence().zipWithNext()) {
            arrow(s, t)
            drawDot(t, 2.0f, Color.BLACK)
        }
        if (isClosed) {
            arrow(points.last(), points.first())
        }
        stroke()
    }
}

suspend fun TSharedRef<Paths>.produceImage(
        size: Vec2d,
        scale: Float,
        imageScale: Float,
        fileName: String
) {
    File(fileName).writeImage(buildImage(size) {
        drawPaths(this@produceImage, true, imageScale / scale)
    })
}


suspend fun TSharedRef<PolyTree>.produceImage(
        size: Vec2d,
        scale: Float,
        steinerPoints: Collection<Vec2f>,
        imageScale: Float,
        fileName: String
) {
    File(fileName).writeImage(buildImage(size) {
        stroke = Color.RED
        fill = Color.RED
        for (steinerPoint in steinerPoints) {
            fillOval(Bounds.centered(steinerPoint * imageScale, vec(2f, 2f)))
        }
        stroke = Color.BLACK
        fill = Color.TRANSPARENT
        drawPaths(openPaths, false, imageScale / scale)
        drawPaths(closedPaths, true, imageScale / scale)
        val (vertices, triangles) = triangulate(scale, steinerPoints)
        stroke = Color.BLUE
        for (triangle in triangles.view()) {
            val v0 = vertices[triangle.x].xy * imageScale
            val v1 = vertices[triangle.y].xy * imageScale
            val v2 = vertices[triangle.z].xy * imageScale
            beginPath()
            moveTo(v0)
            lineTo(v1)
            lineTo(v2)
            lineTo(v0)
            stroke()
        }
    })
}
