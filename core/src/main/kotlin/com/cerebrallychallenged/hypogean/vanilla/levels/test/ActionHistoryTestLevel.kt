package com.cerebrallychallenged.hypogean.vanilla.levels.test

import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.WorldFactory
import com.cerebrallychallenged.hypogean.util.withTestingContext
import com.cerebrallychallenged.hypogean.vanilla.levels.setupBase
import com.cerebrallychallenged.jun.math.geo.Bounds
import com.cerebrallychallenged.jun.math.geo.vec

object ActionHistoryTestLevel : WorldFactory {
    override fun World.setup() = withTestingContext {
        setupBase(Bounds.of(vec(-15, -15), vec(15, 15)))
        setupCheckerBoard(vec(9, 9))
        val robot = setupProtagonist(vec(1, 1))

    }
}