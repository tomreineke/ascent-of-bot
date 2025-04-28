package com.cerebrallychallenged.hypogean.graphics

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.ColorPicker
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import javafx.stage.Stage

fun main(args: Array<String>) {
    Application.launch(ColorizeImageGUI::class.java, *args)
}


class ColorizeImageGUI : Application() {
    override fun start(primaryStage: Stage) {
        val vBox = VBox()
        val imageView = ImageView()
        val colorPicker = ColorPicker().apply {
            setOnAction {
                val black = Image("file:./gui-graphics/tmp/black_x.png")
                val diff = Image("file:./gui-graphics/tmp/diff.png")
                val color = value
                val result = black.pixelWise(diff) { blackPixel, diffPixel ->
                    blackPixel + diffPixel * color
                }
                imageView.image = result
            }
        }

        imageView.minWidth(1685.0)
        imageView.minHeight(540.0)
        vBox.children.addAll(colorPicker, imageView)
        primaryStage.scene = Scene(vBox)
        primaryStage.show()
    }
}