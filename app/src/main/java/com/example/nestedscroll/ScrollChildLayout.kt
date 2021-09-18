package com.example.nestedscroll

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.core.view.NestedScrollingChild3
import androidx.core.view.NestedScrollingChildHelper
import androidx.core.view.ViewCompat
import com.example.utils.limitRange
import kotlin.math.abs

/**
 * @author wanglun
 * @date 2021/08/23
 * @description
 */
class ScrollChildLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : NestedScrollLayout(context, attrs), NestedScrollingChild3 {

    private val scrollOffset = IntArray(2)
    private val scrollConsumed = IntArray(2)
    //仅仅用于校正fling的速度
    private var nestedYOffset = 0
    //父滑动控件消费到的当前View的滑动距离，这部分距离无法体现在scrollY中，如果不考虑，则会导致当前View的可滑动距离变大
    public var parentScrollNotNested = 0
    private var totalUnConsumed = 0

    private val childHelper = NestedScrollingChildHelper(this).apply {
        //注意要手动设置isNestedScrollingEnabled为ture，只有开启此开关，嵌套滑动才有效
        isNestedScrollingEnabled = true
    }

    //实现参考了NestedScrollView
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (ev == null) return false
        val action = ev.action
        if (action == MotionEvent.ACTION_MOVE && isBeingDragged) {
            return true
        }
        var currY = ev.y

        when (action) {
            MotionEvent.ACTION_MOVE -> {
                if (abs(currY - lastY) >= touchSlop) {
                    isBeingDragged = true
                    val parent = parent
                    parent?.requestDisallowInterceptTouchEvent(true)
                }
            }
            MotionEvent.ACTION_DOWN -> {
                isBeingDragged = false
                nestedYOffset = 0
                //开始嵌套滑动，注意不是startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL)
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH)
            }
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP,
            -> {
                //结束嵌套滑动
                isBeingDragged = false
                stopNestedScroll()
            }
        }
        return isBeingDragged
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false
        val action = event.action
        val currX = event.x
        var currY = event.y
        val offsetEvent = MotionEvent.obtain(event)
        //根据总的嵌套滑动偏移量，校正速度
        offsetEvent.offsetLocation(0f, nestedYOffset.toFloat())
        velocityTracker.addMovement(offsetEvent)
        offsetEvent.recycle()
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                touchDown(currX, currY)
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(currX, currY)
            }
            MotionEvent.ACTION_UP -> {
                touchUp()
            }
        }
        //move事件的lastY在内部处理，因为要调整偏移量
        if (action != MotionEvent.ACTION_MOVE) {
            lastX = currX
            lastY = currY
        }
        return true
    }

    override fun handleScroll(currX: Float, currY: Float) {
        val deltaX = currX - lastX
        val deltaY = currY - lastY
        lastX = currX
        lastY = currY
        var unconsumed = deltaY.toInt()
        Log.e("testnested", "currY=${currY},lastY=${lastY}")
        if (dispatchNestedPreScroll(0,
                unconsumed,
                scrollConsumed,
                scrollOffset,
                ViewCompat.TYPE_TOUCH)
        ) {
            Log.e("testnestedscroll",
                "Parent consume pre scroll(T), consumed=${scrollConsumed[1]}")
            unconsumed -= scrollConsumed[1]
            nestedYOffset += scrollOffset[1]
            lastY -= scrollOffset[1]
        }
        if (unconsumed != 0 && canScrollVertically(-unconsumed)) {
            //防止滑出边界
            val selfConsume = getRealScrollDistance(unconsumed)
            Log.e("testnestedscroll",
                "Child consume scroll(T), scrollY=${scrollY},consumed=${selfConsume}")
            scrollBy(0, -selfConsume)
            unconsumed -= selfConsume
            dispatchNestedScroll(0,
                selfConsume,
                0,
                unconsumed,
                scrollOffset,
                ViewCompat.TYPE_TOUCH, scrollConsumed)
            nestedYOffset += scrollOffset[1]
            lastY -= scrollOffset[1]
        }

        if (unconsumed != 0) {

            Log.e("testnested",
                "Parent consume scroll(N-T), consumed=${scrollConsumed[1]}")
            unconsumed -= scrollConsumed[1]
            if (dispatchNestedScroll(0,
                    deltaY.toInt() - unconsumed,
                    0,
                    unconsumed,
                    scrollOffset,
                    ViewCompat.TYPE_TOUCH)
            ) {
                Log.e("testnested",
                    "Parent consume scroll(T), consumed=${scrollConsumed[1]}")
                nestedYOffset += scrollOffset[1]
                lastY -= scrollOffset[1]

            }
        }
        totalUnConsumed += unconsumed
    }

    override fun canScrollVertically(direction: Int): Boolean {
        val offset = computeVerticalScrollOffset()
        val range = computeVerticalScrollRange() - computeVerticalScrollExtent()
        if (range == 0) return false
        val result = if (direction < 0) {
            offset > 0
        } else {
            offset < range - 1
        }
        Log.e("canScrollVertically", "$result - offset=$offset - range=$range")
        return result
    }

    override fun touchUp() {
        velocityTracker.computeCurrentVelocity(1000, maxFlingVelocity.toFloat())
        val yVelocity = velocityTracker.yVelocity
        if (abs(yVelocity) >= minFlingVelocity) {
            Log.e("testnestedfling", "velocityY=${yVelocity}")
            if (!dispatchNestedPreFling(0f, yVelocity)) {
                flingWithOverScroller(-yVelocity)
                Log.e("testnestedfling", "Child消费fling velocityY=${yVelocity} scrollY=${scrollY}")
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_NON_TOUCH)
                ViewCompat.postInvalidateOnAnimation(this)
            }
        }
    }

    override fun computeScroll() {
        if (overScroller.computeScrollOffset()) {
            val deltaY = overScroller.currY - lastFlingY
            var unconsumed = deltaY
            lastFlingY = overScroller.currY
            Log.e("testnestedfling",
                "computeScroll velocityY=${overScroller.currVelocity} currY=${overScroller.currY}")
            if (dispatchNestedPreScroll(0,
                    unconsumed,
                    scrollConsumed,
                    null,
                    ViewCompat.TYPE_NON_TOUCH)
            ) {
                Log.e("testnested",
                    "Parent consume pre scroll(N-T), consumed=${scrollConsumed[1]}")
                unconsumed -= scrollConsumed[1]
            }
            if (unconsumed != 0 && canScrollVertically(-unconsumed)) {
                //防止滑出边界
                val selfConsume = getRealScrollDistance(unconsumed)
                Log.e("testnestedscroll",
                    "Child consume scroll(N-T), scrollY=${scrollY},consumed=${selfConsume}")
                scrollBy(0, -selfConsume)
                unconsumed -= selfConsume
                dispatchNestedScroll(0,
                    selfConsume,
                    0,
                    unconsumed,
                    null,
                    ViewCompat.TYPE_NON_TOUCH, scrollConsumed)
                Log.e("testnested",
                    "Parent consume scroll(N-T), consumed=${scrollConsumed[1]}")
                unconsumed -= scrollConsumed[1]
            }
//            if (unconsumed != 0) {
//                overScroller.abortAnimation()
//                stopNestedScroll(ViewCompat.TYPE_NON_TOUCH)
//            }
        }
        if (!overScroller.isFinished) {
            ViewCompat.postInvalidateOnAnimation(this)
        } else {
            stopNestedScroll(ViewCompat.TYPE_NON_TOUCH)
        }
        awakenScrollBars()

    }

    //    override fun computeVerticalScrollOffset(): Int {
