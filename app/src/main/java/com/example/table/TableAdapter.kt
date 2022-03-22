package com.example.table

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.table.model.TableCell

/**
 * @author wanglun
 * @date 2022/02/11
 * @description
 */
class TableAdapter<T>: RecyclerView.Adapter<TableAdapter.Holder>() {

    private var tableData: List<TableCell<String>> = mutableListOf()

    fun setData(tableData: List<TableCell<String>>) {
        this.tableData = tableData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(viewType, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        tableData[position].let {
            holder.itemView.tag = it
            it.viewBind(it, holder.itemView)
        }
    }

    override fun getItemCount(): Int {
        return tableData.size
    }

    override fun getItemViewType(position: Int): Int {
        return tableData[position].viewRes
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {}
}