package com.cerebrallychallenged.hypogean.app

import com.cerebrallychallenged.HypogeanApplicationFactory

context(ApplicationStateContext)
abstract class ApplicationState {
    abstract suspend fun HypogeanApplicationFactory.execute(): ApplicationState?
}
