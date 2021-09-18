package com.example.nestedscroll

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import com.example.test.R
import com.example.utils.limitRange
import java.lang.Float.max
import kotlin.math.min

/**
 * @author wanglun
 * @date 2021/08/23
 * @description
 */
class ScrollParentLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : NestedScrollLayout(context, attrs), NestedScrollingParent3 {

    private val parentHelper = NestedScrollingParentHelper(this)

    private var topViewHeight: Int = 0
    private lateinit var scrollChildLayout: ScrollChildLayout
    private lateinit var topView: TextView

    override fun onFinishInflate() {
        super.onFinishInflate()
        scrollChildLayout = findViewById(R.id.scroll_child_layout)
        topView = findViewById(R.id.top_view)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        topViewHeight = topView.measuredHeight
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        //判断是否处理嵌套滑动
        return axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        parentHelper.onNestedScrollAccepted(child, target, axes, type)

    }

    override fun onStopNestedScroll(target: View, type: Int) {
        parentHelper.onStopNestedScroll(target, type)
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray,
    ) {

    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
    ) {

    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        var consumedY = 0
        //scrollY以向下为正向,整体相对于初始位置的偏移 -topViewHeight <= scrollY <= 0
        Log.e("testnested", "dy=${dy},scrollY=${scrollY}")
        if (target == scrollChildLayout) {
            //下滑 && TopView还能再下滑（在初始位置之上）
            if (dy > 0 && scrollY > 0 && !scrollChildLayout.canScrollVertically(-1)) {
                consumedY = Math.min(scrollY, dy)
                Log.e("testnested", "下滑 && TopView还能再下滑（在初始位置之上）$consumedY")
            //上滑 && TopView还能向上滑（TopView还可见）
            } else if (dy < 0 && scrollY < topViewHeight) {
                consumedY = Math.max(-topViewHeight + scrollY, dy)
                Log.e("testnested", "上滑 && TopView还能向上滑（TopView还可见）$consumedY")
            }
        }
        if (consumedY != 0) {
            scrollBy(0, -consumedY)
            consumed[1] = consumedY
        } else {
            Log.e("testnested", "不嵌套消费")
        }
    }

    override fun handleScroll(currX: Float, currY: Float) {
        val deltaX = currX - lastX
        val deltaY = currY - lastY
        if (canScrollVertically(1) || canScrollVertically(-1)) {
            //防止滑出边界
            val realDeltaY = getRealScrollDistance(deltaY.toInt())
            scrollBy(0, -realDeltaY)
            scrollChildLayout.parentScrollNotNested += -realDeltaY
        }
        awakenScrollBars()
    }

    override fun computeScroll() {
        if (overScroller.computeScrollOffset()) {
            val deltaY = overScroller.currY - lastFlingY
            lastFlingY = overScroller.currY
            if(canScrollVertically(-deltaY)){
                val realScrollY = getRealScrollDistance(deltaY)
                scrollBy(0, -realScrollY)
                scrollChildLayout.parentScrollNotNested += -realScrollY
                postInvalidate()
            }
        }
        awakenScrollBars()
    }

    override fun onNestedFling(
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        Log.e("testnestedfling", "onNestedFling")
        if(scrollY < topViewHeight){
            flingWithOverScroller(velocityY)
            Log.e("testnestedfling", "Parent消费fling velocityY=${velocityY}")
            return true
        }
        Log.e("testnestedfling", "Parent不消费fling")
        return false
    }

    /**
     * NestedScrollingChild在惯性滑动之前,将惯性滑动的速度分发给NestedScrollingParent
     * @param target 同上
     * @param velocityX 同上
     * @param velocityY 同上
     * @return 返回NestedScrollingParent是否消费全部惯性滑动
     */
    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        return false
    }

}