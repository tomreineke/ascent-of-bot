package com.cerebrallychallenged.jun.unreal

object FPaths {
    val projectConfigDir = getProjectConfigDir()

    val projectContentDir = getProjectContentDir()

    val projectSavedDir = getProjectSavedDir()

    val projectUserDir = getProjectUserDir()
}

external fun getProjectConfigDir(): String

external fun getProjectContentDir(): String

external fun getProjectSavedDir(): String

external fun getProjectUserDir(): String
