package com.example.animation

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.view.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * @author wanglun
 * @date 2021/08/05
 * @description RulerView的控制组件
 */
class FloatingLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private val colors =
        arrayOf(0xFFFFFF, 0x000000, 0xFFFFCC, 0xCCFFFF, 0xFFCCCC).map { transformToColorInt(it) }

    private val colorViews = arrayOfNulls<OptionColorView>(colors.size)
    private lateinit var mainButton: FloatingActionButton

    private val buttonColors = arrayOf(0x009966, 0x9933CC).map { transformToColorInt(it) }
    private var buttonIndex = 1

    private var totalWidth = 0

    //是否展开颜色面板，长按展开
    private var isExpand = false

    var onMainButtonClick: () -> Unit = {}

    init {
        initView()
    }

    private fun initView() {
        setWillNotDraw(false)
        initMainButton()
        post {
            val layoutParams = this.layoutParams
            layoutParams.width = width
            layoutParams.height = height
            this.layoutParams = layoutParams
            totalWidth = min(width, height)
            initColorViews()
        }
        switchColor()
        mainButton.setOnClickListener {
            rotate(mainButton)
            onMainButtonClick()
        }
        mainButton.setOnLongClickListener {
            isExpand = true
            showOptionColorPanel(mainButton)
            return@setOnLongClickListener true
        }

    }

    private fun initMainButton() {
        mainButton = FloatingActionButton(context)
        val layoutParams = MarginLayoutParams(150, 150)
        layoutParams.setMargins(60, 60, 60, 60)
        mainButton.layoutParams = layoutParams
        addView(mainButton)
    }

    private fun initColorViews() {
        if (totalWidth - mainButton.width <= 0) return
        val colorViewWidth = totalWidth - mainButton.width
        for (i in colorViews.indices) {
            val view =
                OptionColorView(context = context, color = colors[i], width = colorViewWidth)
            val layoutParams = LayoutParams(width, width)
            this@FloatingLayout.addView(view, layoutParams)
            colorViews[i] = view
        }
    }

    private fun showOptionColorPanel(view: View) {
        val parent = view.parent as ViewGroup
        val leftEnough = view.x > totalWidth
        val rightEnough = (parent.width - view.x) > totalWidth
        val topEnough = view.y > totalWidth
        val bottomEnough = (parent.height - view.y) > totalWidth

        if (totalWidth - view.width <= 0) return
        //计算optionColorPanel剩余的宽度
        val optionColorPanelWidth = totalWidth - view.width

        val startAngle = 90f
        val endAngle = 270f
        val perAngle = (endAngle - startAngle) / (colorViews.size - 1)
        val translationDistance = (totalWidth / 2).toFloat()
        colorViews.forEachIndexed { index, view ->
            if (view == null) return@forEachIndexed
            val angle = startAngle + perAngle * index
            view.translation(translationDistance, angle)
        }
    }

    //翻转控件的同时切换颜色
    private fun rotate(view: View) {
        var colorSwitched = false
        ValueAnimator.ofFloat(0f, 180f).apply {
            duration = 500
            addUpdateListener {
                val rotate = it.animatedValue as Float
                view.rotationX = rotate
                if (!colorSwitched && rotate >= 90f) {
                    colorSwitched = true
                    switchColor()
                }
            }
            start()
        }
    }

    private fun switchColor() {
        mainButton.backgroundTintList = ColorStateList.valueOf(buttonColors[(buttonIndex)])
        buttonIndex = (buttonIndex + 1) % buttonColors.size
    }

    private fun transformToColorInt(color: Int): Int {
        return Color.rgb(
            color and 0xff0000 shr 16,
            color and 0x00ff00 shr 8,
            color and 0x0000ff
        )
    }

    //不论外部如何设置，mode统一为MeasureSpec.EXACTLY，width和height统一根据子控件大小重新计算
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mode = MeasureSpec.EXACTLY
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        val colorViewWidth = if (colorViews.isNotEmpty() && colorViews[0] != null) {
            colorViews[0]!!.measuredWidth + colorViews[0]!!.marginLeft + colorViews[0]!!.marginRight
        } else {
            0
        }
        val width = if (isExpand) {
            mainButton.measuredWidth + colorViewWidth
        } else {
            max(
                mainButton.measuredWidth + mainButton.marginLeft + mainButton.marginRight,
                colorViewWidth
            )
        }
        val measureSpec = MeasureSpec.makeMeasureSpec(width, mode)
        setMeasuredDimension(measureSpec, measureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (child in children) {
            val cLeft = l + child.marginLeft
            val cTop = t + child.marginTop
            child.layout(cLeft, cTop, cLeft + child.measuredWidth, cTop + child.measuredHeight)
        }
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
    }

    inner class OptionColorView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        color: Int = Color.BLACK,
        width: Int = 0
    ) : View(context, attrs, defStyleAttr) {

        private val radius = (width * 1 / 3).toFloat()

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
            canvas.drawCircle(center, center, radius, paint)
        }

        fun translation(distance: Float, angle: Float) {
            val distanceX = distance * sin(angle)
            val distanceY = distance * cos(angle)
            val holderX = PropertyValuesHolder.ofFloat("translationX", 0f, distanceX)
            val holderY = PropertyValuesHolder.ofFloat("translationY", 0f, distanceY)
            ObjectAnimator.ofPropertyValuesHolder(this, holderX, holderY).apply {
                duration = 1000
                start()
            }

        }
    }
}