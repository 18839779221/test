package com.example.chart

import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import com.example.test.databinding.LineChartIndicatorBinding
import com.example.utils.dp2px
import com.example.utils.setMarginsRelative
import com.example.utils.setRoundCorner
import com.example.utils.setVisible
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import java.text.DecimalFormat

/**
 * @author wanglun
 * @date 2022/11/04
 * @description
 */
class ChartHelper {

    private var chartModel: LineChartModel? = null
    private val commonTextColor = Color.parseColor("#969AA0")
    private val commonGridColor = Color.parseColor("#DFE0E2")

    fun initChart(
        chartView: LineChart,
        indicatorBinding: LineChartIndicatorBinding,
        chartModel: LineChartModel,
    ) {
        this.chartModel = chartModel

        indicatorBinding.ivLabelItemLeft.setRoundCorner(0f, isOval = true)
        indicatorBinding.ivLabelItemRight.setRoundCorner(0f, isOval = true)

        // X轴
        configXAxis(chartView.xAxis, chartModel)

        // 左侧Y轴
        configYAxis(chartView.axisLeft)
        // 右侧Y轴
        configYAxis(chartView.axisRight)

        // 图表整体配置
        chartView.apply {
            setPinchZoom(false)
            isDoubleTapToZoomEnabled = false
            setScaleEnabled(false)
            description.isEnabled = false
            legend.isEnabled = false
            setBackgroundColor(Color.WHITE)
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    h ?: return
                    e ?: return
                    showChartIndicator(chartView, indicatorBinding, e, h)
                }

                override fun onNothingSelected() {}
            })
        }

        // 设置图标下方标识
        setLegends(chartView)

        updateChart(chartView, chartModel)

    }

    fun updateChart(
        chartView: LineChart,
        newChartModel: LineChartModel,
    ) {
        val realCharModel = LineChartModel.resortChartDataSet(this.chartModel, newChartModel)
        this.chartModel = realCharModel
        chartView.axisLeft.isEnabled = realCharModel.dataSetList[0] != null
        chartView.axisRight.isEnabled = realCharModel.dataSetList[1] != null

        val lineData = LineData().apply {
            setValueTextColor(Color.BLACK)
            setValueTextSize(9f)
        }
        val chartDataSetLeft = createDataSet(realCharModel, 0)
        val chartDataSetRight = createDataSet(realCharModel, 1)
        if (chartDataSetLeft != null) {
            lineData.addDataSet(chartDataSetLeft)
        }
        if (chartDataSetRight != null) {
            lineData.addDataSet(chartDataSetRight)
        }

        chartView.data = lineData
        chartView.notifyDataSetChanged()
        chartView.invalidate()

    }

    private fun createDataSet(chartModel: LineChartModel, index: Int): LineDataSet? {
        val isLeft = index % 2 == 0
        val entries = toEntries(chartModel.dataSetList.getOrNull(index))
        if (entries.isEmpty()) return null
        val label = chartModel.labelList.getOrNull(index) ?: return null
        val color = Color.parseColor(if (isLeft) "#2A55E5" else "#79D90C")
        return LineDataSet(entries, label).apply {
            axisDependency = if (isLeft) YAxis.AxisDependency.LEFT else YAxis.AxisDependency.RIGHT
            lineWidth = 2f
            setDrawCircles(true)
            this.color = color
            setCircleColor(color)
            circleRadius = dp2px(2f).toFloat()
            circleHoleRadius = dp2px(1.2f).toFloat()
            mode = LineDataSet.Mode.LINEAR
            setDrawHighlightIndicators(false)
            setDrawFilled(false)
            setDrawValues(false)
        }
    }

    private fun configXAxis(axis: XAxis, chartModel: LineChartModel) {
        val timePoints = chartModel.timeList
        axis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            textSize = 10f
            textColor = commonTextColor
            gridColor = commonGridColor
            axisLineColor = commonGridColor
            setLabelCount(timePoints.size, false)
            setDrawGridLines(true)
            setDrawAxisLine(false)
            enableAxisLineDashedLine(12f, 6f, 0f)
            enableGridDashedLine(12f, 6f, 0f)
            granularity = 1f
            valueFormatter = IAxisValueFormatter { value, _ ->
                return@IAxisValueFormatter timePoints[value.toInt() % timePoints.size]
            }
        }
    }

    private fun configYAxis(axis: YAxis) {
        axis.apply {
            textColor = commonTextColor
            setDrawZeroLine(true)
            setDrawAxisLine(false)
            gridColor = commonGridColor
            textSize = 10f
            setDrawGridLines(true)
            enableGridDashedLine(12f, 6f, 0f)
            setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
            setLabelCount(6, true)
            axisMinimum = 0f
            valueFormatter = IAxisValueFormatter { value: Float, axis: AxisBase? ->
                val last2Dot = DecimalFormat("0.00")
                val yValue = last2Dot.format(value.toDouble())
                preDealData(yValue)
            }
        }
    }

    private fun setLegends(chartView: LineChart) {
        chartView.legend.apply {
            isEnabled = true
            form = Legend.LegendForm.CIRCLE
            formSize = 6f
            textSize = 12f
            textColor = commonTextColor
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            orientation = Legend.LegendOrientation.HORIZONTAL
            xEntrySpace = 20f
            setDrawInside(false)
        }
    }

    private fun showChartIndicator(
        chartView: LineChart,
        indicatorBinding: LineChartIndicatorBinding,
        e: Entry,
        h: Highlight,
    ) {
        val chartModel = chartModel ?: return
        indicatorBinding.apply {
            val offsetTop: Int = 20
            val params = FrameLayout.LayoutParams(floatIndicator.layoutParams)
            var targetX = h.xPx
            var targetY = h.yPx
            if (targetX + params.width > chartView.width) {
                targetX -= params.width - 20
            } else {
                targetX -= params.width / 2
                if (targetX < 0) {
                    targetX = 0f
                }
            }
            targetY -= params.height + 15
            params.setMarginsRelative(targetX.toInt(), (targetY + offsetTop).toInt(), 0, 0)
            floatIndicator.layoutParams = params
            floatIndicator.visibility = View.VISIBLE

            val position = e.x.toInt()
            indicatorTime.text = chartModel.timeList[position]

            val dataSetLeft = chartModel.dataSetList.getOrNull(0)
            indicatorItemLeft.setVisible(!dataSetLeft.isNullOrEmpty())
            if (!dataSetLeft.isNullOrEmpty()) {
                val value = dataSetLeft.getOrElse(position) { "" }
                val label = chartModel.labelList[0]
                tvLabelItemLeft.text = label
                tvValueItemLeft.text = value
            }
            val dataSetRight = chartModel.dataSetList.getOrNull(1)
            indicatorItemRight.setVisible(!dataSetRight.isNullOrEmpty())
            if (!dataSetRight.isNullOrEmpty()) {
                val value = dataSetRight.getOrElse(position) { "" }
                val label = chartModel.labelList[1]
                tvLabelItemRight.text = label
                tvValueItemRight.text = value
            }
        }
    }

    fun preDealData(value: String): String? {
        var value = value
        if (!value.contains("%")) {
            value = fetchThouFormat(fetchNumFormat(value).orEmpty()).orEmpty()
        }
        return value
    }

    fun preDealPercent(percent: String): String? {
        var percent = percent
        if ("--" != percent) {
            percent += '%'
        }
        return percent
    }


    fun fetchThouFormat(original: String): String? {
        if (original.endsWith(".0")) {
            return original.substring(0, original.length - 2)
        }
        return if (original.endsWith(".00")) {
            original.substring(0, original.length - 3)
        } else original
    }

    fun fetchNumFormat(original: String): String? {
        val last2Dot = DecimalFormat("0.00")
        val last3Dot = DecimalFormat("0.000")
        val last4Dot = DecimalFormat("0.0000")
        var target = 0.0
        try {
            target = original.toDouble()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (target >= 9999995000.00) {
            return last3Dot.format(target / 100000000) + "亿"
        }
        return if (target >= 99999950.00) last4Dot.format(target / 100000000) + "亿" else if (target > 9999.99) last2Dot.format(target / 10000) + "万" else original
    }

    private fun toEntries(dataSet: List<String>?): List<Entry> {
        return dataSet?.mapIndexed { index, item ->
            Entry(index.toFloat(), item.toFloat())
        }.orEmpty()
    }
}