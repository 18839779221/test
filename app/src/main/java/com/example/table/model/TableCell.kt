package com.example.table.model

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes

/**
 * @author wanglun
 * @date 2022/02/11
 * @description
 */
data class TableCell<T>(
    @LayoutRes
    var viewRes: Int,
    var data: T? = null,
    var row: Int,
    var column: Int,
    var viewBind: (TableCell<String>, View) -> Unit = { tableCell, view -> }
)

data class TableInfo(
    var topHeader: List<TableCell<String>>? = null,
    var leftHeader: List<TableCell<String>>? = null,
    var tableContent: List<TableCell<String>>? = null,
    var topHeight: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    var leftWidth: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    var outlinePx: Int = 2,
) {
    fun getRowCount(): Int {
        return leftHeader?.size ?: 0
    }

    fun getColumnCount(): Int {
        return topHeader?.size ?: 0
    }


}
