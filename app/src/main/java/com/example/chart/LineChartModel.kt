package com.example.chart

import android.graphics.Color

data class LineChartModel(
    val dataSetList: List<DataSetWrapper>,
    var timeList: List<String>,
    val labelList: List<String>,
    val timeValueFormatter: (index: Int, timeList: List<String>) -> String = defaultTimeValueFormatter,
) {

    init {
//        dataSetList.forEach {
//            it.dataSet = it.dataSet.toMutableList().apply {
//                add(0, "${Integer.MIN_VALUE}")
////                add(0, "${Integer.MIN_VALUE}")
////                add(size - 1, "${Integer.MIN_VALUE}")
//                add(size - 1, "${Integer.MIN_VALUE}")
//            }
//        }
//        timeList = timeList.toMutableList().apply {
//            add(0, "")
////            add(0, "")
////            add(size - 1, "")
//            add(size - 1, "")
//        }
    }

    companion object {

        val defaultTimeValueFormatter = { index: Int, timeList: List<String> ->
            timeList[index % timeList.size]
        }

        val EMPTY = LineChartModel(
            dataSetList = listOf(DataSetWrapper(listOf("0", "0", "0", "0", "0", "0", "0", "0"), true, Color.TRANSPARENT)),
            timeList = listOf("", "", "", "", "", "", "", ""),
            labelList = listOf(""),
            timeValueFormatter = { _, _ -> "" }
        )
    }
}

data class DataSetWrapper(
    var dataSet: List<String>,
    val isLeft: Boolean, // 对应y坐标轴是否在左边
    val lineColor: Int = Color.parseColor("#DFE0E2"), // 对应折线颜色，如果不填则使用默认颜色
)
