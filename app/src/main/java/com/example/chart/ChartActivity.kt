package com.example.chart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.test.databinding.ActivityChartBinding

/**
 * @author wanglun
 * @date 2022/11/03
 * @description
 */
class ChartActivity : AppCompatActivity() {

    private val chartHelper by lazy { ChartHelper() }

    private lateinit var binding: ActivityChartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        chartHelper.initChart(binding.chartView, binding.chartIndicator,
            LineChartModel(
                listOf(
                    listOf("100", "200", "400", "200", "500"),
                    listOf("100", "30", "70", "20", "10")
                ),
                listOf("10:00", "11:00", "12:00", "13:00", "14:00"),
                listOf("现金消耗", "转化成本")
            ))

        binding.chartView.postDelayed({
            chartHelper.updateChart(binding.chartView,
                LineChartModel(
                    listOf(
                        listOf("100", "30", "70", "20", "10"),
                        listOf("1", "2", "5", "6", "5"),
                    ),
                    listOf("10:00", "11:00", "12:00", "13:00", "14:00"),
                    listOf("转化成本", "点击转化")
                ))
        }, 3000)

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