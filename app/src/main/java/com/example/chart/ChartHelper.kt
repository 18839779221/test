package com.example.chart

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.example.test.BuildConfig
import com.example.test.databinding.LineChartIndicatorBinding
import com.example.utils.dp2px
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
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
class ChartHelper(private val context: Context) {

    companion object {
        val chartLineColorList = listOf("#2A55E5", "#79D90C", "#F5B025", "#10A0B3", "#AF10B0")
        val fakerTimeList = listOf("00:00", "02:00", "04:00", "06:00", "08:00", "10:00", "12:00")
    }

    private var chartModel: LineChartModel? = null
    private val commonTextColor = Color.parseColor("#969AA0")
    private val commonGridColor = Color.parseColor("#DFE0E2")

    private val chartIndicatorPopWindow by lazy {
        ChartFloatIndicator(context)
    }

    fun initChart(
        chartView: LineChart,
        indicatorBinding: LineChartIndicatorBinding?,
        legendView: LegendView,
        chartModel: LineChartModel?,
    ) {
        this.chartModel = chartModel
        chartView.isLogEnabled = BuildConfig.DEBUG

        chartView.findViewTreeLifecycleOwner()?.lifecycle?.addObserver(chartIndicatorPopWindow)

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
            minOffset = 0f
            setExtraOffsets(0f, 15f, 0f, 15f)
            renderer = HighlightWithCircleLineChartRender(this, animator, viewPortHandler)
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    h ?: return
                    e ?: return
                    showChartIndicator(chartView, e, h)
                }

                override fun onNothingSelected() {}
            })
        }

        // 设置图标下方标识
//        setLegends(legendView, chartModel)

        if (chartModel != null) {
            updateChart(chartView, legendView, chartModel)
        }

    }

    fun updateChart(
        chartView: LineChart,
        legendView: LegendView,
        newChartModel: LineChartModel,
    ) {
        this.chartModel = newChartModel

        val lineData = LineData().apply {
            setValueTextColor(Color.BLACK)
            setValueTextSize(9f)
        }
        newChartModel.dataSetList.forEachIndexed { index, item ->
            createDataSet(newChartModel, index, item)?.let {
                lineData.addDataSet(it)
            }
        }
        setLegends(legendView, chartModel)
        configXAxis(chartView.xAxis, newChartModel)
        chartView.axisLeft.isEnabled = newChartModel.dataSetList.any { it.isLeft }
        chartView.axisRight.isEnabled = newChartModel.dataSetList.any { !it.isLeft }
        chartView.data = lineData
        chartView.notifyDataSetChanged()
        chartView.invalidate()
    }

    private fun createDataSet(chartModel: LineChartModel, index: Int, dataSetWrapper: DataSetWrapper): LineDataSet? {
        val isLeft = dataSetWrapper.isLeft
        val entries = toEntries(dataSetWrapper.dataSet)
        if (entries.isEmpty()) return null
        val label = chartModel.labelList.getOrNull(index) ?: return null
        val color = dataSetWrapper.lineColor
        return LineDataSet(entries, label).apply {
            axisDependency = if (isLeft) YAxis.AxisDependency.LEFT else YAxis.AxisDependency.RIGHT
            lineWidth = 2f
            setDrawCircles(false)
            this.color = color
            setCircleColor(color)
            circleRadius = dp2px(2f).toFloat()
            circleHoleRadius = dp2px(1.2f).toFloat()
            mode = LineDataSet.Mode.LINEAR
            highLightColor = Color.parseColor("#2A55E5")
            enableDashedHighlightLine(12f, 6f, 0f)
            setDrawHorizontalHighlightIndicator(false)
            setDrawFilled(false)
            setDrawValues(false)
        }
    }

    private fun configXAxis(axis: XAxis, chartModel: LineChartModel?) {
        val timePoints = chartModel?.timeList ?: fakerTimeList
        axis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            textSize = 10f
            textColor = commonTextColor
            gridColor = commonGridColor
            axisLineColor = commonGridColor
            setLabelCount(timePoints.size, false)
            setDrawGridLines(true)
            setDrawAxisLine(false)
            setAvoidFirstLastClipping(true)
            enableAxisLineDashedLine(12f, 6f, 0f)
            enableGridDashedLine(12f, 6f, 0f)
            granularity = 1f
            axisMinimum = -1f
            axisMaximum = timePoints.size.toFloat()
            valueFormatter = IAxisValueFormatter { value, _ ->
                val index = value.toInt()
                if (index < 0 || index >= timePoints.size) return@IAxisValueFormatter ""
                return@IAxisValueFormatter if (chartModel?.timeValueFormatter != null) {
                    chartModel.timeValueFormatter(index, timePoints)
                } else {
                    timePoints[index % timePoints.size]
                }
            }
        }
    }

    private fun configYAxis(axis: YAxis) {
        axis.apply {
            textColor = commonTextColor
            setDrawZeroLine(false)
            setDrawAxisLine(false)
            gridColor = commonGridColor
            textSize = 10f
            setDrawGridLines(true)
            enableGridDashedLine(12f, 6f, 0f)
            setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
            // 固定展示6个刻度，为了左右刻度对齐
            setLabelCount(6, true)
            axisMinimum = 0f
            valueFormatter = IAxisValueFormatter { value: Float, axis: AxisBase? ->
                val last2Dot = DecimalFormat("0.00")
                val yValue = last2Dot.format(value.toDouble())
                preDealData(yValue)
            }
        }
    }

    private fun setLegends(legendView: LegendView, chartModel: LineChartModel?) {
//        chartView.legend.apply {
//            isEnabled = false
//            form = Legend.LegendForm.CIRCLE
//            formSize = 6f
//            textSize = 12f
//            textColor = commonTextColor
//            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
//            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
//            orientation = Legend.LegendOrientation.HORIZONTAL
//            xEntrySpace = 20f
//            setDrawInside(false)
//            isWordWrapEnabled = true
//        }
        if (chartModel == null) return
        val legends = chartModel.labelList.mapIndexed { index, label ->
            chartModel.dataSetList[index].lineColor to label
        }
        legendView.setLegends(legends, dp2px(20f))
    }

    private fun showChartIndicator(
        chartView: LineChart,
        e: Entry,
        h: Highlight,
    ) {
        val chartModel = chartModel ?: return
        if (chartModel === LineChartModel.EMPTY) return
        chartIndicatorPopWindow.updateView(chartView, chartModel, e, h)

        // 将单个highlight分发为整个x轴列的highlight
        val position = e.x.toInt()
        val highlightValues = mutableListOf<Highlight>()
        for (index in 0 until chartView.data.dataSetCount) {
            if (position >= 0 && position < chartView.data.dataSets[index].entryCount) {
                val entry = chartView.data.dataSets[index].getEntryForIndex(position)
                highlightValues.add(Highlight(entry.x, entry.y, index))
            }
        }
        chartView.highlightValues(highlightValues.toTypedArray())
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
            Entry(index.toFloat(), item.toFloatOrNull() ?: 0f)
        }.orEmpty()
    }
}