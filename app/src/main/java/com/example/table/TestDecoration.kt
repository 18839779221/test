package com.example.table

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.table.model.TableCell
import com.example.table.model.TableInfo

/**
 * @author wanglun
 * @date 2022/02/14
 * @description
 */
class TestDecoration(private val tableInfo: TableInfo): RecyclerView.ItemDecoration() {

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        parent.setBackgroundColor(Color.parseColor("#EFF0F1"))
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val outlinePx = 20
        val tableCell = view.tag as TableCell<*>
        if (tableCell.column == 0) {
            outRect.left = outlinePx
        }
        outRect.right = 0
//        outRect.set(outlinePx/2, outlinePx/2, outlinePx/2, outlinePx/2)
//        when(tableCell.row){
//            0 -> {
//                outRect.left = outlinePx
//            }
//            tableInfo.getRowCount() - 1 -> {
//                outRect.right = outlinePx
//            }
//        }
//        when(tableCell.column) {
//            0 -> {
//                outRect.top = outlinePx
//            }
//            tableInfo.getColumnCount() - 1 -> {
//                outRect.bottom = outlinePx
//            }
//        }
    }
}