package com.example.coordinatorlayout.multinested

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.R
import kotlinx.android.synthetic.main.activity_multi_nested.*

/**
 * @author wanglun
 * @date 2021/09/26
 * @description
 */
class MultiNestedActivity: AppCompatActivity() {

    private val topAdapter = TopAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_nested)
        initView()
    }

    private fun initView() {
        rvTop.apply {
            adapter = topAdapter
            layoutManager = LinearLayoutManager(context)
        }
        supportFragmentManager.commit {
            add(R.id.fcvBottom, BottomFragment.newInstance())
        }

    }


}