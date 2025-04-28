package com.cerebrallychallenged.jun.unreal

object FPlatformProcess {
    val baseDir: String = getBaseDir()
}

private external fun getBaseDir(): String