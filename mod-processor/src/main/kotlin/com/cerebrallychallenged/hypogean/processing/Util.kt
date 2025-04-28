package com.cerebrallychallenged.hypogean.processing

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType

internal fun Resolver.obtainClass(name: String): KSClassDeclaration =
    getClassDeclarationByName(name)
        ?: throw NoSuchElementException("""Cannot obtain class "$name"""")

internal fun KSClassDeclaration.isSubClassOf(clazz: KSType): Boolean = getAllSuperTypes().contains(clazz)
