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

    }

    fun getScrollRange(): Int {
        return 0
    }
}