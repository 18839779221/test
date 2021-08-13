package com.example.animation

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import com.example.utils.transformToColorInt
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.abs

/**
 * @author wanglun
 * @date 2021/08/05
 * @description RulerView的控制组件
 */
class FloatingBall @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var mainButton: FloatingActionButton

    private lateinit var colorMenu: ColorMenu

    private val buttonColors = arrayOf(0x009966, 0x9933CC).map { transformToColorInt(it) }
    private var buttonIndex = 1
    private val buttonWidth = 150

    private lateinit var mLayoutParams: LayoutParams

    private var lastX = 0f
    private var lastY = 0f
    private var downX = 0f
    private var downY = 0f
    private var touchSlop = 0
    private var isClick = false
    private var downTimeStamp = 0L
    private var isLongClick = false //isLongClick=true时此次事件序列不响应点击事件/长按事件/移动事件

    private var totalWidth = 0

    //是否展开颜色面板，长按展开
    private var isExpand = false

    var onMainButtonClick: () -> Unit = {}

    init {
        initView()
    }

    private fun initView() {
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
        //如果不设置isClickable，ViewGroup将无法相应滑动时间（只能接收到ACTION_DOWN事件）
        isClickable = true
        initMainButton()
        initColorMenu()
//        post {
//            initLayoutParams()
//        }
    }

    private fun initLayoutParams() {
        mLayoutParams = layoutParams as LayoutParams
        mLayoutParams.width = LayoutParams.WRAP_CONTENT
        mLayoutParams.height = LayoutParams.WRAP_CONTENT
        mLayoutParams.gravity = Gravity.BOTTOM
        this.layoutParams = mLayoutParams
    }

    private fun initColorMenu() {
        colorMenu = ColorMenu(context = context, itemWidth = buttonWidth)
        addView(colorMenu)
        colorMenu.visibility = GONE
    }

    private fun initMainButton() {
        mainButton = FloatingActionButton(context)
        val layoutParams = MarginLayoutParams(buttonWidth, buttonWidth)
        mainButton.layoutParams = layoutParams
        addView(mainButton)
        switchColor()
        mainButton.setOnClickListener {
            rotate(mainButton)
        }
        mainButton.setOnLongClickListener {
            val mainButtonGravity = changeMainButtonLayout()
            isExpand = true
            colorMenu.show(mainButton.width ,mainButtonGravity)
            return@setOnLongClickListener true
        }
    }

    private fun changeMainButtonLayout(): Int {
        val btnLp = LayoutParams(mainButton.layoutParams)
        val parent = parent as ViewGroup
        val colorMenuSize = colorMenu.getLayoutSize()
        btnLp.gravity = Gravity.NO_GRAVITY
        when {
            y - colorMenuSize < 0 -> {
                btnLp.gravity = btnLp.gravity or Gravity.TOP
            }
            parent.height - y < colorMenuSize -> {
                btnLp.gravity = btnLp.gravity or Gravity.BOTTOM
            }
            else -> {
                btnLp.gravity = btnLp.gravity or Gravity.CENTER_VERTICAL
            }
        }
        when {
            x - colorMenuSize < 0 -> {
                btnLp.gravity = btnLp.gravity or Gravity.LEFT
            }
            parent.width - x < colorMenuSize -> {
                btnLp.gravity = btnLp.gravity or Gravity.RIGHT
            }
            else -> {
                btnLp.gravity = btnLp.gravity or Gravity.CENTER_HORIZONTAL
            }
        }
        mainButton.layoutParams = btnLp
        return btnLp.gravity
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false
        val x = event.rawX
        val y = event.rawY
        Log.e("testevent", "${event.action}-$x-$y")
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchDown(x, y)
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(x, y)
            }
            MotionEvent.ACTION_UP -> {
                touchUp(x, y)
            }
        }
        //判断长按事件
        checkIfLongClick()
        return super.onTouchEvent(event)
    }

    private fun touchDown(x: Float, y: Float) {
        downX = x
        downY = y
        lastX = x
        lastY = y
        isClick = true
        isLongClick = false
        downTimeStamp = System.currentTimeMillis()
    }

    private fun touchMove(x: Float, y: Float) {
        if (isLongClick) return
        val totalDeltaX = x - downX
        val totalDeltaY = y - downY
        val deltaX = x - lastX
        val deltaY = y - lastY
        if (isClick && abs(totalDeltaX) > touchSlop || abs(totalDeltaY) > touchSlop) {
            isClick = false
        }
        lastX = x
        lastY = y
        if (!isClick) {
            onMove(deltaX, deltaY)
        }
    }

    private fun touchUp(x: Float, y: Float) {
        if (isLongClick) return
        if (isClick) {
            onClick()
        } else {
            moveToEdge()
        }
    }

    private fun checkIfLongClick() {
        if (isClick && !isLongClick && System.currentTimeMillis() - downTimeStamp > ViewConfiguration.getLongPressTimeout()) {
            isLongClick = true
            onLongClick()
        }
    }

    //浮球移到靠近的一边
    private fun moveToEdge() {
        val parent = parent as ViewGroup
        val centerX = parent.width / 2
        val destX = if (x > centerX) {
            parent.width - width
        } else {
            0
        }
        onMove(destX - x, 0f)
    }

    private fun onMove(deltaX: Float, deltaY: Float) {
        offsetLeftAndRight(deltaX.toInt())
        offsetTopAndBottom(deltaY.toInt())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
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

    //切换MainButton颜色
    private fun switchColor() {
        mainButton.backgroundTintList = ColorStateList.valueOf(buttonColors[(buttonIndex)])
        buttonIndex = (buttonIndex + 1) % buttonColors.size
    }

    private fun onClick() {
        mainButton.performClick()
        onMainButtonClick()
    }


    private fun onLongClick() {
        if (!isExpand) {
            mainButton.performLongClick()
        }
    }


    companion object {
        const val LEFT_TOP = 1
        const val LEFT_CENTER = 2
        const val LEFT_BOTTOM = 3
        const val RIGHT_TOP = 4
        const val RIGHT_CENTER = 5
        const val RIGHT_BOTTOM = 6
        const val TOP_CENTER = 7
        const val BOTTOM_CENTER = 8
        const val CENTER = 9
    }


}