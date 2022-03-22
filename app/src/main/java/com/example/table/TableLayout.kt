package com.example.table

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.HorizontalScrollView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.table.model.TableCell
import com.example.table.model.TableInfo

/**
 * @author wanglun
 * @date 2022/02/10
 * @description
 */
class TableLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : HorizontalScrollView(context, attrs, defStyleAttr) {

    private val adapter by lazy {
        TableAdapter<String>()
    }

    private val recyclerView: RecyclerView by lazy {
        RecyclerView(context)
    }

    private lateinit var tableInfo: TableInfo

    init {
        addView(recyclerView)
    }

    fun initTabInfo(tableInfo: TableInfo) {
        this.tableInfo = tableInfo
        initRecyclerView(tableInfo.tableContent ?: emptyList())
    }

    private fun initRecyclerView(tableContent: List<TableCell<String>>) {
        val spanCount = tableInfo.getColumnCount()
        recyclerView.layoutManager = GridLayoutManager(context, spanCount)
//        recyclerView.addItemDecoration(TableHeaderDecoration(context, tableInfo))
//        recyclerView.addItemDecoration(OutlineDecoration(tableInfo))
        recyclerView.addItemDecoration(TestDecoration(tableInfo))
        recyclerView.adapter = adapter
        adapter.setData(tableContent)
    }


}