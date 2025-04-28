package com.cerebrallychallenged.jun.skiatree.table

import com.cerebrallychallenged.jun.skiatree.layout.Align
import com.cerebrallychallenged.jun.skiatree.node.Node
import java.lang.invoke.MethodHandle

class Column(override val table: Table, override val index: Int) : TableIndexed {
    companion object {
        @JvmStatic
        private val leftDelegate = IndexedIntParameterDelegate<Column>(
            "skiatree_table_column_get_left",
            "skiatree_table_column_set_left"
        )

        @JvmStatic
        private val rightDelegate = IndexedIntParameterDelegate<Column>(
            "skiatree_table_column_get_right",
            "skiatree_table_column_set_right"
        )

        @JvmStatic
        private val minWidthDelegate = IndexedIntParameterDelegate<Column>(
            "skiatree_table_column_get_min_width",
            "skiatree_table_column_set_min_width"
        )

        @JvmStatic
        private val maxWidthDelegate = IndexedIntParameterDelegate<Column>(
            "skiatree_table_column_get_max_width",
            "skiatree_table_column_set_max_width"
        )

        @JvmStatic
        private val alignDelegate = object : IndexedEnumParameterDelegate<Align, Column>(enumValues()) {
            override val getter: MethodHandle = createGetter("skiatree_table_column_get_align")

            override val setter: MethodHandle = createSetter("skiatree_table_column_set_align")
        }
    }

    var left: Int by leftDelegate

    var right: Int by rightDelegate

    var minWidth: Int by minWidthDelegate

    var maxWidth: Int by maxWidthDelegate

    var align: Align by alignDelegate

    operator fun get(rowIndex: Int): Node = table[rowIndex, index]
}
