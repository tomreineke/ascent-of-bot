package com.cerebrallychallenged.jun.math.geo.curve

import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.geo.LinearInterpolator
import com.cerebrallychallenged.jun.math.geo.Metric
import com.cerebrallychallenged.jun.math.geo.Quaternion
import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.Vec4f
import com.cerebrallychallenged.jun.math.geo.absDiff
import com.cerebrallychallenged.jun.math.geo.readInterpolatingFunction
import com.cerebrallychallenged.jun.math.geo.readQuaternion
import com.cerebrallychallenged.jun.math.geo.readVec2f
import com.cerebrallychallenged.jun.math.geo.readVec3f
import com.cerebrallychallenged.jun.math.geo.readVec4f
import com.cerebrallychallenged.jun.math.geo.writeInterpolatingFunction
import com.cerebrallychallenged.jun.math.geo.writeQuaternion
import com.cerebrallychallenged.jun.math.geo.writeVec2f
import com.cerebrallychallenged.jun.math.geo.writeVec3f
import com.cerebrallychallenged.jun.math.geo.writeVec4f
import com.cerebrallychallenged.jun.math.interpolate
import com.cerebrallychallenged.jun.math.readAngle
import com.cerebrallychallenged.jun.math.writeAngle
import java.io.DataInput
import java.io.DataOutput

inline fun <T> DataOutput.writePolyline(polyline: Polyline<T>, writePoint: DataOutput.(T) -> Unit) {
    writeInterpolatingFunction(polyline.function, writePoint)
    writeFloat(polyline.startTime)
    writeFloat(polyline.endTime)
    writePoint(polyline.startPoint)
    writePoint(polyline.endPoint)
    writeFloat(polyline.length)
}

inline fun <T> DataInput.readPolyline(
        noinline interpolator: LinearInterpolator<T>,
        noinline metric: Metric<T>,
        readPoint: DataInput.() -> T
) = Polyline(
        readInterpolatingFunction(interpolator, readPoint),
        readFloat(),
        readFloat(),
        readPoint(),
        readPoint(),
        readFloat(),
        metric
)

fun DataOutput.writePolylineFloat(polyline: Polyline<Float>) {
    writePolyline(polyline) { writeFloat(it) }
}

fun DataOutput.writePolylineAngle(polyline: Polyline<Angle>) {
    writePolyline(polyline) { writeAngle(it) }
}

fun DataOutput.writePolylineVec2f(polyline: Polyline<Vec2f>) {
    writePolyline(polyline) { writeVec2f(it) }
}

fun DataOutput.writePolylineVec3f(polyline: Polyline<Vec3f>) {
    writePolyline(polyline) { writeVec3f(it) }
}

fun DataOutput.writePolylineVec4f(polyline: Polyline<Vec4f>) {
    writePolyline(polyline) { writeVec4f(it) }
}

fun DataOutput.writePolylineQuaternion(polyline: Polyline<Quaternion>) {
    writePolyline(polyline) { writeQuaternion(it) }
}

fun DataInput.readPolylineFloat(): Polyline<Float> = readPolyline(::interpolate, ::absDiff) { readFloat() }

fun DataInput.readPolylineAngle(): Polyline<Angle> = readPolyline(::interpolate, ::absDiff) { readAngle() }

fun DataInput.readPolylineVec2f(): Polyline<Vec2f> = readPolyline(Vec2f::interpolate, Vec2f::distanceTo) { readVec2f() }

fun DataInput.readPolylineVec3f(): Polyline<Vec3f> = readPolyline(Vec3f::interpolate, Vec3f::distanceTo) { readVec3f() }

fun DataInput.readPolylineVec4f(): Polyline<Vec4f> = readPolyline(Vec4f::interpolate, Vec4f::distanceTo) { readVec4f() }

fun DataInput.readPolylineQuaternion(): Polyline<Quaternion>
        = readPolyline(Quaternion::interpolate, Quaternion::angularDistanceTo) { readQuaternion() }
