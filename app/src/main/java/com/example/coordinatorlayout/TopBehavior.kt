package com.example.coordinatorlayout

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.example.utils.limitRange

/**
 * @author wanglun
 * @date 2021/09/14
 * @description
 */
class TopBehavior: CoordinatorLayout.Behavior<View>() {

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        val height = child.height
        val oldTranslationY = child.translationY
        val newTranslationY = limitRange(dy + oldTranslationY, height.toFloat(), 0f)
        if (newTranslationY != oldTranslationY){
            child.translationY = newTranslationY
            consumed[1] = (newTranslationY - oldTranslationY).toInt()
        }
    }
}