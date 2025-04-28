package com.cerebrallychallenged.hypogean.level

import com.cerebrallychallenged.hypogean.vanilla.levels.setupGraphical
import com.cerebrallychallenged.jun.math.geo.*
import com.cerebrallychallenged.jun.util.ArrayND
import com.cerebrallychallenged.jun.util.get
import com.cerebrallychallenged.jun.util.size
import java.io.File
import kotlin.math.max

/**
 * Prints a grid that can be used as a raw literal String for [setupGraphical].
 * Example:
 *
 *     java GraphicalSetupGeneratorKt -13 -10 12 10
 *
 * prints an empty grid for the bounds Bounds.of(vec(-13, -10), vec(12, 10)).
 *
 *     java GraphicalSetupGeneratorKt -13 -10 12 10 entries.txt patterns.txt
 *
 * prints a grid that is filled according to the file `entries.txt`.
 * The file `entries.txt` is parsed by the regex patterns of the file `patterns.txt`.
 * Note that the character `'$'` is replaced by a regex for a group capturing an integer coordinate.
 * If, for example, `patterns.txt` contains a line
 *
 *     "▓▓▓▓" = create\(::CellBrickWallBlock, vec\($, $\)\)
 *
 * then a line
 *
 *     create(::CellBrickWallBlock, vec(4, -3))
 *
 * in `entries.txt` leads to the entry `"▓▓▓▓"` in the grid at position `(4, -3)`.
 */
fun main(args: Array<String>) {
    println(generate(
            Bounds.of(vec(args[0].toInt(), args[1].toInt()), vec(args[2].toInt(), args[3].toInt())),
            args.getOrNull(4)?.let { path ->
                File(path).readLines().map { it.trim() }
            } ?: listOf(),
            args.getOrNull(5)?.let { path ->
                File(path)
                        .readLines()
                        .asSequence()
                        .map { it.trim() }
                        .filterNot { it.isBlank() || it.startsWith("#") }
                        .map { createPattern(it) }
                        .toList()
            } ?: listOf()
    ))
}

private val PATTERN_PATTERN = """"(....)" = (.*)""".toRegex()

private fun createPattern(line: String): Pair<Regex, String> {
    val matchResult =
            PATTERN_PATTERN.matchEntire(line) ?: error("""Line "$line" does not describe a pattern.""")
    return (
            matchResult.groupValues[2].replace("$", """([+-]?\d+)""").toRegex()
                    to matchResult.groupValues[1]
    )
}

private fun generate(bounds: Bounds<Vec2i>, entries: List<String>, patterns: List<Pair<Regex, String>>): String {
    val stringMatrix = ArrayND.create(bounds) { "    " }
    for ((lineIndex, line) in
            entries.asSequence().withIndex().filterNot { (_, line) -> line.isBlank() }
    ) {
        val found = patterns.find { (regex, code) ->
            val match = regex.matchEntire(line)
            if (match != null) {
                val position = vec(match.groupValues[1].toInt(), match.groupValues[2].toInt())
                stringMatrix[position] = code
            }
            match != null
        } != null
        if (!found) {
            println("Warning: No regex pattern matched line ${lineIndex + 1}: $line")
        }
    }
    val rangeX = bounds.rangeX
    val rowLabelWidth = max(rangeX.first.toString().length, rangeX.last.toString().length)
    return buildString {
        append(" ".repeat(rowLabelWidth + 3))
        for (column in bounds.rangeX) {
            val columnString = column.toString()
            val len = columnString.length
            val leftSpace = if (columnString.first() == '-') (4 - len) / 2 else (5 - len) / 2
            val rightSpace = 4 - len - leftSpace
            append(" ".repeat(leftSpace))
            append(columnString)
            append(" ".repeat(rightSpace + 1))
        }
        fun appendBar() {
            append('\n')
            append(" ".repeat(rowLabelWidth + 2))
            append('+')
            repeat(bounds.rangeX.size) { append("----+") }
            append('\n')
        }
        appendBar()
        for (row in bounds.rangeY) {
            append(' ')
            append("%${rowLabelWidth}d".format(row))
            append(" ┊")
            for (column in bounds.rangeX) {
                append(stringMatrix[column, row])
                append('┊')
            }
            appendBar()
        }
    }
}