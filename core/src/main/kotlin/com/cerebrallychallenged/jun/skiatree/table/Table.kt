package com.cerebrallychallenged.jun.skiatree.table

import com.cerebrallychallenged.hypogean.gui.Styling
import com.cerebrallychallenged.hypogean.gui.applyStyle
import com.cerebrallychallenged.jun.log.log
import com.cerebrallychallenged.jun.skiatree.layout.Flow
import com.cerebrallychallenged.jun.skiatree.node.Node
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class Table(private val columnCount: Int) : Node() {
    init {
        flow = Flow.Table
        columns.size = columnCount
    }

    private fun obtainCell(rowIndex: Int, columnIndex: Int): Node {
        val index = rowIndex * columnCount + columnIndex
        repeat(index - children.size + 1) {
            children.add(Node().apply {
                horizontalAlign = this@Table.columns[columnIndex].align
            })
        }
        return children[index]
    }

    operator fun get(rowIndex: Int, columnIndex: Int): Node = obtainCell(rowIndex, columnIndex)

    val columns: Columns
        get() = Columns(this)

    val rows: Rows
        get() = Rows(this)

    fun addRow(): Row = rows[if (children.isEmpty()) 0 else (children.size - 1) / columnCount + 1]
}

inline fun Node.table(
    columnCount: Int,
    style: Styling<Table, Unit>? = null,
    f: Table.() -> Unit
): Table {
    contract {
        callsInPlace(f, InvocationKind.EXACTLY_ONCE)
    }
    return Table(columnCount).also {
        if (style != null) {
            it.applyStyle(style)
        }
        children.add(it)
        it.f()
    }
}
