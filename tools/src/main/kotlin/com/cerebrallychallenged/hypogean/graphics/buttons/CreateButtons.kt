package com.cerebrallychallenged.hypogean.graphics.buttons

import com.cerebrallychallenged.hypogean.graphics.gimp.BevelingStyle
import com.cerebrallychallenged.hypogean.graphics.gimp.CombinationMode
import com.cerebrallychallenged.hypogean.graphics.gimp.EmbossingDirection
import com.cerebrallychallenged.hypogean.graphics.gimp.GimpChannelOp
import com.cerebrallychallenged.hypogean.graphics.gimp.GimpColor
import com.cerebrallychallenged.hypogean.graphics.gimp.GimpComponent
import com.cerebrallychallenged.hypogean.graphics.gimp.GimpContext
import com.cerebrallychallenged.hypogean.graphics.gimp.GimpImage
import com.cerebrallychallenged.hypogean.graphics.gimp.GimpLayer
import com.cerebrallychallenged.hypogean.graphics.gimp.GimpSvgPaths
import com.cerebrallychallenged.hypogean.graphics.gimp.MergeType
import com.cerebrallychallenged.hypogean.graphics.gimp.ScaleInterpolation
import com.cerebrallychallenged.hypogean.graphics.gimp.bevelEmboss
import com.cerebrallychallenged.hypogean.graphics.gimp.brightnessContrast
import com.cerebrallychallenged.hypogean.graphics.gimp.buildGimp
import com.cerebrallychallenged.hypogean.graphics.gimp.crop
import com.cerebrallychallenged.hypogean.graphics.gimp.div
import com.cerebrallychallenged.hypogean.graphics.gimp.editClear
import com.cerebrallychallenged.hypogean.graphics.gimp.fileLoad
import com.cerebrallychallenged.hypogean.graphics.gimp.filePngLoad
import com.cerebrallychallenged.hypogean.graphics.gimp.fileSvgLoad
import com.cerebrallychallenged.hypogean.graphics.gimp.isNotEqual
import com.cerebrallychallenged.hypogean.graphics.gimp.lowerItem
import com.cerebrallychallenged.hypogean.graphics.gimp.minus
import com.cerebrallychallenged.hypogean.graphics.gimp.neonLogoAlpha
import com.cerebrallychallenged.hypogean.graphics.gimp.newChannelFromComponent
import com.cerebrallychallenged.hypogean.graphics.gimp.newFromDrawable
import com.cerebrallychallenged.hypogean.graphics.gimp.plus
import com.cerebrallychallenged.hypogean.graphics.gimp.pngSave
import com.cerebrallychallenged.hypogean.graphics.gimp.rgb
import com.cerebrallychallenged.hypogean.graphics.gimp.scaleFull
import com.cerebrallychallenged.hypogean.graphics.gimp.selectEllipse
import com.cerebrallychallenged.hypogean.graphics.gimp.selectItem
import com.cerebrallychallenged.hypogean.graphics.gimp.selectionInvert
import com.cerebrallychallenged.hypogean.graphics.gimp.selectionNone
import com.cerebrallychallenged.hypogean.graphics.gimp.selectionTranslate
import com.cerebrallychallenged.hypogean.view.actionbar.ActionIconRef
import com.cerebrallychallenged.hypogean.view.actionbar.BadgeGroup
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.util.forEachVariation
import com.cerebrallychallenged.jun.xml.Xml
import com.cerebrallychallenged.jun.xml.readXml
import com.cerebrallychallenged.jun.xml.writeXml
import java.nio.file.Files
import java.nio.file.Path

private val ActionButtonSelectedPath = ExtractedPath.resolve("actionButton-selected.xcf")
private val ActionButtonPath = ExtractedPath.resolve("actionButton.xcf")

data class BadgeGroupStyle<T : Any>(val group: BadgeGroup<T>, val icons: Map<T, String>) {
    companion object {
        operator fun <T : Any> invoke(group: BadgeGroup<T>, vararg icons: Pair<T, String>): BadgeGroupStyle<T> =
            BadgeGroupStyle(group, icons.toMap())

        operator fun invoke(group: BadgeGroup<Unit>, icon: String): BadgeGroupStyle<Unit> =
            BadgeGroupStyle(group, mapOf(Unit to icon))
    }

