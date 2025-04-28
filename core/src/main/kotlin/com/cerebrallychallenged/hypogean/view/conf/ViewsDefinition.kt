package com.cerebrallychallenged.hypogean.view.conf

import com.cerebrallychallenged.hypogean.modding.SimpleObjectRegistry
import com.cerebrallychallenged.hypogean.model.FactionContext
import com.cerebrallychallenged.hypogean.model.FactionEntity
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.WorldContext
import com.cerebrallychallenged.hypogean.view.View
import com.cerebrallychallenged.hypogean.view.ViewModel
import com.cerebrallychallenged.jun.asset.AssetLibrary
import com.cerebrallychallenged.jun.coroutine.Unreal
import com.cerebrallychallenged.jun.skiatree.SkiaTreeWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

interface ViewFactory {
    class Context(
            override val world: World,
            val viewModel: ViewModel,
            val assetLibrary: AssetLibrary,
            val widget: SkiaTreeWidget,
            val sessionScope: CoroutineScope
    ) : WorldContext, FactionContext {
        override val ownFactionEntity: FactionEntity = world.factionEntity(viewModel.clientFaction)
    }

    /**
     * Is called before this factory is configured by [ViewsDefinition].
     * Can be used to obtain a default configuration from other sources, e.g. external files or the browser.
     */
    suspend fun preConfigure(context: Context) {}

    /**
     * Creates the view.
     * Is executed in Unreal thread.
     */
    suspend fun create(context: Context): View
}

class ViewDefinitionContext internal constructor(
        private val creationScope: CoroutineScope,
        private val views: MutableList<Deferred<View>>,
        private val factoryContext: ViewFactory.Context
) {
    suspend fun <F : ViewFactory> view(
            createFactory: () -> F,
            configure: (F.() -> Unit)? = null
    ) {
        views.add(creationScope.async(Dispatchers.Unreal) {
            val factory = createFactory()
            factory.preConfigure(factoryContext)
            if (configure != null) {
                factory.configure()
            }
            factory.create(factoryContext)
        })
    }
}

abstract class ViewsDefinition(
        private val execute: suspend ViewDefinitionContext.() -> Unit
) {
    internal suspend fun createViews(
            world: World,
            viewModel: ViewModel,
            assetLibrary: AssetLibrary,
            widget: SkiaTreeWidget,
            sessionScope: CoroutineScope
    ): List<View> {
        val views = mutableListOf<Deferred<View>>()
        val factoryContext = ViewFactory.Context(world, viewModel, assetLibrary, widget, sessionScope)
        coroutineScope {
            val context = ViewDefinitionContext(this, views, factoryContext)
            context.execute()
        }
        return views.awaitAll()
    }
}

class ViewsDefinitions : SimpleObjectRegistry<ViewsDefinition>()
