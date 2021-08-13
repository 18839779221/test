package com.example.viewgroup

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams

/**
 * @author wanglun
 * @date 2021/08/09
 * @description
 */
class GroupLayout@kotlin.jvm.JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    /**
     * 计算所有ChildView的宽度和高度 然后根据ChildView的计算结果，设置自己的宽和高
     */
    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        /**
         * 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
         */
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val sizeWidth = MeasureSpec.getSize(widthMeasureSpec)
        val sizeHeight = MeasureSpec.getSize(heightMeasureSpec)


        // 计算出所有的childView的宽和高
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        /**
         * 记录如果是wrap_content是设置的宽和高
         */
        var width = 0
        var height = 0
        val cCount = childCount
        var cWidth = 0
        var cHeight = 0
        var cParams: MarginLayoutParams? = null

        // 用于计算左边两个childView的高度
        var lHeight = 0
        // 用于计算右边两个childView的高度，最终高度取二者之间大值
        var rHeight = 0

        // 用于计算上边两个childView的宽度
        var tWidth = 0
        // 用于计算下面两个childiew的宽度，最终宽度取二者之间大值
        var bWidth = 0
        /**
         * 根据childView计算的出的宽和高，以及设置的margin计算容器的宽和高，主要用于容器是warp_content时
         */
        for (i in 0 until cCount) {
            val childView: View = getChildAt(i)
            cWidth = childView.measuredWidth
            cHeight = childView.measuredHeight
            cParams = MarginLayoutParams(childView.layoutParams)

            // 上面两个childView
            if (i == 0 || i == 1) {
                tWidth += cWidth + cParams.leftMargin + cParams.rightMargin
            }
            if (i == 2 || i == 3) {
                bWidth += cWidth + cParams.leftMargin + cParams.rightMargin
            }
            if (i == 0 || i == 2) {
                lHeight += cHeight + cParams.topMargin + cParams.bottomMargin
            }
            if (i == 1 || i == 3) {
                rHeight += cHeight + cParams.topMargin + cParams.bottomMargin
            }
        }
        width = Math.max(tWidth, bWidth)
        height = Math.max(lHeight, rHeight)
        /**
         * 如果是wrap_content设置为我们计算的值
         * 否则：直接设置为父容器计算的值
         */
        setMeasuredDimension(
            if (widthMode == MeasureSpec.EXACTLY) sizeWidth else width,
            if (heightMode == MeasureSpec.EXACTLY) sizeHeight else height
        )
    }

    // abstract method in viewgroup
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val cCount = childCount
        var cWidth = 0
        var cHeight = 0
        var cParams: MarginLayoutParams? = null
        /**
         * 遍历所有childView根据其宽和高，以及margin进行布局
         */
        for (i in 0 until cCount) {
            val childView = getChildAt(i)
            cWidth = childView.measuredWidth
            cHeight = childView.measuredHeight
            cParams = MarginLayoutParams(childView.layoutParams)
            var cl = 0
            var ct = 0
            var cr = 0
            var cb = 0
            when (i) {
                0 -> {
                    cl = cParams.leftMargin
                    ct = cParams.topMargin
                }
                1 -> {
                    cl = (width - cWidth - cParams.leftMargin
                            - cParams.rightMargin)
                    ct = cParams.topMargin
                }
                2 -> {
                    cl = cParams.leftMargin
                    ct = height - cHeight - cParams.bottomMargin
                }
                3 -> {
                    cl = (width - cWidth - cParams.leftMargin
                            - cParams.rightMargin)
                    ct = height - cHeight - cParams.bottomMargin
                }
            }
            cr = cl + cWidth
            cb = cHeight + ct
            childView.layout(cl, ct, cr, cb)
        }
    }
}