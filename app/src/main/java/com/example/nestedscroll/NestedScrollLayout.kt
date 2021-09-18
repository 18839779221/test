package com.example.nestedscroll

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.OverScroller
import android.widget.Scroller
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import com.example.utils.limitRange
import kotlin.math.abs

/**
 * @author wanglun
 * @date 2021/08/19
 * @description copy from ScrollLayout
 */
open class NestedScrollLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private var totalHeight = 0
    private var visibleHeight = 0
    private val vc = ViewConfiguration.get(context)
    protected val touchSlop = vc.scaledTouchSlop
    protected val maxFlingVelocity = vc.scaledMaximumFlingVelocity
    protected val minFlingVelocity = vc.scaledMinimumFlingVelocity

    protected val velocityTracker = VelocityTracker.obtain()

    protected val overScroller = OverScroller(context)

    private var downX = 0f
    private var downY = 0f
    protected var lastX = 0f
    protected var lastY = 0f

    protected var lastFlingY = 0

    protected var isBeingDragged = false


    init {
        isVerticalScrollBarEnabled = true
        scrollBarStyle = View.SCROLLBARS_INSIDE_INSET
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        visibleHeight = measuredHeight
        if (orientation == VERTICAL) {
            var totalLength = paddingTop + paddingBottom
            for (child in children) {
                totalLength += child.marginTop + child.measuredHeight + child.marginBottom
            }
            totalHeight = totalLength
        }
        Log.e("totalLength", "${javaClass.name} - $totalHeight - $visibleHeight")
        setMeasuredDimension(measuredWidth, totalHeight)
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

    open fun touchDown(currX: Float, currY: Float) {
        downX = currX
        downY = currY
        if (!overScroller.isFinished) {
            overScroller.abortAnimation()
        }
    }

    open fun touchMove(currX: Float, currY: Float) {
        handleScroll(currX, currY)
    }

    open fun handleScroll(currX: Float, currY: Float) {
        val deltaX = currX - lastX
        val deltaY = currY - lastY
//        if (abs(deltaX) < touchSlop && abs(deltaY) < touchSlop) return
        if (canScrollVertically(-deltaY.toInt())) {
            //防止滑出边界
            val realDeltaY = getRealScrollDistance(deltaY.toInt())
            scrollBy(0, -realDeltaY)
        }
        awakenScrollBars()
    }

    override fun computeVerticalScrollExtent(): Int {
        return visibleHeight
    }

    open fun touchUp() {
        velocityTracker.computeCurrentVelocity(1000, maxFlingVelocity.toFloat())
        val yVelocity = velocityTracker.yVelocity
        if (abs(yVelocity) >= minFlingVelocity) {
            flingWithOverScroller(yVelocity)
        }
    }

    protected fun flingWithOverScroller(yVelocity: Float) {
        overScroller.abortAnimation()
        overScroller.fling(0,
            scrollY,
            0,
            -yVelocity.toInt(),
            0,
            0,
            Int.MIN_VALUE,
            Int.MAX_VALUE)
        lastFlingY = scrollY
        invalidate()
    }

    override fun computeScroll() {
        if (overScroller.computeScrollOffset()) {
            val deltaY = overScroller.currY - lastFlingY
            lastFlingY = overScroller.currY
            if(canScrollVertically(-deltaY)){
                val realScrollY = getRealScrollDistance(deltaY)
                scrollBy(0, -realScrollY)
                postInvalidate()
            }
        }
        awakenScrollBars()
    }

    override fun onDetachedFromWindow() {
        velocityTracker.recycle()
        super.onDetachedFromWindow()
    }

    fun setVisibleHeight(height: Int) {
        visibleHeight = height
    }

    fun getScrollRange(): Int {
        return totalHeight - visibleHeight
    }

    open fun getRealScrollDistance(deltaY: Int): Int {
        return limitRange(deltaY,
            scrollY,
            -getScrollRange() + scrollY)
    }
}