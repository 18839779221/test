package com.example.utils

import android.graphics.Outline
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.TextView


/**
 * @author wanglun
 * @date 2022/11/02
 * @description
 */

fun View.setGone() {
    visibility = View.GONE
}

fun View.setVisible() {
    visibility = View.VISIBLE
}

fun View.setInvisible() {
    visibility = View.INVISIBLE
}

fun View.setVisible(ifVisible: Boolean?) {
    visibility = when (ifVisible) {
        true -> View.VISIBLE
        false -> View.GONE
        null -> View.INVISIBLE
    }
}

fun TextView.setTextIfNullSetGone(charSequence: CharSequence?, setGoneView: View = this) {
    if (charSequence.isNullOrEmpty()) {
        setGoneView.setGone()
        return
    }
    setGoneView.setVisible()
    text = charSequence
}

fun ViewGroup.MarginLayoutParams?.setMarginsRelative(start: Int, top: Int, end: Int, bottom: Int) {
    this ?: return
    this.topMargin = top
    this.bottomMargin = bottom
    this.marginStart = start
    this.marginEnd = end
}

fun View.setRoundCorner(radius: Float, isOval: Boolean = false) {
    outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            if (isOval) {
                // 设置按钮为圆形
                outline.setOval(0, 0, view.width, view.height)
            } else {
                // 设置按钮圆角率为30
                outline.setRoundRect(0, 0, view.width, view.height, radius)
            }
        }
    }
    clipToOutline = true
}