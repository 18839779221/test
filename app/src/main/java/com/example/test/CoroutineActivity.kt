package com.example.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * @author wanglun
 * @date 2022/05/05
 * @description
 */
class CoroutineActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startCoroutine()
    }

    private fun startCoroutine() {
        lifecycleScope.launch {
            throw java.lang.NullPointerException()
            println("funTest")
            suspendFun1()
            suspendFun2()
        }
    }

    // 挂起函数
    suspend fun suspendFun1() {
        println("suspendFun1")
    }
    // 挂起函数
    suspend fun suspendFun2() {
        println("suspendFun2")
    }
}