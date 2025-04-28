package com.cerebrallychallenged.hypogean.accuracy

import com.cerebrallychallenged.hypogean.util.math.probability.normalAngleDistribution
import com.cerebrallychallenged.jun.math.geo.*
import com.cerebrallychallenged.jun.math.radians
import com.cerebrallychallenged.jun.util.ArrayND
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ListChangeListener
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.stage.Stage
import kotlin.random.Random

class AccuracyDemo private constructor() {

    private val random = Random(12)

    private val bounds = Bounds.centered(Vec2i.ZERO, vec(20, 20))

    private var baseConstant = 0.125f

    private var accuracy = 1.0f

    private var selectedCell: Vec2i? = null

    private var targetPos: Vec2i? = null

    private val gridPane: GridPane

    private var gridPaneRowCount: Int = 0

    private val tableModel: ArrayND<Vec2i, SimpleStringProperty>

    init {
        val stage = Stage()
        stage.width = 800.0
        stage.height = 600.0
        val hBox = HBox()
        val scene = Scene(hBox)
        stage.scene = scene
        val table = TableView<Int>()
        gridPane = GridPane()
        gridPane.minWidth = 300.0
        gridPane.hgap = 20.0
        gridPane.vgap = 10.0
        hBox.children.addAll(table, gridPane)
        val posLabel = Label()
        addToGridPane("Cell:", posLabel)
        val baseConstantField = TextField(baseConstant.toString())
        baseConstantField.setOnAction {
            try {
                baseConstant = baseConstantField.text.toFloat()
                recompute()
            } catch (e: NumberFormatException) {
                baseConstantField.text = baseConstant.toString()
            }
        }
        addToGridPane("Base Constant:", baseConstantField)
        val accuracyField = TextField(accuracy.toString())
        accuracyField.setOnAction {
            try {
                accuracy = accuracyField.text.toFloat()
                recompute()
            } catch (e: NumberFormatException) {
                accuracyField.text = accuracy.toString()
            }
        }
        addToGridPane("Accuracy:", accuracyField)
        val targetLabel = Label()
        addToGridPane("Target:", targetLabel)


        val columns = table.columns
        tableModel = ArrayND.create(bounds) { SimpleStringProperty() }
        for (x in bounds.rangeX) {
            val column = TableColumn<Int, String>("$x")
            column.setCellValueFactory { param ->
                val y = param.value
                tableModel[vec(x, y)]
            }
            column.minWidth = 50.0
            column.maxWidth = 50.0
            column.prefWidth = 50.0
            column.userData = x
            columns.add(column)
        }
        table.selectionModel.isCellSelectionEnabled = true
        table.fixedCellSize = 50.0
        table.items.setAll(bounds.rangeY.toList())
        val selectedPositions = table.selectionModel.selectedCells
        selectedPositions.addListener(ListChangeListener<TablePosition<*, *>> {
            val positionIterator = selectedPositions.iterator()
            if (positionIterator.hasNext()) {
                val position = positionIterator.next()
                val x = columns[position.column].userData as Int
                val y = table.items[position.row]
                selectedCell = vec(x, y)
                posLabel.text = selectedCell!!.toString()
            }
        })
        table.setOnKeyPressed { event ->
            if (event.code == KeyCode.ENTER) {
                targetPos = selectedCell
                targetLabel.text = targetPos!!.toString()
                recompute()
            }
        }
        tableModel[vec(0, 0)].set("Shooter")
        stage.show()
    }

    private fun addToGridPane(labelText: String, control: Node) {
        val label = Label(labelText)
        GridPane.setConstraints(label, 0, gridPaneRowCount)
        GridPane.setConstraints(control, 1, gridPaneRowCount)
        gridPane.children.addAll(label, control)
        ++gridPaneRowCount
    }

    private fun recompute() {
        if (targetPos == null) {
            return
        }
        val hitArray = ArrayND.create(bounds) { 0 }
        for (i in 0 until SAMPLE_COUNT) {
            val hit = sampleHit()
            if (hitArray.indexBounds.contains(hit)) {
                hitArray[hit] = hitArray[hit] + 1
            }
        }
        for (pos in bounds.points.asIterable()) {
            val builder = StringBuilder()
            if (pos.isZero) {
                builder.append("Shooter\n")
            }
            if (pos == targetPos) {
                builder.append("⌖\n")
            }
            val hitCount = hitArray[pos]
            if (hitCount > 0) {
                builder.append(String.format("%.2f", hitCount * 100.0 / SAMPLE_COUNT))
            }
            val string = if (builder.isNotEmpty()) builder.toString() else ""
            tableModel[pos].set(string)
        }
    }

    private fun sampleHit(): Vec2i {
        val intendedDistance = targetPos!!.length
        val intendedAngle = targetPos!!.toDouble().angle()
        val angle = normalAngleDistribution(intendedAngle, (baseConstant / accuracy).radians)(random)
        val distance = 6.0
        return polar(distance, angle).round()
    }

    companion object {
        private const val SAMPLE_COUNT = 1000000

        @JvmStatic
        fun main(args: Array<String>) {
            Platform.startup { AccuracyDemo() }
        }
    }
}
