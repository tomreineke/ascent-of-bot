package com.cerebrallychallenged.jun

import com.cerebrallychallenged.jun.unreal.FCommandLine

object ProgramArguments {
    private val flags: Set<String>

    private val map: Map<String, String>

    init {
        flags = mutableSetOf()
        map = mutableMapOf()
        for (part in split(FCommandLine.get())) {
            val index = part.indexOf('=')
            if (index == -1) {
                flags.add(part)
            } else {
                map[part.substring(0 until index)] = part.substring(index + 1)
            }
        }
    }

    operator fun contains(flag: String): Boolean = flag in flags

    operator fun get(key: String): String? = map[key]
}

private fun split(string: String): Sequence<String> = sequence {
    val builder = StringBuilder()
    var inQuotes = false
    for (ch in string) {
        when (ch) {
            ' ' -> {
                if (inQuotes) {
                    builder.append(ch)
                } else if (builder.isNotBlank()) {
                    yield(builder.toString())
                    builder.clear()
                }
            }
            '"' -> {
                inQuotes = !inQuotes
            }
            else -> {
                builder.append(ch)
            }
        }
    }
    if (builder.isNotBlank()) {
        yield(builder.toString())
    }
}