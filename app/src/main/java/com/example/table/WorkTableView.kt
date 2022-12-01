package com.example.table

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import com.example.test.databinding.ViewWorkTableBinding
import kotlinx.android.synthetic.main.item_column_work_table.view.*


/**
 * @author wanglun
 * @date 2022/11/16
 * @description 工作表格View，业务强相关组件
 */
class WorkTableView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private var binding: ViewWorkTableBinding = ViewWorkTableBinding.inflate(LayoutInflater.from(context), this, true)

    private val tableColumnAdapter by lazy {
        TableColumnAdapter()
    }

    private var rowHeights = listOf<Int>()

    init {
        initView()
    }

    private fun initView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = tableColumnAdapter
        }
        post {
            rowHeights = listOf(
                binding.tvHeader1.height,
                binding.tvHeader2.height,
                binding.tvHeader3.height,
            )
        }
    }

    fun setData(columnDataList: List<List<String>>) {
        post {
            tableColumnAdapter.setData(columnDataList, rowHeights)
        }
    }

    class TableColumnAdapter : RecyclerView.Adapter<TableColumnAdapter.Holder>() {

        private var columnDataList = listOf<List<String>>()
        private var rowHeights = listOf<Int>()

        fun setData(columnDataList: List<List<String>>, rowHeights: List<Int>) {
            this.columnDataList = columnDataList
            this.rowHeights = rowHeights
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableColumnAdapter.Holder {
            return Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_column_work_table, parent, false))
        }

        override fun onBindViewHolder(holder: TableColumnAdapter.Holder, position: Int) {
            holder.bind(columnDataList[position])
        }

        override fun getItemCount(): Int {
            return columnDataList.size
        }

        inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(list: List<String>) {
                itemView.apply {
                    listOf(tvItemHeader, tvItem1, tvItem2).forEachIndexed { index, tv ->
                        tv.text = list.getOrNull(index).orEmpty()
                        rowHeights.getOrNull(index)?.let {
                            val lp = tv.layoutParams
                            lp.height = it
                            tv.layoutParams = lp
                        }
                    }
                }
            }
        }

    }
}