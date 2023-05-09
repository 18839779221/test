package com.example.textview

import android.graphics.Rect
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ImageSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.example.test.R
import com.example.utils.dp2px
import kotlinx.android.synthetic.main.activity_textview.*

class TextViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_textview)
        initTextView()
    }

    private fun initTextView() {
        val spannableStringBuilder = SpannableStringBuilder("hello你好啊")
        val drawable = AppCompatResources.getDrawable(this, R.drawable.icon_new)
        if (drawable != null) {
            drawable.bounds = Rect(0, 0, dp2px(42f), dp2px(18f))
            val imageSpan = ImageSpan(drawable, ImageSpan.ALIGN_BASELINE)
            spannableStringBuilder.setSpan(imageSpan, 0, 0, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }
        tv_test.post {
            if (!TextUtils.isEmpty(spannableStringBuilder)) {
                tv_test.text = spannableStringBuilder
            }
        }
    }
}