package com.example.ruler


import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager

/**
 * @author: wanglun
 * @date: 12/1/20
 * @desc: 测量尺寸的自定义view
 */
class RulerView : View {

    companion object {
        const val PX_MODE = 0
        const val DP_MODE = 1
        val LINE_LEN_ARRAY = arrayOf(40f, 10f, 10f, 10f, 10f, 20f, 10f, 10f, 10f, 10f)
        const val PX_UNIT = 100f
    }

    private var mode = DP_MODE
    private val paintConfigs = arrayOf(PaintConfig(), PaintConfig())

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 16f
        strokeWidth = 2f
        color = Color.BLACK
        style = Paint.Style.STROKE
        pathEffect = null
    }

    private var mLastX = 0
    private var mLastY = 0

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {

    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {

    }

    init {
        initPaintConfig()
    }

    private fun initPaintConfig() {
        paintConfigs[PX_MODE].apply {
            loadWinSize(context, point)
            initPath(this)
        }
        paintConfigs[DP_MODE].apply {
            point.apply {
                point.x = px2dp(context, paintConfigs[PX_MODE].point.x.toFloat()).toInt()
                point.y = px2dp(context, paintConfigs[PX_MODE].point.y.toFloat()).toInt()
            }
            unit = dpToPx(context, paintConfigs[PX_MODE].unit)
            initPath(this)
        }
    }

    private fun initPath(paintConfig: PaintConfig) {
        paintConfig.apply {
            val subUnit = unit / LINE_LEN_ARRAY.size
            val xlen = (point.x / PX_UNIT + 1).toInt()
            for (i in 0 until xlen) {
                for (j in LINE_LEN_ARRAY.indices) {
                    path.moveTo((unit * i + j * subUnit).toFloat(), 0F)
                    path.lineTo((unit * i + j * subUnit).toFloat(), LINE_LEN_ARRAY[j])
                }
            }
            val ylen = (point.y / PX_UNIT + 1).toInt()
            for (i in 0 until ylen) {
                for (j in LINE_LEN_ARRAY.indices) {
                    path.moveTo(0F, (unit * i + j * subUnit).toFloat())
                    path.lineTo(LINE_LEN_ARRAY[j], (unit * i + j * subUnit).toFloat())
                }
            }
            path.moveTo(0F, 0F)
            path.lineTo(0F, dpToPx(context, point.y.toFloat()))
            path.moveTo(0F, 0F)
            path.lineTo(dpToPx(context, point.x.toFloat()), 0F)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paintConfigs[mode].apply {
            //绘制刻度
            canvas?.drawPath(path, paint)
            //绘制刻度文字
            val xlen = (point.x / PX_UNIT + 1).toInt()
            for (i in 1 until xlen) {
                canvas?.drawText((PX_UNIT * i).toInt().toString(), unit * i.toFloat(), LINE_LEN_ARRAY[0] + 20f, textPaint)
            }
            val ylen = (point.y / PX_UNIT + 1).toInt()
            for (i in 1 until ylen) {
                canvas?.drawText((PX_UNIT * i).toInt().toString(), LINE_LEN_ARRAY[0] + 20f, unit * i.toFloat(), textPaint)
            }
        }

    }


    private fun setMode(mode: Int) {
        if (mode < 0 || mode > 1) return
        this.mode = mode
        invalidate()
    }

    fun switchMode() {
        setMode(1 - mode)
    }

    /**
     * 获得屏幕高度
     *
     * @param ctx 上下文
     * @param winSize 屏幕尺寸
     */
    /**
     * 获得屏幕高度
     *
     * @param ctx 上下文
     * @param winSize 屏幕尺寸
     */
    private fun loadWinSize(ctx: Context, winSize: Point) {
        val wm = ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm?.defaultDisplay?.getMetrics(outMetrics)
        winSize.x = outMetrics.widthPixels
        winSize.y = outMetrics.heightPixels
    }

    private class PaintConfig {
        val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = 4f
            color = Color.BLACK
            style = Paint.Style.STROKE
            pathEffect = null
        }
        val path: Path = Path()
        val point: Point = Point()
        var unit: Float = PX_UNIT
    }

    /**
     * Value of px to value of dp.
     *
     * @param pxValue The value of px.
     * @return value of dp
     */
    private fun px2dp(context: Context, pxValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return pxValue / scale + 0.5f
    }

    /**
     * @param dps dp值
     * @return
     */
    private fun dpToPx(context: Context, dps: Float): Float {
        return Math.round(context.resources.displayMetrics.density * dps).toFloat()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.rawX.toInt()
        val y = event.rawY.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX: Int = x - mLastX
                val deltaY: Int = y - mLastY
                val translationX = (translationX + deltaX).toInt()
                val translationY = (translationY + deltaY).toInt()
                setTranslationX(translationX.toFloat())
                setTranslationY(translationY.toFloat())
            }
        }
        mLastX = x
        mLastY = y
        return true
    }

    fun setPaintColor(color: Int){
        paintConfigs.forEach {
            it.paint.color = color
        }
        textPaint.color = color
        invalidate()
    }


}