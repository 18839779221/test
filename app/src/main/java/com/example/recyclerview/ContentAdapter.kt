package com.example.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import android.util.Log
import androidx.core.view.doOnDetach
import kotlinx.android.synthetic.main.item_content.view.*

class ContentAdapter : ListAdapter<User, ContentAdapter.ViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_content, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: ViewHolder) {
        Log.e("ContentAdapter", "onViewRecycled")
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        Log.e("ContentAdapter", "onDetachedFromRecyclerView")
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        Log.e("ContentAdapter", "onViewDetachedFromWindow")
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(user: User?) {
            itemView.apply {
                tvId.text = user?.id
                tvName.text = user?.name
            }
        }

        init {
            itemView.doOnDetach {
                Log.e("ContentAdapter", "doOnDetach")
            }
        }

    }



}
