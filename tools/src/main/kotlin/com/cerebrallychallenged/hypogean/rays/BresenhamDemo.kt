package com.cerebrallychallenged.hypogean.rays

import com.cerebrallychallenged.hypogean.Heading
import com.cerebrallychallenged.jun.math.geo.*
import com.cerebrallychallenged.jun.util.ArrayND
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ListChangeListener
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.stage.Stage

class BresenhamDemo private constructor() {

    private val bounds = Bounds.centered(Vec2i.ZERO, vec(20, 20))

    private var radius = 5

    private var selectedCell: Vec2i? = null

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
        val radiusField = TextField(radius.toString())
        radiusField.setOnAction {
            try {
                radius = radiusField.text.toInt()
                recompute()
            } catch (e: NumberFormatException) {
                radiusField.text = radius.toString()
            }
        }
        addToGridPane("Radius:", radiusField)

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
            recompute()
        })
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
        tableModel.indices.forEach {
            tableModel[it].set("") // reset
        }
//        circleBresenham(selectedCell!!.x, selectedCell!!.y, radius).filter { bounds.contains(it) }.forEach {
//            tableModel[it].set("X")
//        }
        for ((i, p) in circleBresenham(vec(selectedCell!!.x, selectedCell!!.y), radius, Heading.NORTH_WEST, Heading.SOUTH_EAST, false)
            .withIndex().filter { bounds.contains(it.value) }) {
//            tableModel[p].set("$i")
            tableModel[p].set(tableModel[p].get() + ",$i")
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {

            for (i in -10..10) {
                println("$i % 4 == ${i.mod(4)}")
            }

            Platform.startup { BresenhamDemo() }
        }
    }
}
