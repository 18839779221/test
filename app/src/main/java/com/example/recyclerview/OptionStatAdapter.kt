package com.example.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import kotlinx.android.synthetic.main.item_option_stat_group.view.*
import kotlinx.android.synthetic.main.item_option_stat_item.view.*

/**
 * @author wanglun
 * @date 2022/10/21
 * @description 可选指标
 */
class OptionStatAdapter(
    private val lifecycleOwner: LifecycleOwner,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_GROUP = 1
        const val TYPE_ITEM = 2
    }

    init {
//        viewModel.allStat.observe(lifecycleOwner) {
//            it ?: return@observe
//            statList = viewModel.getOptionStatList(it)
//            Log.e("coroutine", statList.toString())
//            notifyDataSetChanged()
//        }
    }

    private var statList: List<StatItem> = emptyList()

//    override fun getItemViewType(position: Int): Int {
//        return if (statList[position].isGroupItem) TYPE_GROUP else TYPE_ITEM
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return GroupHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_option_stat_group, parent, false))
//        return if (viewType == TYPE_GROUP) {
//            GroupHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_option_stat_item, parent, false))
//        } else {
//            ItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_option_stat_group, parent, false))
//        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? GroupHolder)?.bind(statList[position], position)
//        val viewType = getItemViewType(position)
//        if (viewType == TYPE_GROUP) {
//            (holder as? GroupHolder)?.bind(statList[position], position)
//        } else {
//            (holder as? ItemHolder)?.bind(statList[position], position)
//        }
    }

    override fun getItemCount(): Int {
        return statList.size
    }

    fun setData(statList: List<StatItem>) {
        this.statList = statList
        notifyDataSetChanged()
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var statItem: StatItem? = null

        fun bind(statItem: StatItem, position: Int) {
            this.statItem = statItem
            itemView.apply {
                tv_item_title.text = statItem.title
            }
        }
    }

    inner class GroupHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(statItem: StatItem, position: Int) {
            itemView.apply {
                tv_group_title.text = statItem.title
            }
        }

    }
}