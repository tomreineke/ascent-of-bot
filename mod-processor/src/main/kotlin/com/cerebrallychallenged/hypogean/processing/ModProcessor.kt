package com.cerebrallychallenged.hypogean.processing

import com.cerebrallychallenged.hypogean.modding.AutoMod
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.FileLocation
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo
import java.util.TreeSet
import kotlin.io.path.Path
import kotlin.io.path.useLines

class ModProcessor(environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private val logger = environment.logger

    private val codeGenerator = environment.codeGenerator

    private val allPackages: TreeSet<String> = TreeSet()

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.warn("Hello Processor World")
        for (file in resolver.getAllFiles()) {
            allPackages.add(file.packageName.asString())
        }

//        for (pck in allPackages) {
//            logger.warn("--- $pck")
//        }

        try {
            codeGenerator.createNewFile(Dependencies.ALL_FILES, "com.cerebrallychallenged.hypogean", "wuff", "txt").bufferedWriter().use {
                it.appendLine("This is fine.")
            }
        } catch (e: FileAlreadyExistsException) {
        }
        val modInterface = resolver.obtainClass("com.cerebrallychallenged.hypogean.modding.Mod")

        val annotatedModClasses =
            resolver.getSymbolsWithAnnotation("com.cerebrallychallenged.hypogean.modding.AutoMod")
                .filterIsInstance<KSClassDeclaration>()
                .filter { it.isSubClassOf(modInterface.asType(listOf())) }
        logger.warn("KSP START")
        for (modClass in annotatedModClasses) {
            processMod(modClass, resolver)
        }
        logger.warn("KSP END")
//        resolver.
//        logger.warn("modClass=$modClass")
//        for (file in resolver.getAllFiles()) {
//            for (declaration in file.declarations.filterIsInstance<KSClassDeclaration>()) {
////                declaration.getAllSuperTypes()
//                //declaration.
//            }
//        }

        return listOf()
    }

    @OptIn(KspExperimental::class)
    private fun processMod(modClass: KSClassDeclaration, resolver: Resolver) {
        logger.warn("- $modClass")
        for (packageToScan in modClass.getAnnotationsByType(AutoMod::class).flatMap { it.packages.toList() }.distinct()) {
            for (subPackage in allPackages.tailSet(packageToScan)) {
                if (!subPackage.startsWith(packageToScan)) break
                for (declaration in resolver.getDeclarationsFromPackage(subPackage)) {
                    if (declaration is KSPropertyDeclaration) {
                        val location = declaration.location
                        if (location is FileLocation) {

                            Path(location.filePath).useLines {
                                if (it.take(location.lineNumber).last().contains("by attribute")) {
                                    logger.warn("  ATTR $declaration")
                                }
                            }
                        }
//                        val dd: KSPropertyDeclaration = declaration
//                        logger.warn("  * $dd")
//                        val getter = dd.getter
//                        if (getter != null) {
//                            logger.warn("    * $getter")
//                            logger.warn("      # ${getter.location}")
//                            logger.warn("      # ${getter.origin}")
//                            logger.warn("      # ${getter.parent}")
//                        }
//                        //dd.getter!!.
//                        dd.isDelegated()
                    }
//                    logger.warn("     * $declaration")
                    declaration
                }
            }
        }

        val modPackage = modClass.packageName
        val modClassAutoName = "${modClass.simpleName.getShortName()}Auto"
        FileSpec.builder(modPackage.asString(), modClassAutoName).also { fileSpec ->
            fileSpec.addType(TypeSpec.objectBuilder(modClassAutoName).also { autoMod ->
                
            }.build())
        }.build().writeTo(codeGenerator, aggregating = true, listOf())
    }
}
