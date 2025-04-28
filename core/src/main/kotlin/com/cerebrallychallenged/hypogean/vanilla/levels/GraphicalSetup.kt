@file:Suppress("UnstableApiUsage")

package com.cerebrallychallenged.hypogean.vanilla.levels

import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.base.addProp
import com.cerebrallychallenged.hypogean.vanilla.props.CellArc_CaveWallArc_DirectionX_2
import com.cerebrallychallenged.hypogean.vanilla.props.CellArc_CaveWallArc_DirectionY_2
import com.cerebrallychallenged.hypogean.vanilla.props.CellBlock_CaveWall_2
import com.cerebrallychallenged.hypogean.vanilla.props.CellFloor_DirtGround
import com.cerebrallychallenged.jun.math.geo.Bounds
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.points
import com.cerebrallychallenged.jun.math.isEven
import com.cerebrallychallenged.jun.util.getResource

fun World.readLevel(
        relPath: String,
        minCoord: Vec2i,
        maxCoord: Vec2i,
        f: World.(position: Vec2i, code: String) -> Unit = { _, _ -> }
) {
    val levelText = getResource(relPath).readText()
    setupGraphical(Bounds.of(minCoord, maxCoord), levelText) { position, code ->
        when (code) {
            "▓▓▓▓" -> {
                addProp(::CellBlock_CaveWall_2, position)
            }
            " ·· " -> {
                addProp(::CellFloor_DirtGround, position)
            }
            "xΠΠx" -> {
                addProp(::CellFloor_DirtGround, position)
                addProp(::CellArc_CaveWallArc_DirectionX_2, position)
            }
            "yΠΠy" -> {
                addProp(::CellFloor_DirtGround, position)
                addProp(::CellArc_CaveWallArc_DirectionY_2, position)
            }
            else -> f(position, code)
        }
    }
}

fun World.setupGraphical(bounds: Bounds<Vec2i>, string: String, f: World.(position: Vec2i, code: String) -> Unit) {
    val min = bounds.min
    val size = bounds.size
    val parts = string
            .trim()
            .split('\n')
            .asSequence()
            .drop(2)
            .filterIndexed { index, _ -> index.isEven }
            .map { it.substringAfter('┊').split('┊').dropLast(1) }
            .toList()
    require(parts.size == size.y && parts.all { it.size == size.x })
    for (position in bounds.points) {
        f(position, parts[position.y - min.y][position.x - min.x])
    }
}
