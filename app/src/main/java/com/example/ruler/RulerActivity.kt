package com.example.ruler

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.test.R
import kotlinx.android.synthetic.main.activity_ruler.*

/**
 * @author: wanglun
 * @date: 12/1/20
 * @desc: 用于测量屏幕尺寸
 */
class RulerActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ruler)
        initView()
    }

    private fun initView() {
        floatingBall.onMainButtonClick = {
            rulerView.switchMode()
        }

        floatingBall.setOnColorClick {
            rulerView.setPaintColor(it)
        }
    }
}