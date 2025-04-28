package com.cerebrallychallenged

import com.cerebrallychallenged.hypogean.app.ApplicationState
import com.cerebrallychallenged.hypogean.app.ApplicationStateContext
import com.cerebrallychallenged.hypogean.app.MainMenuState
import com.cerebrallychallenged.hypogean.gui.GuiAssetLoaders
import com.cerebrallychallenged.hypogean.gui.GuiLayer
import com.cerebrallychallenged.hypogean.gui.get
import com.cerebrallychallenged.hypogean.model.feature
import com.cerebrallychallenged.jun.JunApplicationFactory
import com.cerebrallychallenged.jun.JunManager
import com.cerebrallychallenged.jun.asset.AssetLibrary
import com.cerebrallychallenged.jun.coroutine.Unreal
import com.cerebrallychallenged.jun.log.log
import com.cerebrallychallenged.jun.skiatree.SkiaTreeWidget
import com.cerebrallychallenged.jun.skiatree.input.HitModel
import com.cerebrallychallenged.jun.unreal.EMouseLockMode
import com.cerebrallychallenged.jun.unreal.FInputModeGameAndUI
import com.cerebrallychallenged.jun.unreal.UGameplayStatics
import com.cerebrallychallenged.jun.unreal.skiatree.isPixelCovered
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object HypogeanApplicationFactory : JunApplicationFactory {
    lateinit var widget: SkiaTreeWidget

    override suspend fun create() = withContext(Dispatchers.Unreal) {
        log.info { "classpath: ${System.getProperties()["java.class.path"]}" }

        val playerController = UGameplayStatics.getPlayerController(playerIndex = 0)
        playerController.showMouseCursor = true
        playerController.enableClickEvents = true
        playerController.enableMouseOverEvents = true

        widget = SkiaTreeWidget()
        JunManager.GEngine.gameViewport?.addViewportWidgetContent(widget.widget, zOrder = 1)
        playerController.setInputMode(FInputModeGameAndUI(widget.widget, EMouseLockMode.DoNotLock, false))
        JunManager.inputManager.pixelOccluders += widget.widget::isPixelCovered
        widget.layers[GuiLayer.Tooltip].hitModel = HitModel.None

        with (ApplicationStateContext()) {
            val guiAssetLibrary = AssetLibrary(this@withContext)
            for (assetLoader in rulebook.feature<GuiAssetLoaders>()) {
                assetLoader.load(guiAssetLibrary)
            }

            // Here we can implement logic to skip the main menu in some cases, e.g. depending on command line parameters.
            val overrideGameState: ApplicationState? = null
//            val overrideGameState: ApplicationState? = GameState(FirstLevel)

            var applicationState: ApplicationState? = overrideGameState ?: MainMenuState()
            while (applicationState != null) {
                applicationState = with(applicationState) { execute() }
            }
        }
    }
}
