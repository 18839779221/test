package com.example.animation

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.children
import com.example.utils.dp2Px
import com.example.utils.transformToColorInt
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

/**
 * @author wanglun
 * @date 2021/08/09
 * @description
 */
class ColorMenu @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, private val itemWidth: Int
) : ViewGroup(context, attrs, defStyleAttr) {

    private val colors =
        arrayOf(0xFFFFFF, 0x000000, 0xFFFFCC, 0xCCFFFF, 0xFFCCCC).map { transformToColorInt(it) }

    private val colorViews = arrayOfNulls<ColorView>(colors.size)
    private var minRadius = dp2Px(context, 65)
    private var radius = minRadius
    private var betweenItemMargin = 5

    private var centerPoint: Point? = null
    private var inCorner = true //centerPoint是否在四个角，90度的半径和180度的半径不一样，radius为180度时的半径

    init {
        initColorViews()
    }

    private fun initColorViews() {
        for (i in colorViews.indices) {
            val view =
                ColorView(context = context, color = colors[i], width = itemWidth)
            val layoutParams = FrameLayout.LayoutParams(width, width)
            addView(view, layoutParams)
            colorViews[i] = view
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = getLayoutSize()
        setMeasuredDimension(size, size)
        for (child in children) {
            child.measure(
                MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY)
            )
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val realRadius: Int = if (inCorner) (radius * 1.75).toInt() else radius
        if (centerPoint == null) return
        for (child in colorViews) {
            if (child == null) continue
            val hW = child.measuredWidth / 2
            val hH = child.measuredHeight / 2
            val radian = Math.toRadians(child.angle.toDouble())
            val childCenterX = centerPoint!!.x + realRadius * cos(radian)
            val childCenterY = centerPoint!!.y + realRadius * sin(radian)
            child.layout(
                (childCenterX - hW).toInt(),
                (childCenterY - hH).toInt(),
                (childCenterX + hW).toInt(),
                (childCenterY + hH).toInt()
            )
        }
    }

    private fun changeLayout(centerViewWidth: Int, gravity: Int) {
        val size = getLayoutSize()
        var fromAngle: Float
        var toAngle: Float
        when {
            hasGravity(gravity, Gravity.LEFT, Gravity.TOP) -> {
                fromAngle = 0f
                toAngle = 90f
                inCorner = true
                centerPoint = Point(centerViewWidth / 2, centerViewWidth / 2)
            }
            hasGravity(gravity, Gravity.RIGHT, Gravity.TOP) -> {
                fromAngle = 180f
                toAngle = 90f
                inCorner = true
                centerPoint = Point(size - centerViewWidth / 2, centerViewWidth / 2)
            }
            hasGravity(gravity, Gravity.LEFT, Gravity.BOTTOM) -> {
                fromAngle = 270f
                toAngle = 360f
                inCorner = true
                centerPoint = Point(centerViewWidth / 2, size - centerViewWidth / 2)
            }
            hasGravity(gravity, Gravity.RIGHT, Gravity.BOTTOM) -> {
                fromAngle = 270f
                toAngle = 180f
                inCorner = true
                centerPoint = Point(size - centerViewWidth / 2, size - centerViewWidth / 2)
            }
            hasGravity(gravity, Gravity.LEFT, Gravity.CENTER_VERTICAL) -> {
                fromAngle = 270f
                toAngle = 450f
                inCorner = false
                centerPoint = Point(centerViewWidth / 2, size / 2)
            }
            hasGravity(gravity, Gravity.RIGHT, Gravity.CENTER_VERTICAL) -> {
                fromAngle = 270f
                toAngle = 90f
                inCorner = false
                centerPoint = Point(size - centerViewWidth / 2, size / 2)
            }
            hasGravity(gravity, Gravity.TOP, Gravity.CENTER_HORIZONTAL) -> {
                fromAngle = 180f
                toAngle = 0f
                inCorner = false
                centerPoint = Point(size / 2, centerViewWidth / 2)
            }
            hasGravity(gravity, Gravity.BOTTOM, Gravity.CENTER_HORIZONTAL) -> {
                fromAngle = 180f
                toAngle = 360f
                inCorner = false
                centerPoint = Point(size / 2, size - centerViewWidth / 2)
            }
            else -> {
                fromAngle = 0f
                toAngle = 270f
                inCorner = false
                centerPoint = Point(size / 2, size / 2)
            }
        }
        setAngles(fromAngle, toAngle)
    }

    private fun hasGravity(mixedGravity: Int, singleGravity1: Int, singleGravity2: Int): Boolean {
        return hasGravity(mixedGravity, singleGravity1) && hasGravity(mixedGravity, singleGravity2)

    }

    private fun hasGravity(mixedGravity: Int, singleGravity: Int): Boolean {
        return mixedGravity and singleGravity == singleGravity
    }


    fun getLayoutSize(): Int {
        radius = calculateRadius()
        val layoutPadding = 10
        return radius * 2 + itemWidth + layoutPadding * 2
    }

    private fun calculateRadius(): Int {
        if (colorViews.size < 2) {
            return minRadius
        }
        val perAngle = (180f / (colorViews.size - 1))
        val perHalfRadian = Math.toRadians((perAngle / 2).toDouble())
        return max(((itemWidth / 2 + betweenItemMargin) / sin(perHalfRadian)).toInt(), minRadius)
    }

    fun show(centerViewWidth: Int, centerViewGravity: Int) {
        changeLayout(centerViewWidth, centerViewGravity)
        visibility = VISIBLE
    }

    private fun setAngles(fromAngle: Float, toAngle: Float) {
        val perAngle = (toAngle - fromAngle) / (colorViews.size - 1)
        colorViews.forEachIndexed { i, view ->
            view?.angle = (fromAngle + perAngle * i)
        }
    }

    fun hide() {

    }

    inner class ColorView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        color: Int = Color.BLACK,
        width: Int = 0
    ) : View(context, attrs, defStyleAttr) {

        var angle = 0f

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            setColor(color)
            style = Paint.Style.FILL_AND_STROKE
        }

        //不管父布局如何设置，根据构造方法传入的width进行测量
        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            if (width == 0) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
                return
            }
            val mode = MeasureSpec.EXACTLY
            val measureSpec = MeasureSpec.makeMeasureSpec(width, mode)
            setMeasuredDimension(measureSpec, measureSpec)
        }

        override fun onDraw(canvas: Canvas?) {
            if (canvas == null) return
            val center = (width / 2).toFloat()
            canvas.drawCircle(center, center, (width / 2).toFloat(), paint)
        }

        fun translation(distance: Float, angle: Float) {
            val radian = Math.toRadians(angle.toDouble()).toFloat()
            val distanceX = distance * sin(radian)
            val distanceY = distance * cos(radian)
            val holderX = PropertyValuesHolder.ofFloat("translationX", 0f, distanceX)
            val holderY = PropertyValuesHolder.ofFloat("translationY", 0f, distanceY)
            ObjectAnimator.ofPropertyValuesHolder(this, holderX, holderY).apply {
                duration = 1000
                start()
            }

        }
    }

}