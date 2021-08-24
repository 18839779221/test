package com.example.nestedscroll

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.NestedScrollingChild3
import com.example.scroll.ScrollLayout

/**
 * @author wanglun
 * @date 2021/08/23
 * @description
 */
class ScrollChildLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : NestedScrollLayout(context, attrs), NestedScrollingChild3 {


    override fun startNestedScroll(axes: Int, type: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun stopNestedScroll(type: Int) {
        TODO("Not yet implemented")
    }

    override fun hasNestedScrollingParent(type: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        type: Int,
        consumed: IntArray,
    ) {
        TODO("Not yet implemented")
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        type: Int,
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?,
        type: Int,
    ): Boolean {
        TODO("Not yet implemented")
    }
}