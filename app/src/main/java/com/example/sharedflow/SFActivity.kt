package com.example.sharedflow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.R
import kotlinx.android.synthetic.main.activity_sf.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * @author wanglun
 * @date 2021/07/23
 * @description
 */
class SFActivity: AppCompatActivity() {

    private val sfViewModel = SFViewModel()

    private val adapter = SFAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sf)
        initView()
        //initLoadState()
//        initData()

    }

    private fun initLoadState() {
        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest {
                when(it.refresh){
                    is LoadState.Loading -> {

                    }
                    is LoadState.Error -> {

                    }
                    is LoadState.NotLoading -> {

                    }
                }
            }
        }

    }

    private fun initView() {
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this)
    }

    private fun initData() {
        lifecycleScope.launch {
            sfViewModel.list.collect{
                adapter.submitData(it)
            }
//            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
//
//            }
        }
    }
}