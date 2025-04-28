package com.cerebrallychallenged.jun.util

import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.geo.Bounds
import com.cerebrallychallenged.jun.math.geo.LineSegment2f
import com.cerebrallychallenged.jun.math.geo.Vec2f
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.ArcType

fun GraphicsContext.translate(translation: Vec2f) = translate(translation.x.toDouble(), translation.y.toDouble())

fun GraphicsContext.scale(scale: Vec2f) = scale(scale.x.toDouble(), scale.y.toDouble())

fun GraphicsContext.rotate(angle: Angle) = rotate(angle.toDegrees().toDouble())

fun GraphicsContext.fillText(text: String, position: Vec2f, maxWidth: Float = Float.POSITIVE_INFINITY) {
    if (maxWidth.isInfinite()) {
        fillText(text, position.x.toDouble(), position.y.toDouble())
    } else {
        fillText(text, position.x.toDouble(), position.y.toDouble(), maxWidth.toDouble())
    }
}

fun GraphicsContext.strokeText(text: String, position: Vec2f, maxWidth: Float = Float.POSITIVE_INFINITY) {
    if (maxWidth.isInfinite()) {
        strokeText(text, position.x.toDouble(), position.y.toDouble())
    } else {
        strokeText(text, position.x.toDouble(), position.y.toDouble(), maxWidth.toDouble())
    }
}

fun GraphicsContext.moveTo(position: Vec2f) = moveTo(position.x.toDouble(), position.y.toDouble())

fun GraphicsContext.lineTo(position: Vec2f) = lineTo(position.x.toDouble(), position.y.toDouble())

fun GraphicsContext.quadraticCurveTo(control: Vec2f, endPoint: Vec2f) =
        quadraticCurveTo(control.x.toDouble(), control.y.toDouble(), endPoint.x.toDouble(), endPoint.y.toDouble())

fun GraphicsContext.bezierCurveTo(control1: Vec2f, control2: Vec2f, endPoint: Vec2f) =
        bezierCurveTo(
                control1.x.toDouble(), control1.y.toDouble(),
                control2.x.toDouble(), control2.y.toDouble(),
                endPoint.x.toDouble(), endPoint.y.toDouble()
        )

fun GraphicsContext.arcTo(point1: Vec2f, point2: Vec2f, radius: Float) =
        arcTo(point1.x.toDouble(), point1.y.toDouble(), point2.x.toDouble(), point2.y.toDouble(), radius.toDouble())

fun GraphicsContext.arc(center: Vec2f, radius: Vec2f, startAngle: Angle, length: Float) =
        arc(
                center.x.toDouble(), center.y.toDouble(),
                radius.x.toDouble(), radius.y.toDouble(),
                startAngle.toDegrees().toDouble(),
                length.toDouble()
        )

fun GraphicsContext.rect(bounds: Bounds<Vec2f>) {
    val min = bounds.min
    val size = bounds.size
    rect(min.x.toDouble(), min.y.toDouble(), size.x.toDouble(), size.y.toDouble())
}

fun GraphicsContext.isPointInPath(point: Vec2f): Boolean = isPointInPath(point.x.toDouble(), point.y.toDouble())

fun GraphicsContext.clearRect(bounds: Bounds<Vec2f>) {
    val min = bounds.min
    val size = bounds.size
    clearRect(min.x.toDouble(), min.y.toDouble(), size.x.toDouble(), size.y.toDouble())
}

fun GraphicsContext.fillRect(bounds: Bounds<Vec2f>) {
    val min = bounds.min
    val size = bounds.size
    fillRect(min.x.toDouble(), min.y.toDouble(), size.x.toDouble(), size.y.toDouble())
}

fun GraphicsContext.strokeRect(bounds: Bounds<Vec2f>) {
    val min = bounds.min
    val size = bounds.size
    strokeRect(min.x.toDouble(), min.y.toDouble(), size.x.toDouble(), size.y.toDouble())
}

fun GraphicsContext.fillOval(bounds: Bounds<Vec2f>) {
    val min = bounds.min
    val size = bounds.size
    fillOval(min.x.toDouble(), min.y.toDouble(), size.x.toDouble(), size.y.toDouble())
}

fun GraphicsContext.strokeOval(bounds: Bounds<Vec2f>) {
    val min = bounds.min
    val size = bounds.size
    strokeOval(min.x.toDouble(), min.y.toDouble(), size.x.toDouble(), size.y.toDouble())
}

fun GraphicsContext.drawDot(position: Vec2f, size: Float, fillPaint: Paint?, strokePaint: Paint? = Color.BLACK) {
    fillPaint?.let { fill = it }
    strokePaint?.let { stroke = it }
    val bounds = Bounds.centered(position, Vec2f.ONE * size)
    strokeOval(bounds)
    fillOval(bounds)
}

