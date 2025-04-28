package com.cerebrallychallenged.jun.skiatree.table

import com.cerebrallychallenged.jun.skiatree.node.Node

class Row(override val table: Table, override val index: Int) : TableIndexed {
    companion object {
        @JvmStatic
        private val topDelegate = IndexedIntParameterDelegate<Row>(
            "skiatree_table_row_get_top",
            "skiatree_table_row_set_top"
        )

        @JvmStatic
        private val bottomDelegate = IndexedIntParameterDelegate<Row>(
            "skiatree_table_row_get_bottom",
            "skiatree_table_row_set_bottom"
        )

        @JvmStatic
        private val minHeightDelegate = IndexedIntParameterDelegate<Row>(
            "skiatree_table_row_get_min_height",
            "skiatree_table_row_set_min_height"
        )

        @JvmStatic
        private val maxHeightDelegate = IndexedIntParameterDelegate<Row>(
            "skiatree_table_row_get_max_height",
            "skiatree_table_row_set_max_height"
        )

        @JvmStatic
        private val topStrokeWidthDelegate = IndexedFloatParameterDelegate<Row>(
            "skiatree_table_row_get_top_stroke_width",
            "skiatree_table_row_set_top_stroke_width"
        )

        @JvmStatic
        private val bottomStrokeWidthDelegate = IndexedFloatParameterDelegate<Row>(
            "skiatree_table_row_get_bottom_stroke_width",
            "skiatree_table_row_set_bottom_stroke_width"
        )
    }

    var top: Int by topDelegate

    var bottom: Int by bottomDelegate

    var minHeight: Int by minHeightDelegate

    var maxHeight: Int by maxHeightDelegate

    var topStrokeWidth by topStrokeWidthDelegate

    var bottomStrokeWidth by bottomStrokeWidthDelegate

    operator fun get(columnIndex: Int): Node = table[index, columnIndex]
}
