package com.example.shadow

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.utils.dp2Px

/**
 * @author wanglun
 * @date 2021/09/09
 * @description
 */
class ShadowView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private val mCornerRect = RectF()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG).apply {
        color = Color.argb(100,180,180,180)
    }

    override fun onDraw(canvas: Canvas?) {
        val cornerRadius = dp2Px(context, 8)
        val outRect = Rect()
        getDrawingRect(outRect)
        val bounds = RectF(outRect)
        val twoRadius = cornerRadius * 2
        val innerWidth: Float = bounds.width() - twoRadius - 1
        val innerHeight: Float = bounds.height() - twoRadius - 1
        if (cornerRadius >= 1f) {
            // increment corner radius to account for half pixels.
            val roundedCornerRadius: Float = cornerRadius + .5f
            mCornerRect.set(-roundedCornerRadius, -roundedCornerRadius, roundedCornerRadius,
                roundedCornerRadius)
            val saved = canvas!!.save()
            canvas.translate(bounds.left + roundedCornerRadius,
                bounds.top + roundedCornerRadius)
            canvas.drawArc(mCornerRect, 180f, 90f, true, paint)
            canvas.translate(innerWidth, 0f)
            canvas.rotate(90f)
            canvas.drawArc(mCornerRect, 180f, 90f, true, paint)
            canvas.translate(innerHeight, 0f)
            canvas.rotate(90f)
            canvas.drawArc(mCornerRect, 180f, 90f, true, paint)
            canvas.translate(innerWidth, 0f)
            canvas.rotate(90f)
            canvas.drawArc(mCornerRect, 180f, 90f, true, paint)
            canvas.restoreToCount(saved)
            //draw top and bottom pieces
            canvas.drawRect(bounds.left + roundedCornerRadius - 1f, bounds.top,
                bounds.right - roundedCornerRadius + 1f,
                bounds.top + roundedCornerRadius, paint)
            canvas.drawRect(bounds.left + roundedCornerRadius - 1f,
                bounds.bottom - roundedCornerRadius,
                bounds.right - roundedCornerRadius + 1f, bounds.bottom, paint)
        }
        // center
        // center
        canvas!!.drawRect(bounds.left, bounds.top + cornerRadius,
            bounds.right, bounds.bottom - cornerRadius, paint)

    }
}