fun GraphicsContext.fillArc(bounds: Bounds<Vec2f>, startAngle: Angle, arcExtent: Angle, closure: ArcType) {
    val min = bounds.min
    val size = bounds.size
    fillArc(
            min.x.toDouble(), min.y.toDouble(),
            size.x.toDouble(), size.y.toDouble(),
            startAngle.toDegrees().toDouble(),
            arcExtent.toDegrees().toDouble(),
            closure
    )
}

fun GraphicsContext.strokeArc(bounds: Bounds<Vec2f>, startAngle: Angle, arcExtent: Angle, closure: ArcType) {
    val min = bounds.min
    val size = bounds.size
    strokeArc(
            min.x.toDouble(), min.y.toDouble(),
            size.x.toDouble(), size.y.toDouble(),
            startAngle.toDegrees().toDouble(),
            arcExtent.toDegrees().toDouble(),
            closure
    )
}

fun GraphicsContext.fillRoundRect(bounds: Bounds<Vec2f>, arcSize: Vec2f) {
    val min = bounds.min
    val size = bounds.size
    fillRoundRect(
            min.x.toDouble(), min.y.toDouble(),
            size.x.toDouble(), size.y.toDouble(),
            arcSize.x.toDouble(), arcSize.y.toDouble()
    )
}

fun GraphicsContext.strokeRoundRect(bounds: Bounds<Vec2f>, arcSize: Vec2f) {
    val min = bounds.min
    val size = bounds.size
    strokeRoundRect(
            min.x.toDouble(), min.y.toDouble(),
            size.x.toDouble(), size.y.toDouble(),
            arcSize.x.toDouble(), arcSize.y.toDouble()
    )
}

fun GraphicsContext.strokeLine(startPoint: Vec2f, endPoint: Vec2f) {
    strokeLine(startPoint.x.toDouble(), startPoint.y.toDouble(), endPoint.x.toDouble(), endPoint.y.toDouble())
}

fun GraphicsContext.strokeLine(lineSegment2f: LineSegment2f) {
    strokeLine(lineSegment2f.startPoint, lineSegment2f.endPoint)
}

fun GraphicsContext.fillPolygon(points: List<Vec2f>) {
    val size = points.size
    fillPolygon(
            DoubleArray(size) { points[it].x.toDouble() },
            DoubleArray(size) { points[it].y.toDouble() },
            size
    )
}

fun GraphicsContext.fillPolygon(points: Sequence<Vec2f>) = fillPolygon(points.toList())

fun GraphicsContext.fillPolygon(points: Iterable<Vec2f>) = fillPolygon(points.toList())

fun GraphicsContext.strokePolygon(points: List<Vec2f>) {
    val size = points.size
    strokePolygon(
            DoubleArray(size) { points[it].x.toDouble() },
            DoubleArray(size) { points[it].y.toDouble() },
            size
    )
}

fun GraphicsContext.strokePolygon(points: Sequence<Vec2f>) = strokePolygon(points.toList())

fun GraphicsContext.strokePolygon(points: Iterable<Vec2f>) = strokePolygon(points.toList())

fun GraphicsContext.strokePolyline(points: List<Vec2f>) {
    val size = points.size
    strokePolyline(
            DoubleArray(size) { points[it].x.toDouble() },
            DoubleArray(size) { points[it].y.toDouble() },
            size
    )
}

fun GraphicsContext.strokePolyline(points: Sequence<Vec2f>) = strokePolyline(points.toList())

fun GraphicsContext.strokePolyline(points: Iterable<Vec2f>) = strokePolyline(points.toList())

fun GraphicsContext.drawImage(img: Image, position: Vec2f) =
        drawImage(img, position.x.toDouble(), position.y.toDouble())

fun GraphicsContext.drawImage(img: Image, destination: Bounds<Vec2f>) {
    val destMin = destination.min
    val destSize = destination.size
    drawImage(img, destMin.x.toDouble(), destMin.y.toDouble(), destSize.x.toDouble(), destSize.y.toDouble())
}

fun GraphicsContext.drawImage(img: Image, source: Bounds<Vec2f>, destination: Bounds<Vec2f>) {
    val srcMin = source.min
    val srcSize = source.size
    val destMin = destination.min
    val destSize = destination.size
    drawImage(
            img,
            srcMin.x.toDouble(), srcMin.y.toDouble(),
            srcSize.x.toDouble(), srcSize.y.toDouble(),
            destMin.x.toDouble(), destMin.y.toDouble(),
            destSize.x.toDouble(), destSize.y.toDouble()
    )
}