    init {
        require(group.values.keys == icons.keys)
    }

    val subIdWithIcon: Sequence<Pair<String, String>>
        get() = group.values.asSequence().map { (value, subId) -> Pair(subId, icons.getValue(value)) }
}

private data class BadgeInstance(val badgeId: String, val subId: String, val icon: String, val position: Vec2i) {
    val combinedId = "$badgeId$subId"

    val scssEntry = "$badgeId: ${if (subId.isEmpty()) "true" else subId}"
}

data class Button(
    val icon: ActionIconRef,
    val size: Int,
    val shift: Vec2i = Vec2i.ZERO,
    val badges: List<Pair<BadgeGroupStyle<*>, Vec2i>> = listOf()
)

private data class ButtonInstance(val id: String, val path: Path)

private class ButtonCreationContext(
    gimp: GimpContext,
    val id: String,
    val svgPath: Path,
    val svgSize: Int,
    val finalSize: Int,
    val shift: Vec2i,
    val pngOutputPath: Path
): GimpContext by gimp

fun createButtons(
    categoryName: String,
    finalSize: Int,
    buttons: List<Button>,
    actuallyRunsGimp: (id: String) -> Boolean = { true }
) {
    Files.createDirectories(DerivedIconsPath)
    val pngOutputPath = PngOutputPath.resolve(categoryName)
    Files.createDirectories(pngOutputPath)
    val script = buildGimp {
        for ((icon, size, shift, badges) in buttons) {
            val buttonId = icon.id
            val basePath = IconsPath.resolve("$buttonId.svg")
            require(Files.isRegularFile(basePath))
            val instances = mutableListOf(ButtonInstance(buttonId, basePath))
            badges.map { (badgeGroupStyle, position) ->
                val badgeId = badgeGroupStyle.group.id
                badgeGroupStyle.subIdWithIcon.mapTo(mutableListOf(null)) { (subId, icon) ->
                    BadgeInstance(badgeId, subId, icon, position)
                }
            }.forEachVariation { badgeList ->
                val derivedId = badgeList.joinToString(separator = "", prefix = buttonId) { "-${it.combinedId}" }
                val derivedPath = DerivedIconsPath.resolve("$derivedId.svg")
                deriveCombinedSvg(basePath, derivedPath, badgeList)
                instances.add(ButtonInstance(derivedId, derivedPath))
            }
            for ((id, path) in instances) {
                if (actuallyRunsGimp(id)) {
                    with(ButtonCreationContext(gimp, id, path, size, finalSize, shift, pngOutputPath)) {
                        process(isSelected = false, isActive = false)
                        process(isSelected = false, isActive = true)
                        process(isSelected = true, isActive = false)
                        process(isSelected = true, isActive = true)
                    }
                }
            }
        }
    }

    println(script)
    if (!script.isEmpty) {
        script.execute()
    }
}

private fun deriveCombinedSvg(basePath: Path, derivedPath: Path, badges: List<BadgeInstance>) {
//    derivedPath.writeXml(Xml.create {
//        "svg"(
//            "xmlns" to "http://www.w3.org/2000/svg",
//            "xmlns:xlink" to "http://www.w3.org/1999/xlink",
//            "viewbox" to "0 0 512 512",
//            "style" to "width: 512px; height: 512px;"
//        ) {
//            "image"(
//                "x" to "0",
//                "y" to "0",
//                "width" to "512",
//                "height" to "512",
//                "href" to DerivedIconsPath.relativize(basePath).toString()
//            )
//            "image"(
//                "x" to badgeLocation.x.toString(),
//                "y" to badgeLocation.y.toString(),
//                "width" to "179",
//                "height" to "179",
//                "href" to DerivedIconsPath.relativize(badgePath).toString()
//            )
//        }
//    })
    val baseXml = basePath.readXml()
    for ((_, _, icon, position) in badges) {
        val badgeXml = BadgesPath.resolve("$icon.svg").readXml()
        baseXml.add(Xml.create {
            "g"("transform" to "translate(${position.x}, ${position.y})") {
                "g"("transform" to "scale(0.7, 0.7) rotate(0, 128, 128)") {
                    addAll(badgeXml.children)
                }
            }
        })
    }
    derivedPath.writeXml(baseXml)
}

