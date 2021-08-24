package com.example.scroll

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.LinearLayout
import android.widget.OverScroller
import android.widget.Scroller
import androidx.core.view.children
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import com.example.utils.limitRange
import kotlin.math.abs

/**
 * @author wanglun
 * @date 2021/08/19
 * @description
 */
open class ScrollLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private var totalHeight: Int = 0
    private val vc = ViewConfiguration.get(context)
    private val touchSlop: Int = vc.scaledTouchSlop

    private val velocityTracker = VelocityTracker.obtain()

    private val scroller = Scroller(context)
    private val overScroller = OverScroller(context)

    private var downX = 0f
    private var downY = 0f
    private var lastX = 0f
    private var lastY = 0f

    init {
        isVerticalScrollBarEnabled = true
        scrollBarStyle = View.SCROLLBARS_INSIDE_INSET
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (orientation == VERTICAL) {
            var totalLength = paddingTop + paddingBottom
            for (child in children) {
                totalLength += child.marginTop + child.measuredHeight + child.marginBottom
            }
            totalHeight = totalLength
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false
        val action = event.action
        val currX = event.x
        var currY = event.y
        velocityTracker.addMovement(event)
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
        lastX = currX
        lastY = currY
        return true
    }

    private fun touchDown(currX: Float, currY: Float) {
        downX = currX
        downY = currY
        if (!scroller.isFinished) {
            scroller.abortAnimation()
        }
    }

    private fun touchMove(currX: Float, currY: Float) {
        handleScroll(currX, currY)
    }

    private fun handleScroll(currX: Float, currY: Float) {
        val deltaX = currX - lastX
        val deltaY = currY - lastY
//        if (abs(deltaX) < touchSlop && abs(deltaY) < touchSlop) return
        if (canScrollVertically(1) || canScrollVertically(-1)) {
            //防止滑出边界
            val realDeltaY = limitRange(-deltaY.toInt(), totalHeight - height - scrollY, -scrollY)
            scrollBy(0, realDeltaY)
        }
        awakenScrollBars()
    }

    override fun computeVerticalScrollRange(): Int {
        return totalHeight
    }

    private fun touchUp() {
        velocityTracker.computeCurrentVelocity(1000, vc.scaledMaximumFlingVelocity.toFloat())
        val yVelocity = velocityTracker.yVelocity
        if (abs(yVelocity) >= vc.scaledMinimumFlingVelocity) {
            flingWithOverScroller(yVelocity)
        }
    }

    private fun flingWithOverScroller(yVelocity: Float) {
        overScroller.fling(scrollX,
            scrollY,
            0,
            -yVelocity.toInt(),
            0,
            scrollX,
            0,
            getScrollRange())
        invalidate()
    }

    private fun flingWithScroll(yVelocity: Float) {
        scroller.fling(scrollX, scrollY, 0, -yVelocity.toInt(), 0, scrollX, 0, getScrollRange())
        invalidate()
    }

    override fun computeScroll() {
        computeWithOverScroller()
        awakenScrollBars()
    }

    private fun computeWithScroller(){
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.currX, scroller.currY)
            postInvalidate()
        }
    }

    private fun computeWithOverScroller(){
        if (overScroller.computeScrollOffset()) {
            scrollTo(overScroller.currX, overScroller.currY)
            postInvalidate()
        }
    }

    override fun onDetachedFromWindow() {
        velocityTracker.recycle()
        super.onDetachedFromWindow()
    }

    private fun getScrollRange(): Int {
        return totalHeight - height
    }
}