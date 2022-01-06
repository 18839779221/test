package com.example.window

import android.R.attr
import android.content.Context
import android.view.WindowManager
import android.view.Gravity

import android.R.attr.gravity
import android.os.Build
import com.example.utils.dp2Px


/**
 * @author wanglun
 * @date 2021/12/09
 * @description
 */
object MessageQueueWindowManager {

    fun addMessageQueueWindow(context: Context) {
        FloatingManager.getInstance(context)
            .addView(MessageQueueView(context).apply {

            }, WindowManager.LayoutParams().apply {
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
                }
                flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                width = dp2Px(context,80)
                height = dp2Px(context,80)
            })

    }
}