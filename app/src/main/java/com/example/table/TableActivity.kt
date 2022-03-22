package com.example.table

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bin.david.form.annotation.SmartColumn
import com.bin.david.form.core.SmartTable
import com.bin.david.form.core.TableConfig
import com.bin.david.form.data.CellInfo
import com.bin.david.form.data.column.Column
import com.bin.david.form.data.format.bg.BaseBackgroundFormat
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat
import com.bin.david.form.data.format.draw.IDrawFormat
import com.bin.david.form.data.style.FontStyle
import com.bin.david.form.data.table.ArrayTableData
import com.bin.david.form.utils.DensityUtils
import com.example.table.model.TableCell
import com.example.table.model.TableInfo
import com.example.table.viewbind.TextViewCell
import com.example.test.R
import kotlinx.android.synthetic.main.activity_table.*

/**
 * @author wanglun
 * @date 2022/02/10
 * @description
 */
class TableActivity : AppCompatActivity() {

    private val topHeaderList by lazy {
        listOf("日", "一", "二", "三", "四", "五", "六").map {
            createDefaultTableCell(it)
        }
    }

    private val leftHeaderList by lazy {
        listOf("工作时间", "工作量上限", "cc").map {
            createDefaultTableCell(it)
        }
    }

    private val tableContent by lazy {
        mutableListOf<TableCell<String>>().apply {
            for (i in leftHeaderList.indices) {
                for (j in topHeaderList.indices) {
                    add(createDefaultTableCell((i + j).toString(), i, j))
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table)
        tableLayout.initTabInfo(TableInfo(
            topHeader = topHeaderList,
            leftHeader = leftHeaderList,
            tableContent = tableContent
        ))
//        initSmartTable()
    }

    private fun createDefaultTableCell(
        str: String,
        row: Int = 0,
        column: Int = 0,
    ): TableCell<String> {
        return TableCell<String>(
            viewRes = R.layout.table_cell_textview,
            data = str,
            row = row,
            column = column,
            viewBind = defaultViewBind
        )
    }

    private val defaultViewBind = { tableCell: TableCell<String>, v: View ->
        TextViewCell.bind(tableCell, v)
    }

    private fun initSmartTable() {
        val table = findViewById<SmartTable<Schedule>>(R.id.smartTable)
        val scheduleList = listOf(
            Schedule(
                leftHeader = "工作时间",
                day1 = "08:00 ~ 22:00",
                day2 = "08:00 ~ 22:00",
                day3 = "-",
                day4 = "08:00 ~ 22:00",
                day5 = "-",
                day6 = "08:00 ~ 22:00",
                day7 = "08:00 ~ 22:00"
            ),
            Schedule(
                leftHeader = "工作量上限",
                day1 = "12",
                day2 = "6",
                day3 = "-",
                day4 = "无上限",
                day5 = "-",
                day6 = "无上限",
                day7 = "无上限"))
        table.config.apply {
            val titleBgColor = Color.parseColor("#F8F9FA")
            isShowTableTitle = false
            isShowXSequence = false
            isShowYSequence = false
            isFixedXSequence = true
            columnTitleBackground = BaseBackgroundFormat(titleBgColor)
            contentStyle = FontStyle(this@TableActivity, 12, Color.parseColor("#8F949C"))
            columnTitleStyle = FontStyle(this@TableActivity, 12, Color.parseColor("#8F949C"))
            columnTitleVerticalPadding = DensityUtils.dp2px(this@TableActivity, 8f)
            columnTitleHorizontalPadding = DensityUtils.dp2px(this@TableActivity, 12f)
            verticalPadding = DensityUtils.dp2px(this@TableActivity, 16f)
            horizontalPadding = DensityUtils.dp2px(this@TableActivity, 10f)
            leftAndTopBackgroundColor = Color.parseColor("#F8F9FA")
            contentCellBackgroundFormat = object: BaseCellBackgroundFormat<CellInfo<*>>() {
                override fun getBackGroundColor(cell: CellInfo<*>?): Int {
                    if (cell?.col == 0) {
                        return titleBgColor
                    }
                    return TableConfig.INVALID_COLOR
                }
            }
        }
        table.setData(scheduleList)
    }

    private fun initSmartTableWithArrayData() {
        val table = findViewById<SmartTable<Int>>(R.id.smartTable)
        val week = arrayOf("日", "一", "二", "三", "四", "五", "六")
        val infos = arrayOf(arrayOf(0, 1, 2, 1, 1, 0, 1, 1, 0, 1, 1, 2, 3),
            arrayOf(4, 2, 1, 1, 0, 1, 1, 0, 1, 1, 2, 2, 3),
            arrayOf(2, 2, 0, 1, 2, 4, 1, 0, 1, 3, 0, 1, 1),
            arrayOf(2, 1, 1, 0, 1, 4, 0, 1, 1, 2, 2, 0, 3),
            arrayOf(0, 1, 2, 4, 1, 0, 1, 4, 0, 1, 1, 2, 2),
            arrayOf(1, 0, 1, 3, 2, 2, 0, 1, 2, 1, 1, 0, 4),
            arrayOf(3, 1, 2, 4, 0, 1, 2, 1, 1, 0, 1, 1, 0))
        val tableData = ArrayTableData.create("日程表", week, infos, object : IDrawFormat<Int> {
            override fun measureWidth(
                column: Column<Int>?,
                position: Int,
                config: TableConfig?,
            ): Int {
                return DensityUtils.dp2px(this@TableActivity, 50f)
            }

            override fun measureHeight(
                column: Column<Int>?,
                position: Int,
                config: TableConfig?,
            ): Int {
                return DensityUtils.dp2px(this@TableActivity, 50f)
            }

            override fun draw(
                c: Canvas,
                rect: Rect,
                cellInfo: CellInfo<Int>,
                config: TableConfig,
            ) {
                val paint = config.paint
                val color = when (cellInfo.data) {
                    1 -> {
                        Color.GRAY
                    }
                    2 -> {
                        Color.BLUE
                    }
                    3 -> {
                        Color.CYAN
                    }
                    4 -> {
                        Color.MAGENTA
                    }
                    else -> {
                        Color.BLACK
                    }
                }
                paint.style = Paint.Style.FILL
                paint.color = color
                c.drawRect(rect.left + 5f, rect.top + 5f, rect.right - 5f, rect.bottom - 5f, paint)
            }
        })
        table.tableData = tableData
        table.config.apply {
            isShowXSequence = false
            isShowYSequence = false
            isFixedXSequence = true
        }
    }

    @com.bin.david.form.annotation.SmartTable()
    data class Schedule(
        @SmartColumn(id = 1, name = " ", fixed = true)
        val leftHeader: String,
        @SmartColumn(id = 2, name = "一")
        val day1: String,
        @SmartColumn(id = 3, name = "二")
        val day2: String,
        @SmartColumn(id = 4, name = "三")
        val day3: String,
        @SmartColumn(id = 5, name = "四")
        val day4: String,
        @SmartColumn(id = 6, name = "五")
        val day5: String,
        @SmartColumn(id = 7, name = "六")
        val day6: String,
        @SmartColumn(id = 8, name = "日")
        val day7: String,
    )
}
