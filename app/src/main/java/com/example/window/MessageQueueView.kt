package com.example.window

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import com.example.utils.dp2Px

/**
 * @author wanglun
 * @date 2021/12/09
 * @description
 */
class MessageQueueView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
): View(context, attrs, defStyleAttr) {

    init {
        background = ColorDrawable(Color.WHITE)
    }

    private val paint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.LTGRAY
    }

    override fun onDraw(canvas: Canvas) {

    }

}