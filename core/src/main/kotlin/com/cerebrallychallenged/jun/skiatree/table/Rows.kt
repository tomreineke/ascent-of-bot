package com.cerebrallychallenged.jun.skiatree.table

@JvmInline
value class Rows(private val table: Table) {
    operator fun get(rowIndex: Int): Row = Row(table, rowIndex)
}
