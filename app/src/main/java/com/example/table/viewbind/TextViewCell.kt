package com.example.table.viewbind

import android.view.View
import android.widget.TextView
import com.example.table.model.TableCell

/**
 * @author wanglun
 * @date 2022/02/11
 * @description
 */
object TextViewCell {

    fun bind(tableCell: TableCell<String>, view: View) {
        if (view !is TextView) return
        view.text = tableCell.data
    }
}