private fun GimpContext.loadMaskLayer(
    targetImg: GimpImage,
    targetLayer: GimpLayer,
    svgPath: Path,
    svgSize: Int,
    shift: Vec2i
) {
    val targetWidth = targetImg.width
    val targetHeight = targetImg.height
    val svgImg = fileSvgLoad(svgPath, "mask_svg", 90.0, svgSize, svgSize, GimpSvgPaths.None)
    val svgLayer = svgImg.layers[0]
    val channel = svgImg.newChannelFromComponent(GimpComponent.Blue, "mask")
    svgImg.insertChannel(channel)
    svgImg.selectItem(GimpChannelOp.Replace, channel)
    svgImg.selectionInvert()
    svgLayer.editClear()
    val maskLayer = svgLayer.newFromDrawable(targetImg)
    targetImg.insertLayer(maskLayer)
    targetImg.selectItem(GimpChannelOp.Replace, maskLayer)
    targetImg.removeLayer(maskLayer)
    targetImg.selectionTranslate((targetWidth - svgSize) / 2 + shift.x, (targetHeight - svgSize) / 2 + shift.y)
    targetImg.selectionInvert()
    targetLayer.editClear()
}

private fun ButtonCreationContext.process(isSelected: Boolean, isActive: Boolean) {
    val metalImg = filePngLoad(ExtractedPath.resolve("metalBase.png"), "metalbase")
    var metalLayer = metalImg.layers[0]
    loadMaskLayer(metalImg, metalLayer, svgPath, svgSize, shift)
    metalImg.selectionInvert()
    if (!isSelected) {
        bevelEmboss(
            metalImg,
            metalLayer,
            BevelingStyle.Emboss,
            18,
            EmbossingDirection.Up,
            12,
            0,
            135.0,
            30.0,
            0,
            GimpColor.White,
            CombinationMode.Screen,
            75.0,
            GimpColor.Black,
            CombinationMode.Multiply,
            75.0,
            0,
            false,
            "Dried mud",
            150.0,
            -30.0,
            invert = false,
            merge = true
        )
    } else {
        neonLogoAlpha(
            metalImg,
            metalLayer,
            30.0,
            rgb(0, 0, 0),
            rgb(255, 128, 0),
            false
        )
        metalImg.removeLayer(metalImg.layers[2])
        metalImg.mergeVisibleLayers(MergeType.ClipToImage)
    }
    metalLayer = metalImg.layers[0]
    val masterImg = fileLoad(if (isSelected) ActionButtonSelectedPath else ActionButtonPath, "actionButton")
    val masterSize = masterImg.width
    val insLayer = metalLayer.newFromDrawable(masterImg)
    masterImg.insertLayer(insLayer)
    masterImg.lowerItem(insLayer)
    var translation = vec(15, 17)
    if (isSelected) {
        translation -= vec(8, 6)
    }
    if (isActive) {
        translation += vec(6, 6)
    }
    insLayer.translate(translation)
    masterImg.selectEllipse(GimpChannelOp.Replace, 37.0, 37.0, 460.0, 460.0)
    masterImg.selectionInvert()
    insLayer.editClear()
    masterImg.selectionNone()
    if (isActive) {
        val darkLayer = masterImg.layers[2]
        darkLayer.brightnessContrast(-0.5, 0.0)
    }
    masterImg.mergeVisibleLayers(MergeType.ClipToImage)
    val fileName = buildString {
        append(id)
        if (isSelected) {
            append("-selected")
        }
        if (isActive) {
            append("-active")
        }
        append(".png")
    }
    masterImg.crop(masterSize, masterSize, 0.g, 0.g)
    masterSize.isNotEqual(finalSize).then {
        masterImg.scaleFull(finalSize, finalSize, ScaleInterpolation.Cubic)
    }
    masterImg.pngSave(masterImg.layers[0], pngOutputPath.resolve(fileName), "?")
}
