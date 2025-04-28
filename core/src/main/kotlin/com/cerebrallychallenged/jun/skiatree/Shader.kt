package com.cerebrallychallenged.jun.skiatree

import com.cerebrallychallenged.jun.CloseableResourceBearer
import com.cerebrallychallenged.jun.apibuilder.toNullableSegment
import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import com.cerebrallychallenged.jun.unreal.color.FLinearColor
import com.cerebrallychallenged.jun.util.confinedArena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_FLOAT
import java.lang.foreign.ValueLayout.JAVA_INT

class Shader private constructor(resource: CloseableResource) : CloseableResourceBearer(resource) {
    companion object : CloseableResourceFactory<Shader>(::Shader, "skiatree_shader_delete") {
        @JvmStatic
        private val shaderNewLinearGradient = function(
            "skiatree_shader_new_linear_gradient",
            ADDRESS,
            JAVA_FLOAT,
            JAVA_FLOAT,
            JAVA_FLOAT,
            JAVA_FLOAT,
            ADDRESS,
            ADDRESS,
            JAVA_INT,
            JAVA_INT
        )

        fun linearGradient(
            firstPos: Vec2f,
            secondPos: Vec2f,
            colors: Array<FLinearColor>,
            positions: FloatArray?,
            mode: TileMode
        ): Shader = Shader {
            confinedArena {
                shaderNewLinearGradient(
                    firstPos.x,
                    firstPos.y,
                    secondPos.x,
                    secondPos.y,
                    colors.toSegment(),
                    positions.toNullableSegment(),
                    colors.size,
                    mode.ordinal
                ) as MemorySegment
            }
        }
    }
}
