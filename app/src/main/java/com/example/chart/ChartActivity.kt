package com.example.chart

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.test.databinding.ActivityChartBinding

/**
 * @author wanglun
 * @date 2022/11/03
 * @description
 */
class ChartActivity : AppCompatActivity() {

    private val chartHelper by lazy { ChartHelper(this) }

    private lateinit var binding: ActivityChartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.chartView.postDelayed({
            chartHelper.initChart(binding.chartView, null, binding.chartLegend,
                LineChartModel(
                    listOf(
                        DataSetWrapper(listOf("100", "30", "70", "20", "10", "12", "31", "32"), isLeft = true),
                        DataSetWrapper(listOf("1", "2", "5", "6", "5", "8", "12", "10"), isLeft = false, lineColor = Color.parseColor("#225a5a")),
                        DataSetWrapper(listOf("100", "30", "70", "20", "10", "12", "31", "32"), isLeft = true),
                        DataSetWrapper(listOf("1", "2", "5", "6", "5", "8", "12", "10"), isLeft = false),
                        DataSetWrapper(listOf("100", "30", "70", "20", "10", "12", "31", "32"), isLeft = true),
                    ),
                    listOf("1日", "2日", "3日", "4日", "5日", "6日", "7日", "8日"),
                    listOf(
                        "转化成本", "点击转化",
                        "经三科技有限公司", "阿卡丽撒看开点卡卡卡卡卡卡卡卡卡卡卡", "adasdsadsadsadsadsadasdas"
                    )
                ))
        }, 0)

//        binding.chartView.postDelayed({
//            chartHelper.initChart(binding.chartView, binding.chartIndicator,
//                LineChartModel(
//                    listOf(
//                        DataSetWrapper(isLeft = true, dataSet = listOf("100", "200", "400", "200", "500")),
//                        DataSetWrapper(isLeft = true, dataSet = listOf("100", "30", "70", "20", "10"))
//                    ),
//                    listOf("10:00", "11:00", "12:00", "13:00", "14:00"),
//                    listOf("现金消耗", "转化成本")
//                ))
//        }, 3000)
//
//        binding.chartView.postDelayed({
//            chartHelper.updateChart(binding.chartView, LineChartModel.EMPTY)
//        }, 3000)

//        binding.chartView.postDelayed({
//            chartHelper.updateChart(binding.chartView,
//                LineChartModel(
//                    listOf(
//                        listOf("100", "30", "70", "20", "10"),
//                        listOf("1", "2", "5", "6", "5"),
//                    ),
//                    listOf("10:00", "11:00", "12:00", "13:00", "14:00"),
//                    listOf("转化成本", "点击转化")
//                ))
//        }, 3000)

//        binding.chartView.postDelayed({
//            chartHelper.updateChart(binding.chartView,
//                LineChartModel(
//                    listOf(
//                        listOf("1", "2", "5", "6", "5"),
//                        null,
//                    ),
//                    listOf("10:00", "11:00", "12:00", "13:00", "14:00"),
//                    listOf("点击转化", null)
//                ))
//        }, 6000)
    }

}