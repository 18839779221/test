package com.example.utils

import android.content.Context
import android.graphics.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * @author wanglun
 * @date 2021/08/09
 * @description
 */
fun transformToColorInt(color: Int): Int {
    return Color.rgb(
        color and 0xff0000 shr 16,
        color and 0x00ff00 shr 8,
        color and 0x0000ff
    )
}

fun dp2Px(context: Context, dp: Int): Int {
    return (context.resources.displayMetrics.density * dp).roundToInt()
}

fun hasGravity(mixedGravity: Int, singleGravity1: Int, singleGravity2: Int): Boolean {
    return hasGravity(mixedGravity, singleGravity1) && hasGravity(mixedGravity, singleGravity2)

}

fun hasGravity(mixedGravity: Int, singleGravity: Int): Boolean {
    return mixedGravity and singleGravity == singleGravity
}

fun limitRange(value: Float, max: Float, min: Float): Float {
    return max(min(value, max), min)
}

fun limitRange(value: Int, max: Int, min: Int): Int {
    return max(min(value, max), min)
}
