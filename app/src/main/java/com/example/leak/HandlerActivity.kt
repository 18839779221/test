package com.example.leak

import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import com.example.test.R
import kotlinx.android.synthetic.main.activty_handler.*
import kotlin.concurrent.thread

/**
 * @author wanglun
 * @date 2022/06/08
 * @description https://cloud.tencent.com/developer/article/1800399
 */
class HandlerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activty_handler)
//        leakCase1()
        leakCase2()
    }

    private fun leakCase1() {
        //发送延迟消息
        mHandler.sendEmptyMessageDelayed(0, 20000)

        btn1.setOnClickListener {
            finish()
        }
    }

    private fun leakCase2() {
        //运行中的子线程
        thread {
            Thread.sleep(20000)
            btn1.text
        }

        btn1.setOnClickListener {
            finish()
        }
    }

    val mHandler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            btn1.setText("2222")
        }
    }
}