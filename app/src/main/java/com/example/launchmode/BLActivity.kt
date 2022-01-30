package com.example.launchmode

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.test.R
import kotlinx.android.synthetic.main.activity_launch_mode.*

/**
 * @author wanglun
 * @date 2022/01/12
 * @description
 */
class BLActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch_mode)
        tvPage.text = "B"
    }
}
