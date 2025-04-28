package com.cerebrallychallenged.jun.unreal

class FCommandLine {
    companion object {
        @JvmStatic
        fun get(): String = getImpl()
    }
}

private external fun getImpl(): String