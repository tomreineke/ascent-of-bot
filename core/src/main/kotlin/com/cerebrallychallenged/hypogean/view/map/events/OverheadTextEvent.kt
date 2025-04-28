package com.cerebrallychallenged.hypogean.view.map.events

import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.vanilla.refs.Hypogean
import com.cerebrallychallenged.hypogean.view.map.LOOK_VECTOR
import com.cerebrallychallenged.hypogean.view.map.MapViewContext
import com.cerebrallychallenged.jun.math.geo.curve.Polyline
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.unreal.EComponentMobility
import com.cerebrallychallenged.jun.unreal.color.FLinearColor
import com.cerebrallychallenged.jun.unreal.font.FFontOutlineSettings
import com.cerebrallychallenged.jun.unreal.font.FSlateFontInfo
import com.cerebrallychallenged.jun.unreal.newObject
import com.cerebrallychallenged.jun.unreal.widget.ETextJustify
import com.cerebrallychallenged.jun.unreal.widget.EWidgetSpace
import com.cerebrallychallenged.jun.unreal.widget.STextBlock
import com.cerebrallychallenged.jun.unreal.widget.UWidgetComponent
import com.cerebrallychallenged.jun.unreal.widget.colorAndOpacity
import com.cerebrallychallenged.jun.unreal.widget.font
import com.cerebrallychallenged.jun.unreal.widget.justification
import com.cerebrallychallenged.jun.unreal.widget.text

class OverheadTextEvent(
    private val entity: LocatedEntity,
    private val color: FLinearColor,
    private val text: String
) : MapViewEvent() {
    context(MapViewContext)
    override suspend fun execute() {
        val textBlock = STextBlock.createBySNew()
        textBlock.text = text
        textBlock.colorAndOpacity = color
        textBlock.justification = ETextJustify.Center
        val font = assetLibrary.load(Hypogean.OverheadFont)
        val fontInfo = FSlateFontInfo.makeShared(
            font,
            48,
            null,
            FFontOutlineSettings.makeShared(1, FLinearColor.Black)
        )
        textBlock.font = fontInfo
        val component = newObject<UWidgetComponent>()
        component.mobility = EComponentMobility.Movable
        component.slateWidget = textBlock
        component.widgetSpace = EWidgetSpace.Screen
        component.pivot = vec(0.5f, 0.1f)
        component.registerComponent()
        val position = entity.centerPoint
        val positionCurve =
            Polyline.from(position + vec(0.0f, 0.0f, 1.5f))
                .apply { speed = 1.2f }
                .lineTo(position + vec(0.0f, 0.0f, 3.0f))
                .build()
        val rotationCurve =
            Polyline.constant((-LOOK_VECTOR).toLookAtWith(vec(1.0f, 1.0f, 1.0f)))

        addAnimation(MoveAnimation(component, positionCurve, rotationCurve, destroyAtEnd = true))
    }

    override val duration: Float
        get() = 1.5f
}
