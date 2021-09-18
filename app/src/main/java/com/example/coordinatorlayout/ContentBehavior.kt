package com.example.coordinatorlayout

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
import com.example.test.R
import com.example.utils.limitRange

/**
 * @author wanglun
 * @date 2021/09/09
 * @description
 */
class ContentBehavior: Behavior<View>() {

    private var dependentViewHeight = 0

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        if (dependency.id == R.id.top_view){
            dependentViewHeight = dependency.height
            return true
        }
        return false
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        child.translationY = dependency.height + dependency.translationY
        return true
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        if (child.translationY >= dependentViewHeight){
            if (child.canScrollVertically(-dy)){
                val realDeltaY = limitRange(dy + child.scrollY, child.height,0)
            }
        }
    }

}