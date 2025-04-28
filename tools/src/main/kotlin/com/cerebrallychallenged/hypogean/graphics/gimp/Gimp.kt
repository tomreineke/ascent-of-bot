package com.cerebrallychallenged.hypogean.graphics.gimp

import java.lang.ProcessBuilder.Redirect
import java.nio.file.Paths

private val ExecutablePath = Paths.get("tools", "GIMP 2", "bin", "gimp-2.10.exe").toAbsolutePath()

class Gimp : GimpContext {
    private val script: StringBuilder = StringBuilder()

    override var indentation: Int = 0

    private var nextVarIndex: Int = 0

    internal fun obtainVarIndex(): Int = nextVarIndex.also { ++nextVarIndex }

    override val gimp: Gimp
        get() = this

    override fun append(line: String) {
        isEmpty = false
        script.appendLine("${"    ".repeat(indentation)}$line")
    }

    override fun toString(): String = script.toString()

    var isEmpty: Boolean = true
        private set

    fun execute() {
        val file = createTempFile(suffix = ".py")
        file.deleteOnExit()
        val stem = file.nameWithoutExtension
        println(file.absolutePath)
        println(stem)
        file.writeText(script.toString())
        val process = ProcessBuilder(
            ExecutablePath.toString(),
            "-i",
            "--batch-interpreter",
            "python-fu-eval",
            "-b",
            """
                import sys
                sys.path.append('${file.absoluteFile.parent.escapePython()}')
                import $stem
            """.trimIndent(),
            "-b",
            "pdb.gimp_quit(1)"
        ).run {
            redirectOutput(Redirect.INHERIT)
            redirectError(Redirect.INHERIT)
            start()
        }
        process.waitFor()
    }

    init {
        script.appendLine("from gimpfu import *")
    }
}

fun buildGimp(block: GimpContext.() -> Unit): Gimp = Gimp().apply(block)