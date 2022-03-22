package com.example.table

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.table.model.TableCell
import com.example.table.model.TableInfo

/**
 * @author wanglun
 * @date 2022/02/11
 * @description 左侧表头和上侧表头的decoration
 */
class TableHeaderDecoration(private val context: Context, private val tableInfo: TableInfo): RecyclerView.ItemDecoration() {

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        // 绘制topHeader
        c.translate(0f, tableInfo.leftWidth.toFloat())
        tableInfo.topHeader?.forEachIndexed { index, tableCell ->
            val left = index * tableInfo.leftWidth
            val right = left + tableInfo.leftWidth
            val rect = Rect(left, 0 ,right, tableInfo.topHeight)
            val view = LayoutInflater.from(context).inflate(tableCell.viewRes, null)
            view?.apply {
                clipBounds = rect
                draw(c)
            }
        }
        // 绘制leftHeader
        c.translate(tableInfo.topHeight.toFloat(), -tableInfo.leftWidth.toFloat())
        tableInfo.leftHeader?.forEachIndexed { index, tableCell ->
            val top = index * tableInfo.topHeight
            val bottom = top + tableInfo.topHeight
            val rect = Rect(0, top ,tableInfo.leftWidth, bottom)
            val view = LayoutInflater.from(context).inflate(tableCell.viewRes, null)
            view?.apply {
                clipBounds = rect
                draw(c)
            }
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        var topMargin = tableInfo.topHeight
        var leftMargin = tableInfo.leftWidth
        val tableCell = view.tag as TableCell<*>
        if (tableCell.row == 0) {
            //预留topHeader的空间
            if (topMargin == ViewGroup.LayoutParams.WRAP_CONTENT) {
                // 用户设置的TopHeader的高度 ?: 当前View的高度
                topMargin = 100 ?: view.height
                // 缓存高度值，同一行则不用再次获取
                tableInfo.topHeight = topMargin
            }
        } else {
            topMargin = 0
        }
        if (tableCell.column == 0) {
            //预留leftHeader的空间
            if (leftMargin == ViewGroup.LayoutParams.WRAP_CONTENT) {
                leftMargin = 100 ?: view.width
                tableInfo.leftWidth = leftMargin
            }
        } else {
            leftMargin = 0
        }
        outRect.set(leftMargin, topMargin, 0 ,0)
    }
}