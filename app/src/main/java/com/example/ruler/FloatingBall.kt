package com.example.ruler

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import com.example.utils.hasGravity
import com.example.utils.limitRange
import com.example.utils.transformToColorInt
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.abs

/**
 * @author wanglun
 * @date 2021/08/05
 * @description RulerView的控制组件
 */
class FloatingBall @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
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

    private var needAmendTranslation = false //是否需要修正偏移量，在打开或关闭colorMenu时需修正

    var onMainButtonClick: () -> Unit = {}

    init {
        initView()
    }

    private fun initView() {
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
        //如果不设置isClickable，ViewGroup将无法相应滑动时间（只能接收到ACTION_DOWN事件）
        isClickable = true
        initColorMenu()
        initMainButton()
    }

    private fun initMainButton() {
        mainButton = FloatingActionButton(context)
        val layoutParams = LayoutParams(buttonWidth, buttonWidth)
        layoutParams.gravity = Gravity.RIGHT or Gravity.BOTTOM
        addView(mainButton, layoutParams)
        switchColor()
        mainButton.setOnClickListener {
            rotate(mainButton)
            onMainButtonClick()
        }
        mainButton.setOnLongClickListener {
            toggleColorMenu()
            return@setOnLongClickListener true
        }
    }

    private fun initColorMenu() {
        colorMenu = ColorMenu(context = context, itemWidth = buttonWidth * 3 / 4)
        addView(colorMenu)
        colorMenu.visibility = GONE
    }

    fun setOnColorClick(listener: (color: Int) -> Unit) {
        colorMenu.onSelectedColor = {
            toggleColorMenu()
            listener(it)
        }
    }

    private fun toggleColorMenu() {
        if (colorMenu.isExpand) {
            colorMenu.hide()
        } else {
            val mainButtonGravity = changeMainButtonLayout()
            colorMenu.show(mainButton.width, mainButtonGravity)
        }
        needAmendTranslation = true
    }

    private fun changeMainButtonLayout(): Int {
        val btnLp = LayoutParams(mainButton.layoutParams)
        val parent = parent as ViewGroup
        val halfSize = colorMenu.getLayoutSize() / 2
        btnLp.gravity = Gravity.NO_GRAVITY
        val centerX = x + mainButton.width / 2
        val centerY = y + mainButton.width / 2
        when {
            centerY - halfSize < 0 -> {
                btnLp.gravity = btnLp.gravity or Gravity.TOP
            }
            parent.height - centerY < halfSize -> {
                btnLp.gravity = btnLp.gravity or Gravity.BOTTOM
            }
            else -> {
                btnLp.gravity = btnLp.gravity or Gravity.CENTER_VERTICAL
            }
        }
        when {
            centerX - halfSize < 0 -> {
                btnLp.gravity = btnLp.gravity or Gravity.LEFT
            }
            parent.width - centerX < halfSize -> {
                btnLp.gravity = btnLp.gravity or Gravity.RIGHT
            }
            else -> {
                btnLp.gravity = btnLp.gravity or Gravity.CENTER_HORIZONTAL
            }
        }
        mainButton.layoutParams = btnLp
        return btnLp.gravity
    }


    /**
     * 由于amendTranslation()中主要关心的变量为：
     * 1.mainButton.gravity 2.colorMenu.getLayoutSize() 3.FloatingBall.measureWidth 4.colorMenu.isExpand
     * mainButton.gravity在每次colorMenu展开后改变，onMeasure()会在mainButton.gravity更新后执行，没有问题
     * colorMenu.getLayoutSize()的值不会改变，没有问题
     * FloatingBall.measureWidth在onMeasure之后可能变化，这会影响amendTranslation()计算的偏移量
     * colorMenu.isExpand可以控制让其在amendTranslation()期间不改变，没有问题
     *
     * 于是就只有FloatingBall.measureWidth会对amendTranslation()产生影响
     * 我们应该：
     * 在要打开colorMenu时，在super.onMeasure()前调用amendTranslation()
     * 在要关闭colorMenu时，在super.onMeasure()后调用amendTranslation()
     *
     * 这样FloatingBall.measureWidth != colorMenu.getLayoutSize()才成立，才有可能计算出偏移量
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val flag = colorMenu.isExpand
        if (flag && needAmendTranslation) {
            amendTranslation()
            needAmendTranslation = false
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (!flag) {
            amendTranslation()
            needAmendTranslation = false
        }
    }

    /**
     * 修正FloatingBall的偏移量，由于视觉上我们拖动浮球时移动的是mainButton,但实际修改的是FloatingBall的偏移量(translation)
     * 在FloatingBall重新调用onLayout时（也就是FloatingBall内部元素UI变化时）,mainButton在FloatingBall内部的位置可能发生变化
     * FloatingBall的偏移量不变，导致看起来的结果是mainButton的实际位置发生了变化。
     * 预期的结果是mainButton的位置是不变的，因此需要修正FloatingBall的偏移量让mainButton正常显示
     */
    private fun amendTranslation() {
        val size = colorMenu.getLayoutSize()
        if (size == 0) return
        val btnLp = mainButton.layoutParams as LayoutParams
        val gravity = btnLp.gravity
        var amendX = 0
        var amendY = 0
        var w = measuredWidth
        when {
            hasGravity(gravity, Gravity.LEFT, Gravity.TOP) -> {
                amendX = size - w
                amendY = size - w
            }
            hasGravity(gravity, Gravity.RIGHT, Gravity.TOP) -> {
                amendY = size - w
            }
            hasGravity(gravity, Gravity.LEFT, Gravity.BOTTOM) -> {
                amendX = size - w
            }
            hasGravity(gravity, Gravity.RIGHT, Gravity.BOTTOM) -> {
            }
            hasGravity(gravity, Gravity.LEFT, Gravity.CENTER_VERTICAL) -> {
                amendX = size - w
                amendY = (size - w) / 2
            }
            hasGravity(gravity, Gravity.RIGHT, Gravity.CENTER_VERTICAL) -> {
                amendY = (size - w) / 2
            }
            hasGravity(gravity, Gravity.TOP, Gravity.CENTER_HORIZONTAL) -> {
                amendX = (size - w) / 2
                amendY = size - w
            }
            hasGravity(gravity, Gravity.BOTTOM, Gravity.CENTER_HORIZONTAL) -> {
                amendX = (size - w) / 2
            }
            hasGravity(gravity, Gravity.CENTER) -> {
                amendX = (size - w) / 2
                amendY = (size - w) / 2
            }
        }
        //要保证colorMenu.isExpand变量是状态切换过后的状态
        if (!colorMenu.isExpand) {
            amendX = -amendX
            amendY = -amendY
        }
        translationX += amendX
        translationY += amendY
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (!colorMenu.isExpand) {
            return true
        }
        return false
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false
        val x = event.rawX
        val y = event.rawY
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
        if (colorMenu.isExpand){
            toggleColorMenu()
            return
        }
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
        val centerParentX = parent.width / 2
        val centerX = x + width / 2
        val margin = 30
        val destX = if (centerX > centerParentX) {
            parent.width - width - margin
        } else {
            0 + margin
        }
        //防止滑出屏幕
        val destY = limitRange(y, (parent.height - width).toFloat(), 0f) - y
        onMove(destX - x, destY)
    }

    private fun onMove(deltaX: Float, deltaY: Float) {
        translationX += deltaX
        translationY += deltaY
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
        if (colorMenu.isExpand){
            toggleColorMenu()
            return
        }
        mainButton.performClick()
    }

    private fun onLongClick() {
        if (!colorMenu.isExpand) {
            mainButton.performLongClick()
        }
    }

}