package com.cerebrallychallenged.hypogean.modding

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class AutoMod(vararg val packages: String)