//        return scrollY - totalParentConsumeScrollY
//    }

    override fun getRealScrollDistance(deltaY: Int): Int {
        return limitRange(deltaY,
            scrollY,
            -getScrollRange() + scrollY)
    }

    override fun startNestedScroll(axes: Int, type: Int): Boolean {
        return childHelper.startNestedScroll(axes, type)
    }

    override fun stopNestedScroll(type: Int) {
        return childHelper.stopNestedScroll(type)
    }

    override fun hasNestedScrollingParent(type: Int): Boolean {
        return childHelper.hasNestedScrollingParent(type)
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
        childHelper.dispatchNestedScroll(dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            offsetInWindow,
            type,
            consumed)
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        type: Int,
    ): Boolean {
        return childHelper.dispatchNestedScroll(dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            offsetInWindow,
            type)
    }

    /**
     * 在滑动之前，将滑动值分发给NestedScrollingParent
     * @param dx 水平方向消费的距离
     * @param dy 垂直方向消费的距离
     * @param consumed 输出坐标数组，consumed[0]为NestedScrollingParent消耗的水平距离、
     * consumed[1]为NestedScrollingParent消耗的垂直距离，此参数可空。
     * @param offsetInWindow 含有View从此方法调用之前到调用完成后的屏幕坐标偏移量，
     * 可以使用这个偏移量来调整预期的输入坐标（即上面4个消费、剩余的距离）跟踪，此参数可空。
     * @return 返回NestedScrollingParent是否消费部分或全部滑动值
     */
    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?,
        type: Int,
    ): Boolean {
        return childHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)
    }

    override fun dispatchNestedFling(
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean,
    ): Boolean {
        Log.e("testnestedfling", "dispatchNestedFling")
        return childHelper.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        Log.e("testnestedfling", "dispatchNestedPreFling")
        return childHelper.dispatchNestedPreFling(velocityX, velocityY)
    }
}