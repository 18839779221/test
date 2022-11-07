package com.example.recyclerview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.R
import kotlinx.android.synthetic.main.activity_recyclerview.*

/**
 * @author wanglun
 * @date 2021/09/26
 * @description
 */
class RecyclerViewActivity : AppCompatActivity() {

    private val contentAdapter = ContentAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)
        initRecyclerView()
    }


    private fun initRecyclerView() {
        rvContent.apply {
            adapter = contentAdapter
            layoutManager = LinearLayoutManager(context)
        }
        contentAdapter.submitList(listOf(
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", ""),
            User("1", ""),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom")


        ))

    }


}