package com.example.coordinatorlayout.multinested

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import kotlinx.android.synthetic.main.item_fbottom.view.*

/**
 * @author wanglun
 * @date 2021/09/27
 * @description
 */
class FBottomAdapter: RecyclerView.Adapter<FBottomAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_fbottom, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(position)
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(position: Int) {
            itemView.tvContent.text = "I' am FBottom $position"
        }

    }

    override fun getItemCount(): Int {
        return 20
    }
}