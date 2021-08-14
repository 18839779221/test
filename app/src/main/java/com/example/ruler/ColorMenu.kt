package com.example.ruler

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import androidx.core.view.children
import com.example.utils.dp2Px
import com.example.utils.hasGravity
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
        arrayOf(0xFFFFFF, 0x000000, 0x993333, 0x0066CC, 0x006633).map { transformToColorInt(it) }

    private val colorViews = arrayOfNulls<ColorView>(colors.size)
    private var minRadius = dp2Px(context, 65)
    private var radius = minRadius
    private var betweenItemMargin = 5

    private var centerPoint: Point? = null
    private var inCorner = true //centerPoint是否在四个角，90度的半径和180度的半径不一样，radius为180度时的半径

    var onSelectedColor: (color: Int) -> Unit = {}

    private var layoutSize = -1

    private var inToggle = false
    var isExpand = false

    init {
        initColorViews()
    }

    private fun initColorViews() {
        for (i in colorViews.indices) {
            val view =
                ColorView(context = context, color = colors[i])
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
//
//    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
//        val realRadius: Int = if (inCorner) (radius * 1.75).toInt() else radius
//        if (centerPoint == null) return
//        for (child in colorViews) {
//            if (child == null) continue
//            val hW = child.measuredWidth / 2
//            val hH = child.measuredHeight / 2
//            val radian = Math.toRadians(child.angle.toDouble())
//            val childCenterX = centerPoint!!.x + realRadius * cos(radian)
//            val childCenterY = centerPoint!!.y + realRadius * sin(radian)
//            child.layout(
//                (childCenterX - hW).toInt(),
//                (childCenterY - hH).toInt(),
//                (childCenterX + hW).toInt(),
//                (childCenterY + hH).toInt()
//            )
//        }
//    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (centerPoint == null) return
        val left = centerPoint!!.x - itemWidth / 2
        val top = centerPoint!!.y - itemWidth / 2
        for (child in colorViews) {
            if (child == null) continue
            child.layout(left, top, left + itemWidth, top + itemWidth)
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

    fun getLayoutSize(): Int {
        if (layoutSize != -1) return layoutSize
        radius = calculateRadius()
        val layoutPadding = 10
        layoutSize = radius * 2 + itemWidth + layoutPadding * 2
        return layoutSize
    }

    private fun calculateRadius(): Int {
        if (colorViews.size < 2) {
            return minRadius
        }
        val perAngle = (180f / (colorViews.size - 1))
        val perHalfRadian = Math.toRadians((perAngle / 2).toDouble())
        return max(((itemWidth / 2 + betweenItemMargin) / sin(perHalfRadian)).toInt(), minRadius)
    }

    private fun setAngles(fromAngle: Float, toAngle: Float) {
        val perAngle = (toAngle - fromAngle) / (colorViews.size - 1)
        colorViews.forEachIndexed { i, view ->
            view?.angle = (fromAngle + perAngle * i)
        }
    }

    fun show(centerViewWidth: Int, centerViewGravity: Int) {
        if (inToggle) return
        changeLayout(centerViewWidth, centerViewGravity)
        isExpand = true
        toggle(0f, 1f)
    }

    fun hide() {
        if (inToggle) return
        isExpand = false
        toggle(1f, 0f)
    }

    @SuppressLint("Recycle")
    private fun toggle(from: Float, to: Float) {
        inToggle = true
        ValueAnimator.ofFloat(from, to).apply {
            duration = 100
            interpolator = AccelerateInterpolator()
            addUpdateListener {
                updateViewInToggle(it.animatedValue as Float)
            }
            start()
        }
        inToggle = false
    }

    private fun updateViewInToggle(percentage: Float){
        if (centerPoint == null) return
        visibility = if (percentage == 0f) GONE else VISIBLE
        val realMaxRadius: Int = if (inCorner) (radius * 1.75).toInt() else radius
        val realRadius = realMaxRadius * percentage
        for (child in colorViews) {
            if (child == null) continue
            val radian = Math.toRadians(child.angle.toDouble())
            child.translationX = (realRadius * cos(radian)).toFloat()
            child.translationY = (realRadius * sin(radian)).toFloat()
        }
    }


    private fun afterSelectedColor(color: Int) {
        onSelectedColor(color)
    }

    inner class ColorView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        color: Int = Color.BLACK,
    ) : View(context, attrs, defStyleAttr) {

        var angle = 0f

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            setColor(color)
            style = Paint.Style.FILL_AND_STROKE
        }

        init {
            setOnClickListener {
                afterSelectedColor(color)
            }
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
    }

}