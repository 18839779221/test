package com.example.chart

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.test.R
import com.example.test.databinding.ChartLegendItemBinding
import com.example.utils.dp2px
import com.example.utils.setRoundCorner

/**
 * @author wanglun
 * @date 2022/11/15
 * @description 为解决chart的legend自定义的需求
 * 1. legend一行两列
 * 2. 当最后一行只有一个元素时，居中
 * 3. 当一行中有文本超过12个字(长度超标)，则图例整体左右居中展示
 */
class LegendView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : ViewGroup(context, attrs, defStyleAttr) {

    private var itemSpace: Int = 0
    private var legends = listOf<Pair<Int, String>>() // FormColor to LegendText
    private val itemHeight = dp2px(20f)
    private val horizontalPadding = dp2px(16f)

    fun setLegends(legends: List<Pair<Int, String>>, itemSpace: Int) {
        this.legends = legends
        this.itemSpace = itemSpace

        removeAllViews()
        legends.forEach { pair ->
            val itemBinding = ChartLegendItemBinding.inflate(LayoutInflater.from(context), this, false).apply {
                ivLegend.setBackgroundColor(pair.first)
                ivLegend.setRoundCorner(0f, isOval = true)
                tvLegend.text = pair.second
            }
            addView(itemBinding.root)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measuredWidth = MeasureSpec.getSize(widthMeasureSpec)
        val count = childCount
        for (i in 0 until count) {
            val isLeft = i % 2 == 0
            val child = getChildAt(i)
            child.measure(
                MeasureSpec.makeMeasureSpec(measuredWidth - itemSpace - horizontalPadding, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(itemHeight, MeasureSpec.EXACTLY))

            // 如果左右两边的宽度都超标，则按左右各一半重新测量一次，防止整体超长
            if (!isLeft && (child.measuredWidth + getChildAt(i - 1).measuredWidth
                        > measuredWidth - itemSpace - horizontalPadding)
            ) {
                arrayOf(getChildAt(i - 1), child).forEach {
                    it.measure(
                        MeasureSpec.makeMeasureSpec((measuredWidth - itemSpace) / 2 - horizontalPadding, MeasureSpec.AT_MOST),
                        MeasureSpec.makeMeasureSpec(itemHeight, MeasureSpec.EXACTLY))
                }
            }

        }
        val itemMeasureHeight = getChildAt(0)?.measuredHeight ?: 0
        val measuredHeight = itemMeasureHeight * ((childCount + 1) / 2)
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val count = childCount

        var childTop = 0
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility != GONE) {
                val width = child.measuredWidth
                val height = child.measuredHeight
                var childLeft = 0
                val isLeft = i % 2 == 0
                val lastRowSingleChild = i == count - 1 && isLeft
                childLeft += if (lastRowSingleChild) {
                    measuredWidth / 2 - width / 2
                } else {
                    if (isLeft) {
                        measuredWidth / 2 - width - itemSpace / 2
                    } else {
                        measuredWidth / 2 + itemSpace / 2
                    }
                }

                if (!isLeft) {
                    val leftChild = getChildAt(i - 1)
                    val overSizeCount = arrayOf(leftChild, child).count {
                        it.findViewById<TextView>(R.id.tv_legend).text.length > 12
                    }
                    // 左右是否整体居中
                    val alignCenterWhole = overSizeCount == 1
                    if (alignCenterWhole) {
                        val leftChildLeft = ((measuredWidth - leftChild.measuredWidth - width - itemSpace) / 2).coerceAtLeast(0)
                        leftChild.layout(leftChildLeft, childTop, leftChildLeft + leftChild.measuredWidth, childTop + height)
                        childLeft = leftChildLeft + leftChild.measuredWidth + itemSpace
                    }
                }

                child.layout(childLeft, childTop, childLeft + width, childTop + height)
                if (!isLeft) {
                    childTop += height
                }
            }
        }
    }
}