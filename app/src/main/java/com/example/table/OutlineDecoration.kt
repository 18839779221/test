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
class OutlineDecoration(private val tableInfo: TableInfo): RecyclerView.ItemDecoration() {

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        parent.setBackgroundColor(Color.parseColor("#EFF0F1"))
        val outlinePx = tableInfo.outlinePx
        parent.setPadding(outlinePx, outlinePx, 0, 0)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val outlinePx = tableInfo.outlinePx
        outRect.right = outlinePx
        outRect.bottom = outlinePx
    }
}