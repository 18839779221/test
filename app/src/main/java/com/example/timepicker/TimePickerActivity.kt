package com.example.timepicker

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.test.R
import com.loper7.date_time_picker.number_picker.NumberPicker


/**
 * @author wanglun
 * @date 2022/09/07
 * @description
 */
class TimePickerActivity : AppCompatActivity() {

    companion object {
        val TAG = TimePickerActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_picker)
        initView()
    }

    private fun initView() {
        findViewById<NumberPicker>(R.id.datetime_hour).apply {
            setFormatter { "${it}点" }
            minValue = 0
            maxValue = 24
        }
        findViewById<NumberPicker>(R.id.datetime_minute).apply {
            displayedValues = arrayOf("00分", "30分")
            maxValue = 1
            minValue = 0
        }
        setTopTips()


    }

    private fun setTopTips() {
        val desc = "descinfo"
        val buttonTxt = "btn"
        val lenDesc = desc.length
        val lenBtn = buttonTxt.length
        val spannable = SpannableStringBuilder(desc + buttonTxt)

        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(this@TimePickerActivity, "click", Toast.LENGTH_SHORT).show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.BLUE
                ds.isUnderlineText = false
            }
        }, lenDesc, (lenDesc + lenBtn), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        findViewById<TextView>(R.id.tv_test).apply {
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = 0
            text = spannable
        }
    }


}