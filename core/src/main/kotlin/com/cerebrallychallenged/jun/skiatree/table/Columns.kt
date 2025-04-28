package com.cerebrallychallenged.jun.skiatree.table

import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.libraryPointer
import com.cerebrallychallenged.jun.skiatree.VOID
import com.cerebrallychallenged.jun.skiatree.guarded
import com.cerebrallychallenged.jun.skiatree.guardedUnit
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_INT
import java.lang.foreign.ValueLayout.JAVA_LONG

@JvmInline
value class Columns(private val table: Table) {
    companion object {
        @JvmStatic
        private val tableGetColumnCount = function(
            "skiatree_table_get_column_count",
            JAVA_INT,
            ADDRESS,
            JAVA_LONG
        )

        @JvmStatic
        private val tableSetColumnCount = function(
            "skiatree_table_set_column_count",
            VOID,
            ADDRESS,
            JAVA_LONG,
            JAVA_INT
        )
    }

    var size: Int
        get() = guarded(Int.MIN_VALUE) { tableGetColumnCount(libraryPointer, table.resource.key) as Int }
        internal set(value) = guardedUnit { tableSetColumnCount(libraryPointer, table.resource.key, value) as Byte }

    operator fun get(columnIndex: Int): Column = Column(table, columnIndex)
}
