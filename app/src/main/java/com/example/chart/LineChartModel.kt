package com.example.chart

data class LineChartModel(
    val dataSetList: List<List<String>?>,
    val timeList: List<String>,
    val labelList: List<String?>,
) {
    companion object {
        // 重排图表数据集，希望对于prev和curr中同时存在的item，其展示效果不变（在list中的index不变）
        // 需要保证prev和curr的size一样（相同的size中，部分元素可为空）
        fun resortChartDataSet(prev: LineChartModel?, curr: LineChartModel): LineChartModel {
            if (prev == null ||
                prev === curr ||
                prev.labelList.size != curr.labelList.size
            ) return curr
            val targetDataSetList = curr.dataSetList.toMutableList()
            val targetLabelList = curr.labelList.toMutableList()
            for (prevIndex in 0 until prev.dataSetList.size) {
                val currIndex = curr.labelList.indexOfFirst { it == prev.labelList[prevIndex] }
                if (currIndex != -1) {
                    // swap index
                    val data1 = targetDataSetList[currIndex]
                    val data2 = targetDataSetList[prevIndex]
                    targetDataSetList[currIndex] = data2
                    targetDataSetList[prevIndex] = data1

                    val label1 = targetLabelList[currIndex]
                    val label2 = targetLabelList[prevIndex]
                    targetLabelList[currIndex] = label2
                    targetLabelList[prevIndex] = label1
                }
            }
            return LineChartModel(
                targetDataSetList,
                curr.timeList,
                targetLabelList
            )
        }
    }
}