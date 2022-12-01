package com.example.canvas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * @author wanglun
 * @date 2022/11/16
 * @description
 */
class CanvasView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private val colorList = listOf("#2A55E5", "#79D90C", "#F5B025", "#10A0B3", "#AF10B0")

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLACK
    }

    override fun onDraw(canvas: Canvas?) {
        canvas ?: return
        colorList.forEachIndexed { index, color ->
            circlePaint.color = Color.parseColor(color)
            canvas.drawCircle(50f, 50f + index * 50f, 25f, circlePaint)
            circlePaint.color = Color.WHITE
            canvas.drawCircle(50f, 50f + index * 50f, 15f, circlePaint)
        }

    }
}