package com.example.chart

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.test.R
import com.example.test.databinding.LineChartIndicatorBinding
import com.example.test.databinding.LineChartIndicatorItemBinding
import com.example.utils.dp2px
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight

/**
 * @author wanglun
 * @date 2022/11/14
 * @description chart点击时弹出的指示器
 */
class ChartFloatIndicator(
    private val context: Context,
) : PopupWindow(context, null, R.style.EmptyPopupWindowAnimator), LifecycleEventObserver {

    init {
        initView()
    }

    private lateinit var indicatorBinding: LineChartIndicatorBinding

    fun initView() {
        indicatorBinding = LineChartIndicatorBinding.inflate(LayoutInflater.from(context), null, false)
        contentView = indicatorBinding.root
        isOutsideTouchable = true
        animationStyle = R.style.EmptyPopupWindowAnimator
        setBackgroundDrawable(ColorDrawable(0x00000000))
    }

    fun updateView(chartView: View, chartModel: LineChartModel, e: Entry, h: Highlight) {
        indicatorBinding.apply {
            val position = e.x.toInt()
            indicatorTime.text = chartModel.timeList[position]

            // 判断是否要重新创建itemView，如果数量一致，则直接复用
            if (indicatorContentLayout.childCount != chartModel.dataSetList.size) {
                indicatorContentLayout.removeAllViews()
                repeat(chartModel.dataSetList.size) {
                    val itemBinding = LineChartIndicatorItemBinding.inflate(
                        LayoutInflater.from(indicatorBinding.root.context),
                        indicatorContentLayout, false)
                        .apply {
//                            ivLabelItem.setRoundCorner(0f, isOval = true)
                        }
                    indicatorContentLayout.addView(itemBinding.root)
                }
            }
            if (indicatorContentLayout.childCount != chartModel.dataSetList.size) {
                return
            }
            var maxWidthOfItems = dp2px(100f)
            chartModel.dataSetList.forEachIndexed { index, dataSetWrapper ->
                val itemView = indicatorContentLayout.getChildAt(index) ?: return
                val itemBinding = LineChartIndicatorItemBinding.bind(itemView)
                // item的布局方式要求动态计算整个父布局的宽度
                val label = chartModel.labelList.getOrNull(index).orEmpty()
                val value = dataSetWrapper.dataSet.getOrNull(position).orEmpty()
                val color = dataSetWrapper.lineColor
                val concatText = label + value
                // 30f = item 各view间的margin, padding和小圆点的宽度
                val itemMinWidth = itemBinding.tvLabelItem.paint.measureText(concatText) + dp2px(30f)
                maxWidthOfItems = maxWidthOfItems.coerceAtLeast(itemMinWidth.toInt())
                itemBinding.apply {
                    tvLabelItem.text = label
                    tvValueItem.text = value
                    ivLabelItem.setBackgroundColor(color)
                }
            }

            floatIndicator.post {
                val lp = floatIndicator.layoutParams
                lp.width = maxWidthOfItems
                floatIndicator.layoutParams = lp
            }

            // 确定indicator相对图表的位置
            val offsetLeft = 30
            val indicatorWidth = maxWidthOfItems
            val indicatorHeight = 50 * chartModel.dataSetList.size + 80
            // x左小右大，y上小下大
            var targetX = h.xPx
            var targetY = h.yPx
            android.util.Log.i("MPAndroidChart", "xPx: ${targetX}, yPx: ${targetY}")
            when {
                // 居右
                targetX + indicatorWidth + offsetLeft <= chartView.width -> {
                    targetX += offsetLeft
                }
                // 居左
                targetX + indicatorWidth + offsetLeft >= 0 -> {
                    targetX -= indicatorWidth + offsetLeft
                }
                // 居中
                else -> {
                    targetX -= indicatorWidth / 2
                }
            }
            if (targetX < 0) {
                targetX = 0f
            }
            when {
                targetY - indicatorHeight / 2 < 0 -> {
                }
                targetY + indicatorHeight / 2 > chartView.height -> {
                    targetY -= indicatorWidth
                }
                else -> {
                    targetY -= indicatorHeight / 2
                }
            }

            targetY -= chartView.height
            showAsDropDown(chartView, targetX.toInt(), targetY.toInt(), Gravity.BOTTOM or Gravity.START)
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            dismiss()
        }
    }
}