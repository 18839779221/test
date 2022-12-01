package com.example.chart

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.renderer.LineChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * @author wanglun
 * @date 2022/11/15
 * @description 为解决仅在选中当前行时绘制圆圈的需求
 */
class HighlightWithCircleLineChartRender(
    chart: LineDataProvider?, animator: ChartAnimator?,
    viewPortHandler: ViewPortHandler?,
) : LineChartRenderer(chart, animator, viewPortHandler) {

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLACK
    }

    override fun drawHighlighted(c: Canvas?, indices: Array<out Highlight>?) {
        super.drawHighlighted(c, indices)
        drawHighlightCircle(c, indices)
    }

    private fun drawHighlightCircle(c: Canvas?, indices: Array<out Highlight>?) {
        c ?: return
        indices?.forEach {
            val dataSet = mChart.lineData.dataSets.getOrNull(it.dataSetIndex)
            if (dataSet != null) {
                if (dataSet.circleColorCount <= 0) return@forEach
                android.util.Log.e("MPAndroidChart", "index: ${it.dataSetIndex}, color: ${dataSet.getCircleColor(0)}")
                circlePaint.color = dataSet.getCircleColor(0)
                c.drawCircle(it.drawX, it.drawY, dataSet.circleRadius, circlePaint)
                circlePaint.color = dataSet.circleHoleColor
                c.drawCircle(it.drawX, it.drawY, dataSet.circleHoleRadius, circlePaint)
            }

        }
    }

}